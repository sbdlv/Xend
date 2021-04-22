package me.sergiobarriodelavega.xend.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "last_chatted_users")
public class LastChattedUser {

    public LastChattedUser(String jid) {
        this.jid = jid;
    }

    @PrimaryKey
    @NonNull
    public String jid;
}
