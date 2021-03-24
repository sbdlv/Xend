package me.sergiobarriodelavega.xend;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import me.sergiobarriodelavega.xend.recyclers.MessageAdapter;

public class ChatActivity extends AppCompatActivity implements IncomingChatMessageListener {

    private ArrayList<Message> messages;
    private MessageAdapter messageAdapter;
    private IncomingChatMessageListener incomingChatMessageListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        incomingChatMessageListener = this;

        //TODO Messages should come from bundle
        messages = new ArrayList<>();

        Message msgTest = new Message();
        msgTest.setBody("Este es un mensaje local de prueba");
        messages.add(msgTest);

        RecyclerView rvMessages = findViewById(R.id.rvMessages);

        messageAdapter = new MessageAdapter(messages);

        rvMessages.setAdapter(messageAdapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));



        //TODO: Temporal connection and chat reciver
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try  {
                    AbstractXMPPConnection connection;
                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword("sergio", "usuario")
                            .setHostAddress(InetAddress.getByName("192.168.1.97"))
                            .setXmppDomain("xend")
                            .setPort(5222)
                            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                            .build();

                    connection = new XMPPTCPConnection(config);
                    connection.connect().login();

                    Toast.makeText(getApplicationContext(), "Conexion realizada", Toast.LENGTH_LONG).show();


                    //Chat
                    ChatManager chatManager = ChatManager.getInstanceFor(connection);
                    chatManager.addIncomingListener(incomingChatMessageListener);

                } catch (SmackException.EndpointConnectionException | UnknownHostException | XmppStringprepException e) {
                    Toast.makeText(getApplicationContext(), "No se pudo realizar la conexion", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getApplicationContext(), message.getBody(), Toast.LENGTH_LONG).show();
                System.out.println("Msg recivido: " + message.getBody());
                messages.add(message);
                messageAdapter.notifyDataSetChanged();
            }
        });

    }
}
