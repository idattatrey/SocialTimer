package com.social.timer.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.social.timer.ui.composibles.AnimatedPreLoader
import com.social.timer.ui.composibles.LabeledPieChart
import com.social.timer.ui.composibles.PieSlice
import com.social.timer.ui.theme.outfitFontFamily
import com.social.timer.viewmodel.SocialTimingViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(mainPaddingValues: PaddingValues, viewModel: SocialTimingViewModel) {
    val tabItems = listOf("Daily", "Weekly", "Monthly", "Quarterly")

    val pagerState = rememberPagerState(pageCount = { tabItems.size })
    val coroutineScope = rememberCoroutineScope()

    val socialInfoList = remember { mutableStateListOf<PieSlice>() }

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val selectedIndex = remember { mutableIntStateOf(0) }

    viewModel.socialRangeDataResult.observe(lifecycleOwner) {
        if (it.isNotEmpty()) {
            socialInfoList.clear()
            socialInfoList.addAll(it)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getPieChartData(0)
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(mainPaddingValues)
        ) {
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(pagerState.currentPage),
                        color = Color.White
                    )
                }
            ) {
                tabItems.forEachIndexed { index, item ->
                    val isSelected = pagerState.currentPage == index

                    Tab(
                        selected = isSelected,
                        onClick = {
                            selectedIndex.intValue = index
                            when (index) {
                                0 -> {
                                    viewModel.getPieChartData(index)
                                }

                                1 -> {
                                    viewModel.getPieChartData(index)
                                }

                                2 -> {
                                    viewModel.getPieChartData(index)
                                }

                                3 -> {
                                    viewModel.getPieChartData(index)
                                }
                            }
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        modifier = Modifier.background(Color(0xFF6100ED)),
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color.Gray
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item,
                                style = TextStyle(
                                    color = Color.White,
                                    fontFamily = outfitFontFamily,
                                    fontWeight = FontWeight.SemiBold
                                ),
                            )
                        }

                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth(),
                key = { page -> page }
            ) { _ -> }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFBFCF8))
            ) {
                if (socialInfoList.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        LabeledPieChart(
                            slices = socialInfoList,
                            modifier = Modifier
                                .padding(36.dp)
                                .fillMaxWidth()
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(socialInfoList.size) { index ->
                                GridItemCard(
                                    title = socialInfoList[index].label + "\n" + socialInfoList[index].timing
                                )
                            }
                        }
                    }
                } else {
                    AnimatedPreLoader()
                }
            }
        }
    }
}

@Composable
fun GridItemCard(title: String) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            style = TextStyle(fontFamily = outfitFontFamily, fontWeight = FontWeight.SemiBold),
            lineHeight = 24.sp
        )
    }
}