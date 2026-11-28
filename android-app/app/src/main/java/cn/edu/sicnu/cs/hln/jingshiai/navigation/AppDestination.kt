package cn.edu.sicnu.cs.hln.jingshiai.navigation

import androidx.annotation.DrawableRes
import cn.edu.sicnu.cs.hln.jingshiai.R

// 页面路由
object Routes {
    const val HOME = "home"
    const val RECOGNITION = "recognition"
    const val GAME = "game"
    const val MINE = "mine"
    const val KNOWLEDGE = "knowledge"
}

// 底部导航的四个入口
enum class BottomTab(val route: String, val label: String, @param:DrawableRes val iconRes: Int) {
    Home(Routes.HOME, "首页", R.drawable.ic_nav_home),
    Recognition(Routes.RECOGNITION, "AI 识别", R.drawable.ic_nav_scan),
    Game(Routes.GAME, "分类游戏", R.drawable.ic_nav_game),
    Mine(Routes.MINE, "我的", R.drawable.ic_nav_mine)
}
