package me.sergiobarriodelavega.xend.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "last_chatted_users")
public class LastChattedUser {

    @Ignore
    public LastChattedUser(@NonNull String jid) {
        this.jid = jid;
    }

    public LastChattedUser(@NonNull String jid, String lastMsg, @NonNull Date date) {
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
