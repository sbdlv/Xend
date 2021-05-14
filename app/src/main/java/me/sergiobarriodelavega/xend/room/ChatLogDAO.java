package me.sergiobarriodelavega.xend.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatLogDAO {

    @Query("SELECT * FROM ChatLog WHERE remoteJID LIKE :xmppDomain AND localJID = :localJID GROUP BY remoteJID ORDER BY date ASC")
    public List<ChatLog> getAllLastLogs(String xmppDomain, String localJID);

    @Query("SELECT * FROM ChatLog WHERE remoteJID = :remoteJID AND localJID = :localJID ORDER BY date ASC")
    public List<ChatLog> getLogForRemoteUser(String remoteJID, String localJID);

    @Query("SELECT * FROM ChatLog WHERE remoteJID = :jid AND localJID = :localJID ORDER BY date ASC LIMIT 1")
    public ChatLog getLastMsg(String jid, String localJID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(ChatLog chatLog);

    @Query("DELETE FROM ChatLog")
    public void deleteAll();
}
