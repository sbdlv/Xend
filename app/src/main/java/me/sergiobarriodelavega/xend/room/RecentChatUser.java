package me.sergiobarriodelavega.xend.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "recent_chat_users")
public class RecentChatUser {

    @Ignore
    public RecentChatUser(@NonNull String jid) {
        this.jid = jid;
    }

    public RecentChatUser(@NonNull String jid, String lastMsg, @NonNull Date date) {
        this.jid = jid;
        this.lastMsg = lastMsg;
        this.date = date;
    }

    @PrimaryKey
    @NonNull
    public String jid;

    public String lastMsg;

    @NonNull
    public Date date;
}
