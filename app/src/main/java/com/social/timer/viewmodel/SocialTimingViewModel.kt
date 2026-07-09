package com.social.timer.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.room.Room
import com.social.timer.db.AppDatabase
import com.social.timer.db.SocialTiming
import com.social.timer.screens.formatMinutes
import com.social.timer.ui.composibles.PieSlice
import kotlinx.coroutines.launch

class SocialTimingViewModel(application: Application) : AndroidViewModel(application) {

    val roomDbName = "social_timer_db"

    val db = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java, roomDbName
    ).build()
    val socialDao = db.appDao()

    var addSocialInfoResult = MutableLiveData(2)

    var deletePastSocialResult = MutableLiveData(2)

    var socialRangeDataResult = MutableLiveData(listOf<PieSlice>())

    var deleteSocialResult = MutableLiveData(2)

    val socialPager = Pager(
        config = PagingConfig(pageSize = 10)
    ) {
        socialDao.getSocialTimingPaged()
    }.flow.cachedIn(viewModelScope)

    val platformColors = hashMapOf(
        "YouTube" to Color(0xFFFF0000),
        "Instagram" to Color(0xFFC32AA3),
        "Threads" to Color(0xFF2B2B2B),
        "X" to Color(0xFF000000),
        "Zee5" to Color(0xFF5A2D81),
        "SonyLiv" to Color(0xFF0066FF),
        "Facebook" to Color(0xFF3B5998),
        "LinkedIn" to Color(0xFF007BB6),
        "Porn" to Color(0xFFA30000),
        "Others" to Color(0xFF9E9E9E)
    )

    fun addSocialTimeInfo(name: String, time: String, timeInMillis: Long) {
        val socialTimingInfo = SocialTiming(
            socialName = name,
            socialTiming = time.toInt(),
            dateTimeEpoch = timeInMillis
        )
        viewModelScope.launch {
            socialDao.saveSocialInfoItem(socialTimingInfo)
            addSocialInfoResult.postValue(1)
        }
    }

    fun deletePastSocial() {
        val threeMonthInMillis = 8 * 24 * 60 * 60 * 7862400000
        val threeMonthAgoMillis = System.currentTimeMillis() - threeMonthInMillis
        viewModelScope.launch {
            socialDao.deletePastSocial(threeMonthAgoMillis)
            deletePastSocialResult.postValue(1)
        }
    }

    fun getPieChartData(dateRangeFlag: Int) {
        val timeNow = System.currentTimeMillis()

        var socialTimeInMillis: Long? = null

        when (dateRangeFlag) {
            0 -> {
                socialTimeInMillis = timeNow - 86400000
            }

            1 -> {
                socialTimeInMillis = timeNow - 604800000
            }

            2 -> {
                socialTimeInMillis = timeNow - 2629746000
            }

            3 -> {
                socialTimeInMillis = timeNow - 7776000000
            }
        }

        viewModelScope.launch {
            val socialData = socialDao.getSocialRangeData(socialTimeInMillis!!)
            val pieChartData = arrayListOf<PieSlice>()

            var totalSocialTiming = 0
            socialData.forEach {
                totalSocialTiming += it.socialTiming
            }
            val onePercent = totalSocialTiming / 100F

            socialData.forEach {
                pieChartData.add(
                    PieSlice(
                        value = (it.socialTiming / onePercent),
                        label = it.socialName,
                        color = platformColors[it.socialName],
                        timing = formatMinutes(it.socialTiming)
                    ),
                )
            }
            socialRangeDataResult.postValue(pieChartData)
        }
    }

    fun deleSocialInfo(id: Int) {
        viewModelScope.launch {
            socialDao.deleSocialInfo(id)
            deleteSocialResult.postValue(1)
        }
    }
}