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
import java.util.Calendar;
import java.util.Date;

import me.sergiobarriodelavega.xend.R;
import me.sergiobarriodelavega.xend.room.ChatLog;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private ArrayList<ChatLog> messages;
    private Jid remoteUserJID;
    private static final int LOCAL_MSG = 0, REMOTE_MSG = 1;
    private static final SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm"), formatDate = new SimpleDateFormat("dd/MM/YYYY");

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
        ChatLog chatLog = messages.get(position), previousChatLog;
        holder.tvMessageText.setText(chatLog.msg);
        holder.tvMessageTime.setText(formatTime.format(chatLog.date));

        //Show date
        if(position != 0){
            previousChatLog = messages.get(position - 1);
            if(previousChatLog == null || !compareDates(chatLog.date, previousChatLog.date)){
                holder.tvDate.setText(formatDate.format(chatLog.date));
            } else {
                holder.tvDate.setVisibility(View.GONE);
            }
        } else {
            holder.tvDate.setText(formatDate.format(chatLog.date));
        }

    }

    private static boolean compareDates(Date date1, Date date2){
        Calendar cal1, cal2;
        cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.DAY_OF_MONTH) ==cal2.get(Calendar.DAY_OF_MONTH) &&
                cal1.get(Calendar.MONTH) ==cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.YEAR) ==cal2.get(Calendar.YEAR);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMessageText, tvMessageTime, tvDate;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvMessageText = view.findViewById(R.id.tvMessageText);
            tvMessageTime = view.findViewById(R.id.tvMessageTime);
            tvDate= view.findViewById(R.id.tvDate);
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

    public void clearMessages(){
        messages.clear();
    }
}
