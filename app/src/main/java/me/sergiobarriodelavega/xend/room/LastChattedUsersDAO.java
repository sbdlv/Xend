package me.sergiobarriodelavega.xend.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LastChattedUsersDAO {

    @Query("SELECT jid FROM last_chatted_users")
        public List<String> getAllLastChattedUsers();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertUser(LastChattedUser user);

    @Delete
    public void deleteUser(LastChattedUser user);
}
