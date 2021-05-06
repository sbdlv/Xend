package me.sergiobarriodelavega.xend.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecentChatUsersDAO {

    @Query("SELECT * FROM recent_chat_users ORDER BY date DESC")
    public List<RecentChatUser> getAllLastChattedUsers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUser(RecentChatUser user);

    @Delete
    public void deleteUser(RecentChatUser user);

    @Query("DELETE FROM recent_chat_users")
    public void deleteAll();
}
