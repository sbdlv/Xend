package me.sergiobarriodelavega.xend;

import android.app.Application;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.net.InetAddress;

public class App {
    private static AbstractXMPPConnection connection;

    public static AbstractXMPPConnection getConnection() throws InterruptedException, XMPPException, SmackException, IOException {
        if(connection == null){
            //TODO: Temporal connection
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword("sergio", "usuario")
                    .setHostAddress(InetAddress.getByName("192.168.1.97"))
                    .setXmppDomain("xend")
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();

            connection = new XMPPTCPConnection(config);
            connection.connect().login();
        }
        return connection;
    }
}
