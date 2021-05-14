package me.sergiobarriodelavega.xend.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.util.Date;

@Entity(primaryKeys = {"remoteJID","localJID", "date","msg","isFromLocalUser"})
public class ChatLog {

    @NonNull
    public String remoteJID;

    @NonNull
    public String localJID;

    @NonNull
    public Date date;

    @NonNull
    public String msg;

    @NonNull
    public boolean isFromLocalUser;

    @Ignore
    public static ChatLog create(String msg, String remoteJID, String localJID, boolean isFromLocalUser){
        ChatLog chatLog = new ChatLog();

        chatLog.date = new Date();
        chatLog.msg = msg;
        chatLog.remoteJID = remoteJID;
        chatLog.localJID = localJID;
        chatLog.isFromLocalUser = isFromLocalUser;

        return chatLog;
    }
}
