package com.social.timer.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SocialTiming::class],
    version = 1,
    exportSchema = true
)

abstract class AppDatabase : RoomDatabase(){
    abstract fun appDao(): AppDao
}