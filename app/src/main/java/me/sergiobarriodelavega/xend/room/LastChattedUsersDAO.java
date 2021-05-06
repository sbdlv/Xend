package me.sergiobarriodelavega.xend.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LastChattedUsersDAO {

    @Query("SELECT * FROM last_chatted_users ORDER BY date DESC")
    public List<LastChattedUser> getAllLastChattedUsers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertUser(LastChattedUser user);

    @Delete
    public void deleteUser(LastChattedUser user);

    @Query("DELETE FROM last_chatted_users")
    public void deleteAll();
}
