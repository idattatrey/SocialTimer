package com.social.timer.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.social.timer.model.SocialInfo


@Dao
interface AppDao {
    @Query("SELECT * FROM social_timing")
    fun getSocialTimingPaged(): PagingSource<Int, SocialTiming>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSocialInfoItem(socialInfo: SocialTiming): Long

    @Query("DELETE FROM social_timing WHERE date_time < :socialDateInMillis")
    suspend fun deletePastSocial(socialDateInMillis: Long): Int

    @Query("SELECT socialName, SUM(social_timing.socialTiming) AS socialTiming FROM social_timing WHERE date_time > :socialDateInMillis group by socialName")
    suspend fun getSocialRangeData(socialDateInMillis: Long): List<SocialInfo>

    @Query("DELETE FROM social_timing WHERE id = :id")
    suspend fun deleSocialInfo(id: Int): Int
}