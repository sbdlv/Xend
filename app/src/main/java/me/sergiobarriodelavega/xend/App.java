package me.sergiobarriodelavega.xend;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.android.AndroidSmackInitializer;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.impl.JidCreate;

import java.io.IOException;

import me.sergiobarriodelavega.xend.room.XendDatabase;

public class App extends Application{
    private static XendDatabase db;
    private static VCardManager vCardManager;
    private static final String TAG = "XEND_APP";
    public static final String CHANNEL_ID = "xendServiceChannel";
    public static String onChatWith;

    //Bound service
    private static XendService xendService;
    private static boolean bound;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.d(TAG, "Service bounded");

            //We've bound to LocalService, cast the IBinder and get LocalService instance
            XendService.XendBinder binder = (XendService.XendBinder) service;
            xendService = binder.getService();
            bound = true;

            //Check if a setup is configured
            SharedPreferences s = getApplicationContext().getSharedPreferences(getString(R.string.preferences_xmpp_config), MODE_PRIVATE);

            if(s.getBoolean("hasSetup", false)){
                //Thread in order to not freeze the Splash Screen
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Make connection. Once its made, launch Main Activity
                            xendService.makeConnectionFromConfig();
                        } catch (Exception e){
                            e.printStackTrace();
                            //TODO: Manage reconnect dialog
                            //new AlertDialog.Builder(getApplicationContext()).setMessage("Error XMPP Connection").create().show();
                        }

                    }
                }).start();

            } else {
                //Notify Splash to launch the SetupWizard
                Intent startWizard = new Intent(LocalBroadcastsEnum.START_SETUP_WIZARD);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(startWizard);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

        //Smack
        AndroidSmackInitializer.initialize(getApplicationContext());

        //Service Notification
        createNotificationChannel();
        Intent i = new Intent(this, XendService.class);

        if(!isBound()){
            startService(i);
            bindService(i, connection, BIND_AUTO_CREATE);
        }

        Utils.context = getApplicationContext();

        Log.d(TAG,"App created");
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "XEND Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public static boolean isBound() {
        return bound;
    }

    public static void tryNewConnection(String username, String password, String address, String domain) throws IOException, InterruptedException, XMPPException, SmackException{
        xendService.tryNewConnection(username,password,address,domain);
    }

    public static AbstractXMPPConnection getConnection() throws InterruptedException, XMPPException, SmackException, IOException {
        return xendService.getAbstractXMPPConnection();
    }

    private static VCardManager getvCardManager() throws InterruptedException, IOException, SmackException, XMPPException {
        if(vCardManager == null){
            vCardManager = VCardManager.getInstanceFor(getConnection());
        }
        return vCardManager;
    }

    public static XendDatabase getDb(Context context) {
        if(db == null){
            db = Room.databaseBuilder(context,
                    XendDatabase.class, "xend").build();
        }
        return db;
    }

    /**
     * Gets a VCard by JID
     * @param jid
     * @return
     */
    public static VCard getUserVCard(String jid) throws InterruptedException, XMPPException, SmackException, IOException {
        return getvCardManager().loadVCard(JidCreate.entityBareFrom(jid));
    }

    /**
     * Converts the byte[] from vcard´s getAvatar() to a Bitmap
     * @param vCard
     * @return
     */
    public static Bitmap avatarToBitmap(VCard vCard){
        byte[] userPictureRaw = vCard.getAvatar();

        if(userPictureRaw != null){
            return BitmapFactory.decodeByteArray(userPictureRaw, 0, userPictureRaw.length);
        }

        return null;
    }
}
