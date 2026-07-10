package com.social.timer.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.social.timer.R
import com.social.timer.ui.composibles.AnimatedPreLoader
import com.social.timer.ui.dialog.CustomProgressDialog
import com.social.timer.viewmodel.SocialTimingViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimingsScreen(mainPaddingValues: PaddingValues, viewModel: SocialTimingViewModel) {

    val lazyPagingItems = viewModel.socialPager.collectAsLazyPagingItems()
    var showLoadingDialog by remember { mutableStateOf(false) }
    val showNoDataAnimation = remember { mutableStateOf(false) }
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    viewModel.deleteSocialResult.observe(lifecycleOwner) {
        showLoadingDialog = false
        if (it != 2) {
            if (it > 0) {
                Toast.makeText(
                    context,
                    "Social info has been deleted successfully!!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (it == 0) {
                Toast.makeText(
                    context,
                    "Something went wrong, Please try again later!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(mainPaddingValues)
                .background(Color(0xFFFBFCF8))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1F)
            ) {
                item { Spacer(modifier = Modifier.height(12.dp)) }
                items(
                    count = lazyPagingItems.itemCount,
                    key = lazyPagingItems.itemKey { it.id }
                ) { index ->
                    val social = lazyPagingItems[index]
                    if (social != null) {
                        showNoDataAnimation.value = false
                        showLoadingDialog = false
                        val timing = " : " + formatMinutes(social.socialTiming)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFCFBFC))
                            ) {
                                Row {
                                    Text(
                                        social.socialName, modifier = Modifier
                                            .padding(
                                                start = 16.dp,
                                                end = 16.dp,
                                                top = 16.dp,
                                                bottom = 8.dp
                                            )
                                            .weight(1F), style = TextStyle(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp
                                        )
                                    )
                                    Image(
                                        painter = painterResource(R.drawable.outline_delete_24),
                                        contentDescription = stringResource(id = R.string.app_name),
                                        modifier = Modifier
                                            .padding(
                                                start = 12.dp,
                                                end = 12.dp,
                                                top = 12.dp,
                                            )
                                            .width(24.dp)
                                            .height(24.dp)
                                            .clickable {
                                                showLoadingDialog = true
                                                viewModel.deleSocialInfo(social.id)
                                            }
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .padding(bottom = 16.dp)
                                ) {
                                    Text(
                                        "Browsed Time", style = TextStyle(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF58585D)
                                        )
                                    )
                                    Text(
                                        timing,
                                        style = TextStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                if (lazyPagingItems.loadState.append is LoadState.Loading) {
                    item { showLoadingDialog = true }
                }
                if (lazyPagingItems.loadState.refresh is LoadState.NotLoading && lazyPagingItems.itemCount == 0) {
                    item {
                        showNoDataAnimation.value = true
                    }
                }
            }
        }
        CustomProgressDialog(
            isLoading = showLoadingDialog,
            onDismissRequest = { showLoadingDialog = false }
        )
        if (showNoDataAnimation.value) {
            AnimatedPreLoader()
        }
    }
}

fun formatMinutes(totalMinutes: Int): String {

    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return "${hours}h ${minutes}m"
}