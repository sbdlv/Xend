package me.sergiobarriodelavega.xend;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;

import java.io.IOException;
import java.net.InetAddress;

import me.sergiobarriodelavega.xend.room.ChatLog;
import me.sergiobarriodelavega.xend.room.ChatLogDAO;


public class XendService extends Service {
    private static final String TAG ="XEND_SERVICE";
    private IBinder mBinder = new XendBinder();
    private AbstractXMPPConnection abstractXMPPConnection;
    private ChatLogDAO chatLogDAO;
    private XendConnectionListener xendConnectionListener;


    //Incoming chat notifications
    private IncomingChatMessageListener incomingChatMessageListener;
    private ChatManager chatManager;
    private NotificationManagerCompat notificationManagerCompat;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class XendBinder extends Binder{
        XendService getService(){
            return XendService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "On start command");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("XEND Service")
                .setContentText(null)
                .setSmallIcon(R.drawable.ic_xend_icon_white_no_bg)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        chatLogDAO = App.getDb(getApplicationContext()).chatLogDAO();

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Created XEND SERVICE");
        xendConnectionListener = new XendConnectionListener();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"Destroyed XEND Service");
        super.onDestroy();
    }

    public AbstractXMPPConnection getAbstractXMPPConnection() {
        return abstractXMPPConnection;
    }

    public void makeConnectionFromConfig() throws IOException, InterruptedException, XMPPException, SmackException {
        String username, password, address, domain;
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.preferences_xmpp_config), Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username",null);
        password = sharedPreferences.getString("password",null);
        domain = sharedPreferences.getString("domain",null);
        address = sharedPreferences.getString("address",null);

        Log.d(TAG, "Making XMPP Connection");
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword(username, password)
                    .setHostAddress(InetAddress.getByName(address))
                    .setXmppDomain(domain)
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();

            abstractXMPPConnection = new XMPPTCPConnection(config);
            abstractXMPPConnection.addConnectionListener(xendConnectionListener);
            abstractXMPPConnection.connect().login();


        Log.d(TAG, "XMPP Connection successful");

        createChatManager();

        //Notify Connection to Splash
        Intent notifyConnection = new Intent(LocalBroadcastsEnum.SUCCESSFUL_CONNECTION);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(notifyConnection);

        App.localJID = getAbstractXMPPConnection().getUser().asBareJid().toString();
    }

    /**
     * Tries a new connection, if successful, replace actual one for this one and save config on shared preferences
     * @param username
     * @param password
     * @param address
     * @param domain
     * @throws IOException
     * @throws InterruptedException
     * @throws XMPPException
     * @throws SmackException
     */
    public void tryNewConnection(String username, String password, String address, String domain) throws IOException, InterruptedException, XMPPException, SmackException {
        AbstractXMPPConnection temporalConnection;

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(username, password)
                .setHostAddress(InetAddress.getByName(address))
                .setXmppDomain(domain)
                .setPort(5222)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .build();


        temporalConnection = new XMPPTCPConnection(config);
        temporalConnection.connect().login();

        //If connections is OK, then replace the old one for the new one
        if(abstractXMPPConnection != null){
            abstractXMPPConnection.disconnect();
            abstractXMPPConnection.removeConnectionListener(xendConnectionListener);
        }

        abstractXMPPConnection = temporalConnection;
        abstractXMPPConnection.addConnectionListener(xendConnectionListener);

        //Remove old Chat Manager and create a new one
        createChatManager();

        //Save new Config
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.preferences_xmpp_config), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("username",username);
        editor.putString("password",password);
        editor.putString("domain",domain);
        editor.putString("address",address);
        editor.putBoolean("hasSetup", true);
        editor.commit();

        App.localJID = getAbstractXMPPConnection().getUser().asBareJid().toString();
    }

    /**
     * Creates the Chat Manager for the actual connection. Also removes any old listener in case they exist
     */
    private void createChatManager(){
        //Remove old listener if exists
        if(incomingChatMessageListener != null){
            chatManager.removeIncomingListener(incomingChatMessageListener);
        }

        //Create listener for incoming chats
        chatManager = ChatManager.getInstanceFor(abstractXMPPConnection);
        notificationManagerCompat = NotificationManagerCompat.from(this);
        incomingChatMessageListener = new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                int notificationID;
                //Check if the user is already chatting with the person in order to not show a notification
                if(App.onChatWith == null || !App.onChatWith.equals(from.asEntityBareJidString())) {
                    notificationID = getNotificationIDFromJID(from);

                    //Create intent to open ChatActivity
                    Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                    i.putExtra("user", from.toString());
                    i.putExtra("notificationID", notificationID);

                    // Create the TaskStackBuilder and add the intent, which inflates the back stack
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                    stackBuilder.addNextIntentWithParentStack(i);
                    // Get the PendingIntent containing the entire back stack
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    //Save message to DB
                    saveIncomingMessageToDB(message, from);

                    //Notification build
                    Bitmap userPicture = null;
                    try {
                        userPicture = App.avatarToBitmap(App.getUserVCard(from.toString()));
                        userPicture = App.getCircleBitmap(userPicture);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), App.CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_xend_icon_white_no_bg)
                            .setContentTitle(from)
                            .setContentText(message.getBody())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(resultPendingIntent)
                            .setLargeIcon(userPicture);

                    notificationManagerCompat.notify(notificationID, builder.build());
                }
            }
        };

        chatManager.addIncomingListener(incomingChatMessageListener);
    }


    /**
     * Generates an int from the users JID. Used for notifications id
     * @param jid The user JID
     * @return int value generated from the user JID characters
     */
    private int getNotificationIDFromJID(EntityBareJid jid){
        int id = 0;
        char[] chars = jid.toString().toCharArray();

        for (int i = 0; i< chars.length; i++){
            id += i + chars[i];
        }
        return id;
    }

    /**
     * Saves a incoming message to the DB, since the {@link ChatActivity} is not running and saving it
     * @param message The incoming message
     * @param from BareJID from the sender, cannot be local user
     */
    private void saveIncomingMessageToDB(Message message, EntityBareJid from){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChatLog chatLog = ChatLog.create(message.getBody(), from.asEntityBareJidString(), App.localJID, false);
                chatLogDAO.insert(chatLog);
            }
        }).start();
    }
}
