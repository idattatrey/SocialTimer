package com.social.timer.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "social_timing")
data class SocialTiming(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val socialName: String,
    val socialTiming: Int,
    @ColumnInfo(name = "date_time")
    val dateTimeEpoch: Long,
)