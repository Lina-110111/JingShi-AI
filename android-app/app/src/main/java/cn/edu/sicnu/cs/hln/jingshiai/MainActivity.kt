package cn.edu.sicnu.cs.hln.jingshiai

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cn.edu.sicnu.cs.hln.jingshiai.navigation.JingShiApp
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.JingShiTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 边到边显示：状态栏与导航栏透明，内容通过 Compose 的 Scaffold 处理安全区
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        // 单 Activity + Compose 页面导航
        setContent {
            JingShiTheme {
                JingShiApp()
            }
        }
    }
}
