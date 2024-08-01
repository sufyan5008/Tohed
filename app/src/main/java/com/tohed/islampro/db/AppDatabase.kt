package com.tohed.islampro.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tohed.islampro.adapters.dao.PostDao

@Database(entities = [PostEntity::class], version = 2)
/*
@TypeConverters(Converters::class)
*/
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    //abstract fun categoryDao(): CategoryDao
}

