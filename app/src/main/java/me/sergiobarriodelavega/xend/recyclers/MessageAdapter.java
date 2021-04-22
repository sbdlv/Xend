package me.sergiobarriodelavega.xend.recyclers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.Jid;

import java.util.ArrayList;

import me.sergiobarriodelavega.xend.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private ArrayList<Message> messages;
    private Jid localUserJID;

    public MessageAdapter(ArrayList<Message> messages, Jid localUserJID) {
        this.messages = messages;
        this.localUserJID = localUserJID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Message msg = messages.get(viewType);
        int layoutViewHolderResID = R.layout.viewholder_message;

        // Create a new view, which defines the UI of the list item
        if(msg.getFrom().equals(localUserJID)){
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
}
