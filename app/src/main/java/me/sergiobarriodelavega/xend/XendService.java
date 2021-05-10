package me.sergiobarriodelavega.xend;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class XendService extends Service {
    private static final String TAG ="XEND_SERVICE";
    private IBinder mBinder = new XendBinder();
    private Handler mHandler;
    private AbstractXMPPConnection abstractXMPPConnection;

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
                .setContentTitle("Example Service")
                .setContentText(null)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Created XEND SERVICE");
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
            abstractXMPPConnection.connect().login();


        Log.d(TAG, "XMPP Connection successful");

        //Notify Connection to Splash
        Intent notifyConnection = new Intent(LocalBroadcastsEnum.SUCCESSFUL_CONNECTION);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(notifyConnection);
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
        //TODO: Remove chat listener and create new one
        if(abstractXMPPConnection != null){
            abstractXMPPConnection.disconnect();
        }

        abstractXMPPConnection = temporalConnection;

        //Save new Config
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.preferences_xmpp_config), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("username",username);
        editor.putString("password",password);
        editor.putString("domain",domain);
        editor.putString("address",address);
        editor.putBoolean("hasSetup", true);
        editor.commit();
    }
}
