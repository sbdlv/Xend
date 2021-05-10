package me.sergiobarriodelavega.xend.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import org.bouncycastle.crypto.engines.ISAACEngine;
import org.jivesoftware.smack.packet.Message;

import java.util.Date;

@Entity(primaryKeys = {"remoteJID", "date","msg","isFromLocalUser"})
public class ChatLog {

    @NonNull
    public String remoteJID;

    @NonNull
    public Date date;

    @NonNull
    public String msg;

    @NonNull
    public boolean isFromLocalUser;

    @Ignore
    public static ChatLog fromMessage(Message message, String remoteJID){
        ChatLog chatLog = new ChatLog();

        chatLog.date = new Date();
        chatLog.msg = message.getBody();
        chatLog.remoteJID = remoteJID;
        chatLog.isFromLocalUser = remoteJID.equals(message.getTo().asBareJid().toString());

        return chatLog;
    }

    @Ignore
    public static ChatLog fromString(String msg, String remoteJID, boolean isFromLocalUser){
        ChatLog chatLog = new ChatLog();

        chatLog.date = new Date();
        chatLog.msg = msg;
        chatLog.remoteJID = remoteJID;
        chatLog.isFromLocalUser = isFromLocalUser;

        return chatLog;
    }
}
