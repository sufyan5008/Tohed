package com.tohed.islampro.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tohed.islampro.adapters.dao.PostDao

@Database(entities = [PostEntity::class, PageEntity::class], version = 2, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao


}

