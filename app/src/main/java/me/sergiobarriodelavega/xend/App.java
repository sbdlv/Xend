package me.sergiobarriodelavega.xend;

import android.app.Application;
import android.content.Context;

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

    public static AbstractXMPPConnection getConnection() throws InterruptedException, XMPPException, SmackException, IOException {
        if(connection == null || !connection.isAuthenticated()){
            //TODO: Temporal connection
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword("sergio", "usuario")
                    .setHostAddress(InetAddress.getByName("45.91.64.21"))
                    .setXmppDomain("xend")
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();

            connection = new XMPPTCPConnection(config);
            connection.connect().login();
        }
        return connection;
    }

    public static XendDatabase getDb(Context context) {
        if(db == null){
            db = Room.databaseBuilder(context,
                    XendDatabase.class, "xend").build();
        }
        return db;
    }
}
