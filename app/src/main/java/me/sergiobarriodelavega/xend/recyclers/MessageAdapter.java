package me.sergiobarriodelavega.xend.recyclers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.Jid;

import java.util.ArrayList;

import me.sergiobarriodelavega.xend.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private ArrayList<Message> messages;
    private Jid remoteUserJID;
    private static final int LOCAL_MSG = 0, REMOTE_MSG = 1;

    public MessageAdapter(ArrayList<Message> messages, Jid remoteUserJID) {
        this.messages = messages;
        this.remoteUserJID = remoteUserJID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutViewHolderResID;

        // Create a new view, which defines the UI of the list item
        if(viewType == LOCAL_MSG){
            layoutViewHolderResID = R.layout.viewholder_message;
        } else {
            layoutViewHolderResID = R.layout.viewholder_local_message;
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutViewHolderResID, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = messages.get(position);
        holder.tvMessageText.setText(msg.getBody());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMessageText;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvMessageText = view.findViewById(R.id.tvMessageText);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getTo().equals(remoteUserJID)){
            return REMOTE_MSG;
        }
        return LOCAL_MSG;
    }
}
