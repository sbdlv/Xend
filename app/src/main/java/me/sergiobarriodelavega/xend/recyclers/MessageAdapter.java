package me.sergiobarriodelavega.xend.recyclers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jxmpp.jid.Jid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import me.sergiobarriodelavega.xend.R;
import me.sergiobarriodelavega.xend.room.ChatLog;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private ArrayList<ChatLog> messages;
    private Jid remoteUserJID;
    private static final int LOCAL_MSG = 0, REMOTE_MSG = 1;
    private static final SimpleDateFormat formatTime = new SimpleDateFormat("H:mm");

    public MessageAdapter(ArrayList<ChatLog> messages, Jid remoteUserJID) {
        this.remoteUserJID = remoteUserJID;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutViewHolderResID;

        // Create a new view, which defines the UI of the list item
        if(viewType == LOCAL_MSG){
            layoutViewHolderResID = R.layout.viewholder_local_message;
        } else {
            layoutViewHolderResID = R.layout.viewholder_message;
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutViewHolderResID, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatLog chatLog = messages.get(position);
        holder.tvMessageText.setText(chatLog.msg);
        holder.tvMessageTime.setText(formatTime.format(chatLog.date));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMessageText, tvMessageTime;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvMessageText = view.findViewById(R.id.tvMessageText);
            tvMessageTime = view.findViewById(R.id.tvMessageTime);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //Check BareJIDs to specify the message type
        if(messages.get(position).isFromLocalUser){
            return LOCAL_MSG;
        }
        return REMOTE_MSG;
    }
}
