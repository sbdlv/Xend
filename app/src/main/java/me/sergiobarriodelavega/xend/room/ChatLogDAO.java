package me.sergiobarriodelavega.xend.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatLogDAO {

    @Query("SELECT * FROM ChatLog WHERE remoteJID LIKE :xmppDomain GROUP BY remoteJID ORDER BY date ASC")
    public List<ChatLog> getAllLastLogs(String xmppDomain);

    @Query("SELECT * FROM ChatLog WHERE remoteJID = :jid ORDER BY date ASC")
    public List<ChatLog> getLogForRemoteUser(String jid);

    @Query("SELECT * FROM ChatLog WHERE remoteJID = :jid ORDER BY date ASC LIMIT 1")
    public ChatLog getLastMsg(String jid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(ChatLog chatLog);

    @Query("DELETE FROM ChatLog")
    public void deleteAll();
}
