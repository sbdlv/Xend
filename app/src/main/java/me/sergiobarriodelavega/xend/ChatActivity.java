package me.sergiobarriodelavega.xend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import me.sergiobarriodelavega.xend.entities.XMPPUser;
import me.sergiobarriodelavega.xend.recyclers.MessageAdapter;

public class ChatActivity extends AppCompatActivity implements IncomingChatMessageListener, TextView.OnEditorActionListener {

    private ArrayList<Message> messages;
    private MessageAdapter messageAdapter;
    private IncomingChatMessageListener incomingChatMessageListener;
    private EditText txtChat;
    private Chat chat;
    private XMPPUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        incomingChatMessageListener = this;

        user = (XMPPUser) getIntent().getSerializableExtra("user");

        //TODO Old messages should be loaded if exists
        messages = new ArrayList<>();

        //Toolbar
        getSupportActionBar().setTitle(user.getUserName()); //TODO Should load full name from vCard
        getSupportActionBar().setSubtitle(user.getJid());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Temporal: For testing purposes
        Message msgTest = new Message();
        msgTest.setBody("Este es un mensaje local de prueba");
        messages.add(msgTest);

        //Find views
        txtChat = findViewById(R.id.txtChat);
        RecyclerView rvMessages = findViewById(R.id.rvMessages);

        //Chat message
        txtChat.setOnEditorActionListener(this);

        //Recycler
        messageAdapter = new MessageAdapter(messages);

        rvMessages.setAdapter(messageAdapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try  {
                    //Chat
                    ChatManager chatManager = ChatManager.getInstanceFor(App.getConnection());
                    chatManager.addIncomingListener(incomingChatMessageListener);

                    EntityBareJid jid = JidCreate.entityBareFrom(user.getJid());
                    chat = chatManager.chatWith(jid);

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
                messages.add(message);
                messageAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if(i == EditorInfo.IME_ACTION_DONE){
            sendMessage(null);
        }
        return false;
    }

    public void sendMessage(View view){
        try {
            chat.send(txtChat.getText());
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean showProfileInfo(MenuItem item){
        Intent i = new Intent(getApplicationContext(), ProfileInfoActivity.class);
        i.putExtra("user", user);
        startActivity(i);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
