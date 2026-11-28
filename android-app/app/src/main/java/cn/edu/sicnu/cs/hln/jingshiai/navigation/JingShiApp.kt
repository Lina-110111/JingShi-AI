package cn.edu.sicnu.cs.hln.jingshiai.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cn.edu.sicnu.cs.hln.jingshiai.R
import cn.edu.sicnu.cs.hln.jingshiai.ui.screens.GameHistoryViewModel
import cn.edu.sicnu.cs.hln.jingshiai.ui.screens.GameRecordCard
import cn.edu.sicnu.cs.hln.jingshiai.ui.screens.HomeScreen
import cn.edu.sicnu.cs.hln.jingshiai.ui.screens.KnowledgeScreen
import cn.edu.sicnu.cs.hln.jingshiai.ui.screens.MineScreen
import cn.edu.sicnu.cs.hln.jingshiai.ui.screens.MineViewModel
import cn.edu.sicnu.cs.hln.jingshiai.ui.screens.PlaceholderScreen
import cn.edu.sicnu.cs.hln.jingshiai.ui.screens.RecognitionHistoryViewModel
import cn.edu.sicnu.cs.hln.jingshiai.ui.screens.RecognitionRecordCard
import cn.edu.sicnu.cs.hln.jingshiai.ui.screens.RecognitionScreen
import cn.edu.sicnu.cs.hln.jingshiai.ui.screens.RecordsScreen
import cn.edu.sicnu.cs.hln.jingshiai.ui.screens.StatsViewModel
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.CardWhite
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.GreenLight
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.GreenPrimary
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.PageBackground
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextHint

@Composable
fun JingShiApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // 只在四个主页面显示底部导航
    val showBottomBar = BottomTab.entries.any { it.route == currentRoute }

    Scaffold(
        containerColor = PageBackground,
        bottomBar = {
            if (showBottomBar) {
                JingShiBottomBar(
                    currentRoute = currentRoute,
                    onTabSelected = { tab -> navController.navigateToTab(tab) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                val viewModel: StatsViewModel = viewModel()
                val stats by viewModel.stats.collectAsStateWithLifecycle()
                HomeScreen(
                    stats = stats,
                    onStartRecognition = { navController.navigateToTab(BottomTab.Recognition) },
                    onOpenGame = { navController.navigateToTab(BottomTab.Game) },
                    onOpenKnowledge = { navController.navigate(Routes.KNOWLEDGE) }
                )
            }
            composable(Routes.RECOGNITION) {
                RecognitionScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.GAME) {
                PlaceholderScreen(
                    title = "垃圾分类游戏",
                    description = "游戏模块正在开发中，敬请期待。"
                )
            }
            composable(Routes.MINE) {
                val viewModel: MineViewModel = viewModel()
                val stats by viewModel.stats.collectAsStateWithLifecycle()
                val user by viewModel.user.collectAsStateWithLifecycle()
                MineScreen(
                    stats = stats,
                    user = user,
                    onOpenRecognitionHistory = { navController.navigate(Routes.RECOGNITION_HISTORY) },
                    onOpenGameHistory = { navController.navigate(Routes.GAME_HISTORY) }
                )
            }
            composable(Routes.KNOWLEDGE) {
                KnowledgeScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.RECOGNITION_HISTORY) {
                val viewModel: RecognitionHistoryViewModel = viewModel()
                val records by viewModel.records.collectAsStateWithLifecycle()
                RecordsScreen(
                    title = "识别历史",
                    iconRes = R.drawable.ic_nav_scan,
                    emptyTitle = "暂无识别记录",
                    emptyHint = "完成一次 AI 识别后，识别结果会显示在这里。",
                    records = records,
                    onBack = { navController.popBackStack() }
                ) { record ->
                    RecognitionRecordCard(record)
                }
            }
            composable(Routes.GAME_HISTORY) {
                val viewModel: GameHistoryViewModel = viewModel()
                val records by viewModel.records.collectAsStateWithLifecycle()
                RecordsScreen(
                    title = "游戏记录",
                    iconRes = R.drawable.ic_nav_game,
                    emptyTitle = "暂无游戏记录",
                    emptyHint = "完成一局垃圾分类游戏后，成绩会显示在这里。",
                    records = records,
                    onBack = { navController.popBackStack() }
                ) { record ->
                    GameRecordCard(record)
                }
            }
        }
    }
}

@Composable
private fun JingShiBottomBar(
    currentRoute: String?,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar(
        containerColor = CardWhite,
        tonalElevation = 0.dp
    ) {
        BottomTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        painter = painterResource(tab.iconRes),
                        contentDescription = tab.label
                    )
                },
                label = {
                    Text(text = tab.label, fontSize = 11.sp)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GreenPrimary,
                    selectedTextColor = GreenPrimary,
                    indicatorColor = GreenLight,
                    unselectedIconColor = TextHint,
                    unselectedTextColor = TextHint
                )
            )
        }
    }
}

// 切换底部导航：保留各 Tab 状态，避免重复入栈
private fun NavHostController.navigateToTab(tab: BottomTab) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
