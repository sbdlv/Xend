package me.sergiobarriodelavega.xend;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

public class XendConnectionListener implements ConnectionListener {
    private static final String TAG = "XEND_CONN_LISTENER";
    private static final int RETRY_CONNECTION_COOLDOWN = 1000;

    @Override
    public void connected(XMPPConnection connection) {
        Log.d(TAG, "Connection made successfully");
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Log.d(TAG, "Authenticated successfully");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.e(TAG, "XEND Connection lost on error:");
        e.printStackTrace();

        while (true){
            try {
                Log.d(TAG, "Attempt to reconnect...");
                App.getConnection().connect().login();
                break;
            } catch (Exception ex) {
                Log.e(TAG, "Attempt to reconnect FAILED");
                ex.printStackTrace();
                try {
                    Thread.sleep(RETRY_CONNECTION_COOLDOWN);
                } catch (InterruptedException exc) {
                    exc.printStackTrace();
                }
            }
        }

    }

    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
