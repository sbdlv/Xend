package me.sergiobarriodelavega.xend.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {ChatLog.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class XendDatabase extends RoomDatabase {
    public abstract ChatLogDAO chatLogDAO();
}
