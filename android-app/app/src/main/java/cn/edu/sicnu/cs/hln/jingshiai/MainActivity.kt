package cn.edu.sicnu.cs.hln.jingshiai

import android.content.Intent
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

        // AI识别按钮：进入识别页面
        findViewById<Button>(R.id.btnAiRecognition)
            .setOnClickListener {
                openRecognitionPage()
            }

        // 分类闯关
        findViewById<View>(R.id.btnGame)
            .setOnClickListener {
                showMessage("等待游戏模块接入")
            }

        // 分类知识
        findViewById<View>(R.id.btnKnowledge)
            .setOnClickListener {
                showMessage("分类知识模块即将开放")
            }

        // 底部导航
        initBottomNavigation()
    }

    // 打开AI识别页面
    private fun openRecognitionPage() {
        val intent = Intent(this, RecognitionActivity::class.java)
        startActivity(intent)
    }

    // 底部导航
    private fun initBottomNavigation() {

        findViewById<View>(R.id.navHome)
            .setOnClickListener {
                showMessage("当前已经是首页")
            }

        findViewById<View>(R.id.navRecognition)
            .setOnClickListener {
                openRecognitionPage()
            }

        findViewById<View>(R.id.navGame)
            .setOnClickListener {
                showMessage("等待游戏模块接入")
            }

        findViewById<View>(R.id.navMine)
            .setOnClickListener {
                showMessage("个人中心模块即将开放")
            }
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
