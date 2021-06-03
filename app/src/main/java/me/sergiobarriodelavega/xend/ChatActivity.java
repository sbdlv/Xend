package me.sergiobarriodelavega.xend;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;

import me.sergiobarriodelavega.xend.listeners.ChatRecyclerListener;
import me.sergiobarriodelavega.xend.recyclers.MessageAdapter;
import me.sergiobarriodelavega.xend.room.ChatLog;
import me.sergiobarriodelavega.xend.room.ChatLogDAO;

public class ChatActivity extends AppCompatActivity implements IncomingChatMessageListener, TextView.OnEditorActionListener {
    private static final String TAG = "XEND_CHAT_ACTIVITY";

    private ArrayList<ChatLog> messages;
    private MessageAdapter messageAdapter;
    private EditText txtChat;
    private Chat chat;
    private String remoteJID;
    private VCard remoteUser;
    private ChatLogDAO chatLogDAO;
    private ChatManager chatManager;
    private ChatRecyclerListener chatRecyclerListener;
    private RecyclerView rvMessages;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rvMessages = findViewById(R.id.rvMessages);

        remoteJID = getIntent().getStringExtra("user");

        //Prevent showing chat notifications for this user
        App.onChatWith = remoteJID;

        //If the activity has been started from a notification, close the notification
        int notificationID = getIntent().getIntExtra("notificationID", -1);
        if(notificationID != -1){
            NotificationManagerCompat.from(this).cancel(notificationID);
        }

        //Toolbar
        try {
            remoteUser = App.getUserVCard(remoteJID);
            if(remoteUser.getFirstName() == null){
                Objects.requireNonNull(getSupportActionBar()).setTitle(remoteJID);
            } else {
                Objects.requireNonNull(getSupportActionBar()).setTitle(remoteUser.getFirstName()); //TODO Should load full name from vCard
                getSupportActionBar().setSubtitle(remoteJID);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new LoadChat().execute();

    }

    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        Log.d(TAG, "Saving incoming message");

        //Generate ChatLog obj
        ChatLog chatLog = ChatLog.create(message.getBody(), remoteJID, App.localJID, false);

        //Refresh Recycler
        messages.add(chatLog);

        runOnUiThread(() -> {
                messageAdapter.notifyDataSetChanged();
                if(chatRecyclerListener.scrollToBottom())
                rvMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
        });

        //Save on local DB
        new Thread(() -> chatLogDAO.insert(chatLog)).start();
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
            //Send msg
            chat.send(txtChat.getText());

            //Generate ChatLog
            ChatLog chatLog = ChatLog.create(txtChat.getText().toString(), remoteJID,App.localJID, true);

            //Refresh recycler
            messages.add(chatLog);
            messageAdapter.notifyDataSetChanged();
            rvMessages.scrollToPosition(messageAdapter.getItemCount() - 1);

            //Save on local DB
            Log.d(TAG, "Sending & saving new msg: " + txtChat.getText().toString());
            new Thread(() ->
                    chatLogDAO.insert(chatLog)
            ).start();

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean showProfileInfo(MenuItem item){
        Intent i = new Intent(getApplicationContext(), ProfileInfoActivity.class);
        i.putExtra("user", remoteJID);
        startActivity(i);
        return true;
    }

    public boolean deleteChat(MenuItem item){
        new Thread(new Runnable() {
            @Override
            public void run() {
                chatLogDAO.deleteChatWith(remoteJID, App.localJID);
            }
        }).start();
        messageAdapter.clearMessages();
        messageAdapter.notifyDataSetChanged();
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
        chatManager.removeIncomingListener(this);
        App.onChatWith = null;
    }


    /**
     * Load old chat records
     */
    private class LoadChat extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //Load old messages
            chatLogDAO = App.getDb(ChatActivity.this).chatLogDAO();
            messages = (ArrayList<ChatLog>) chatLogDAO.getLogForRemoteUser(remoteJID,App.localJID);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //Find views
            txtChat = findViewById(R.id.txtChat);

            //Chat message
            txtChat.setOnEditorActionListener(ChatActivity.this);

            //Recycler
            try {
                messageAdapter = new MessageAdapter(messages, JidCreate.bareFrom(remoteJID));

                rvMessages.setAdapter(messageAdapter);
                linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
                linearLayoutManager.setStackFromEnd(true);
                rvMessages.setLayoutManager(linearLayoutManager);
                chatRecyclerListener = new ChatRecyclerListener();
                rvMessages.addOnScrollListener(chatRecyclerListener);

                try  {
                    //Chat
                    chatManager = ChatManager.getInstanceFor(App.getConnection());
                    chatManager.addIncomingListener(ChatActivity.this);

                    EntityBareJid jid = JidCreate.entityBareFrom(remoteJID);
                    chat = chatManager.chatWith(jid);

                } catch (SmackException.EndpointConnectionException | UnknownHostException | XmppStringprepException e) {
                    Toast.makeText(getApplicationContext(), "Couldn't connect to the server", Toast.LENGTH_LONG).show();
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

            } catch (XmppStringprepException e) {
                //Invalid Jid Format
                e.printStackTrace();
            }

        }
    }
}
