package me.sergiobarriodelavega.xend.room;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Convert data types for SQLite
 */
public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
