package me.sergiobarriodelavega.xend.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {LastChattedUser.class}, version = 1)
public abstract class XendDatabase extends RoomDatabase {
    public abstract LastChattedUsersDAO lastChattedUsersDAO();
}
