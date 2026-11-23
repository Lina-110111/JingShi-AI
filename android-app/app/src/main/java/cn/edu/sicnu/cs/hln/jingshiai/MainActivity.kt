package cn.edu.sicnu.cs.hln.jingshiai



import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 加载首页
        setContentView(R.layout.activity_main)

        // AI识别按钮
        findViewById<Button>(R.id.btnAiRecognition)
            .setOnClickListener {
                showMessage("AI垃圾识别模块即将接入")
            }

        // 核心功能入口
        findViewById<View>(R.id.btnGame)
            .setOnClickListener {
                showMessage("等待游戏模块接入")
            }

        findViewById<View>(R.id.btnKnowledge)
            .setOnClickListener {
                showMessage("分类知识模块即将开放")
            }

        // 底部导航栏
        initBottomNavigation()
    }

    /**
     * 初始化底部导航栏
     */
    private fun initBottomNavigation() {

        // 首页
        findViewById<View>(R.id.navHome)
            .setOnClickListener {
                showMessage("当前已经是首页")
            }

        // 识别
        findViewById<View>(R.id.navRecognition)
            .setOnClickListener {
                showMessage("即将进入AI垃圾识别")
            }

        // 游戏
        findViewById<View>(R.id.navGame)
            .setOnClickListener {
                showMessage("即将进入垃圾分类游戏")
            }

        // 我的
        findViewById<View>(R.id.navMine)
            .setOnClickListener {
                showMessage("个人中心模块即将开放")
            }
    }

    /**
     * 统一提示方法
     */
    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
