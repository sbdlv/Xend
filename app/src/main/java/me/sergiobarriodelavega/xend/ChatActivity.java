package me.sergiobarriodelavega.xend;

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
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import me.sergiobarriodelavega.xend.recyclers.MessageAdapter;
import me.sergiobarriodelavega.xend.room.RecentChatUser;

public class ChatActivity extends AppCompatActivity implements IncomingChatMessageListener, OutgoingChatMessageListener, TextView.OnEditorActionListener {

    private ArrayList<Message> messages;
    private MessageAdapter messageAdapter;
    private EditText txtChat;
    private Chat chat;
    private String userJID;
    private RecentChatUser recentChatUser;
    private VCard remoteUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userJID = getIntent().getStringExtra("user");

        //Prevent showing chat notifications for this user
        App.onChatWith = userJID;

        //If the activity has been started from a notification, close the notification
        int notificationID = getIntent().getIntExtra("notificationID", -1);
        if(notificationID != -1){
            NotificationManagerCompat.from(this).cancel(notificationID);
        }

        recentChatUser = new RecentChatUser(userJID);

        //TODO Old messages should be loaded if exists
        messages = new ArrayList<>();

        //Toolbar
        try {
            remoteUser = App.getUserVCard(userJID);

            if(remoteUser.getFirstName() == null){
                Objects.requireNonNull(getSupportActionBar()).setTitle(userJID);
            } else {
                Objects.requireNonNull(getSupportActionBar()).setTitle(remoteUser.getFirstName()); //TODO Should load full name from vCard
                getSupportActionBar().setSubtitle(userJID);
            }

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //Find views
            txtChat = findViewById(R.id.txtChat);
            RecyclerView rvMessages = findViewById(R.id.rvMessages);

            //Chat message
            txtChat.setOnEditorActionListener(this);

            //Recycler
            try {
                messageAdapter = new MessageAdapter(messages, JidCreate.bareFrom(userJID));

                rvMessages.setAdapter(messageAdapter);
                rvMessages.setLayoutManager(new LinearLayoutManager(this));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try  {
                            //Chat
                            ChatManager chatManager = ChatManager.getInstanceFor(App.getConnection());
                            chatManager.addIncomingListener(ChatActivity.this);
                            chatManager.addOutgoingListener(ChatActivity.this);

                            EntityBareJid jid = JidCreate.entityBareFrom(userJID);
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

            } catch (XmppStringprepException e) {
                //Invalid Jid Format
                e.printStackTrace();
            }

        } catch (Exception e){

        }

    }

    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        runOnUiThread(() -> {
            messages.add(message);
            messageAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void newOutgoingMessage(EntityBareJid to, MessageBuilder messageBuilder, Chat chat) {
        Message n = messageBuilder.build();
        messages.add(messageBuilder.build());
        messageAdapter.notifyDataSetChanged();
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

            //Set msg and date
            recentChatUser.date = new Date();
            recentChatUser.lastMsg = txtChat.getText().toString();
            new Thread(() -> App.getDb(getApplicationContext()).lastChattedUsersDAO().insertUser(recentChatUser)).start();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean showProfileInfo(MenuItem item){
        Intent i = new Intent(getApplicationContext(), ProfileInfoActivity.class);
        i.putExtra("user", userJID);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.onChatWith = null;
    }
}
