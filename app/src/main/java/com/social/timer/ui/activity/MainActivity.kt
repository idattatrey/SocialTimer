package com.social.timer.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.social.timer.R
import com.social.timer.model.TabBarItem
import com.social.timer.screens.AnalyticsScreen
import com.social.timer.screens.TimingsScreen
import com.social.timer.ui.dialog.AddTimingDialog
import com.social.timer.ui.dialog.CustomProgressDialog
import com.social.timer.ui.dialog.GenericDialog
import com.social.timer.ui.theme.SocialTimerTheme
import com.social.timer.ui.theme.outfitFontFamily
import com.social.timer.viewmodel.SocialTimingViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SocialTimingViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
            val homeTab = TabBarItem(
                title = "Timings",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            )
            val analyticsTab = TabBarItem(
                title = "Analytics",
                selectedIcon = Icons.Filled.Analytics,
                unselectedIcon = Icons.Outlined.Analytics
            )

            val tabBarItems = listOf(homeTab, analyticsTab)

            val navController = rememberNavController()

            val showAddTimingDialog = remember { mutableStateOf(false) }
            var showLoadingDialog by remember { mutableStateOf(false) }

            if (showAddTimingDialog.value) {
                AddTimingDialog(
                    onDismissRequest = { showAddTimingDialog.value = false },
                    onConfirm = { name, time, timeInMillis ->
                        showAddTimingDialog.value = false
                        showLoadingDialog = true
                        viewModel.addSocialTimeInfo(name, time, timeInMillis)
                    })
            }

            CustomProgressDialog(
                isLoading = showLoadingDialog,
                onDismissRequest = { showLoadingDialog = false }
            )

            viewModel.addSocialInfoResult.observe(lifecycleOwner) {
                showLoadingDialog = false
                if (it != 2) {
                    if (it > 0) {
                        Toast.makeText(
                            applicationContext,
                            "Social timing info has been added successfully!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (it == 0) {
                        Toast.makeText(
                            applicationContext,
                            "Something went wrong, Please try again later!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            val context = LocalContext.current
            val showPastSocialDeletionDialog = remember { mutableStateOf(false) }

            if (showPastSocialDeletionDialog.value) {
                GenericDialog(
                    onDismissRequest = { showPastSocialDeletionDialog.value = false },
                    onConfirm = {
                        showLoadingDialog = true
                        viewModel.deletePastSocial()
                    },
                    "Delete Past Social Info",
                    "Do you want to delete past social info?"
                )
            }

            viewModel.deletePastSocialResult.observe(lifecycleOwner) {
                showLoadingDialog = false
                showPastSocialDeletionDialog.value = false

                if (it != 2) {
                    if (it > 0) {
                        Toast.makeText(
                            context,
                            "Past social info has been deleted successfully!!!",
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

            SocialTimerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Social Timer",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }, navigationIcon = {
                                    IconButton(onClick = { }) {
                                        Icon(
                                            imageVector = Icons.Filled.Timer,
                                            contentDescription = "Back",
                                            tint = Color.White
                                        )
                                    }
                                }, actions = {
                                    IconButton(
                                        onClick = {
                                            showPastSocialDeletionDialog.value = true
                                        }
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_delete_white),
                                            contentDescription = "Description for accessibility"
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            showAddTimingDialog.value = true
                                        }
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_add_24),
                                            contentDescription = "Description for accessibility"
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color(0xFF3700B4),
                                    titleContentColor = Color.White,
                                    navigationIconContentColor = Color.White
                                ), modifier = Modifier.shadow(6.dp)
                            )
                        }, bottomBar = { TabView(tabBarItems, navController) }) { paddingValues ->
                        NavHost(navController = navController, startDestination = homeTab.title) {
                            composable(homeTab.title) {
                                TimingsScreen(paddingValues, viewModel)
                            }
                            composable(analyticsTab.title) {
                                AnalyticsScreen(paddingValues, viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar {
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(tabBarItem.title)
                },
                icon = {
                    TabBarIconView(
                        isSelected = selectedTabIndex == index,
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title,
                        badgeAmount = tabBarItem.badgeAmount
                    )
                },
                label = {
                    Text(
                        tabBarItem.title,
                        style = TextStyle(
                            fontFamily = outfitFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    )
                })
        }
    }
}

@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    badgeAmount: Int? = null
) {
    BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {
        Icon(
            imageVector = if (isSelected) {
                selectedIcon
            } else {
                unselectedIcon
            },
            contentDescription = title
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}