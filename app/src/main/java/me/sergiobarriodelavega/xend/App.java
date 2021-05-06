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
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.impl.JidCreate;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

import me.sergiobarriodelavega.xend.entities.XMPPUser;
import me.sergiobarriodelavega.xend.room.XendDatabase;

public class App {
    private static AbstractXMPPConnection connection;
    private static XendDatabase db;
    private static Context context;
    private static HashMap<String, VCard> users = new HashMap<>();
    private static VCardManager vCardManager;

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

        //If all went well
        if (connection != null)
        connection.disconnect();

        connection = temporalConnection;

        //If successful connection, save preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_xmpp_config), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username",username);
        editor.putString("password",password);
        editor.putString("domain",domain);
        editor.putString("address",address);
        editor.putBoolean("hasSetup", true);
        editor.commit();
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
    public static VCard getXMPPUser(String jid) throws InterruptedException, XMPPException, SmackException, IOException {
        //If user is not saved on the map, make query
        if(users.containsKey(jid)){
            return users.get(jid);
        }

        VCard vCard = getvCardManager().loadVCard(JidCreate.entityBareFrom(jid));
        users.put(jid, vCard);
        return vCard;
    }
}
