package me.sergiobarriodelavega.xend;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.net.InetAddress;

import me.sergiobarriodelavega.xend.room.XendDatabase;

public class App {
    private static AbstractXMPPConnection connection;
    private static XendDatabase db;
    private static Context context;

    public static void init(Context context){
        App.context = context;
    }

    public static Context getContext() {
        return context;
    }

    public static AbstractXMPPConnection getConnection() throws InterruptedException, XMPPException, SmackException, IOException {
        if(connection == null || !connection.isAuthenticated()){
            makeNewConnection();
        }
        return connection;
    }

    public static void makeNewConnection() throws InterruptedException, XMPPException, SmackException, IOException {
        String username, password, address, domain;
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_xmpp_config), Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username",null);
        password = sharedPreferences.getString("password",null);
        domain = sharedPreferences.getString("domain",null);
        address = sharedPreferences.getString("address",null);

        makeNewConnection(username,password,address,domain);
    }

    public static void makeNewConnection(String username, String password, String address, String domain) throws InterruptedException, XMPPException, SmackException, IOException {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(username, password)
                .setHostAddress(InetAddress.getByName(address))
                .setXmppDomain(domain)
                .setPort(5222)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .build();

        connection = new XMPPTCPConnection(config);
        connection.connect().login();
    }

    public static XendDatabase getDb(Context context) {
        if(db == null){
            db = Room.databaseBuilder(context,
                    XendDatabase.class, "xend").build();
        }
        return db;
    }
}
