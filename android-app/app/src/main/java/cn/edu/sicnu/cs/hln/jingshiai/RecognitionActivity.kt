package cn.edu.sicnu.cs.hln.jingshiai

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class RecognitionActivity : AppCompatActivity() {

    private lateinit var ivPreview: ImageView
    private lateinit var layoutPlaceholder: LinearLayout
    private lateinit var btnStartRecognition: Button
    private lateinit var layoutResult: LinearLayout

    private var hasImage = false

    // 相册选择图片
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            if (uri != null) {
                ivPreview.setImageURI(uri)
                showSelectedImage()
            }
        }

    // 调用系统相机
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->

            if (bitmap != null) {
                ivPreview.setImageBitmap(bitmap)
                showSelectedImage()
            } else {
                showMessage("未获取到照片，请重新拍摄")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_recognition)

        // 状态栏和导航栏
        window.statusBarColor = Color.rgb(46, 125, 50)
        window.navigationBarColor = Color.BLACK

        // 初始化控件
        initViews()

        // 初始化点击事件
        initListeners()
    }

    private fun initViews() {

        ivPreview = findViewById(R.id.ivPreview)

        layoutPlaceholder = findViewById(R.id.layoutPlaceholder)

        btnStartRecognition = findViewById(R.id.btnStartRecognition)

        layoutResult = findViewById(R.id.layoutResult)

        // 获取三个按钮
        val btnCamera = findViewById<Button>(R.id.btnCamera)
        val btnGallery = findViewById<Button>(R.id.btnGallery)

        // 关键：清除 Android 主题默认的紫色 Tint
        btnCamera.backgroundTintList = null
        btnGallery.backgroundTintList = null
        btnStartRecognition.backgroundTintList = null

        // 重新指定自定义背景
        btnCamera.setBackgroundResource(R.drawable.bg_recognition_primary)

        btnGallery.setBackgroundResource(R.drawable.bg_recognition_outline)

        btnStartRecognition.setBackgroundResource(
            R.drawable.bg_recognition_gradient
        )
    }

    private fun initListeners() {

        // 返回
        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // 拍照
        findViewById<Button>(R.id.btnCamera).setOnClickListener {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                showMessage("暂时无法打开相机")
            }
        }

        // 相册选择
        findViewById<Button>(R.id.btnGallery).setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // 开始识别
        btnStartRecognition.setOnClickListener {

            if (!hasImage) {
                showMessage("请先选择或拍摄一张图片")
                return@setOnClickListener
            }

            showDemoResult()
        }

        // 底部导航
        initBottomNavigation()
    }

    // 图片选择成功
    private fun showSelectedImage() {

        hasImage = true

        ivPreview.visibility = View.VISIBLE
        layoutPlaceholder.visibility = View.GONE

        // 启用识别按钮
        btnStartRecognition.isEnabled = true
        btnStartRecognition.alpha = 1.0f

        // 隐藏旧结果
        layoutResult.visibility = View.GONE

        showMessage("图片添加成功，可以开始识别")
    }

    // 演示识别结果
    private fun showDemoResult() {

        // 当前为演示数据，尚未接入真实AI模型
        findViewById<TextView>(R.id.tvObjectName).text =
            "塑料瓶"

        findViewById<TextView>(R.id.tvCategory).text =
            "可回收物"

        findViewById<TextView>(R.id.tvConfidence).text =
            "96.8%"

        findViewById<TextView>(R.id.tvSuggestion).text =
            "投放建议：清空瓶内液体，简单冲洗后投入可回收物收集容器。"

        layoutResult.visibility = View.VISIBLE

        showMessage("演示识别完成")
    }

    // 底部导航
    private fun initBottomNavigation() {

        // 首页
        findViewById<View>(R.id.navHome).setOnClickListener {
            finish()
        }

        // 当前识别页面
        findViewById<View>(R.id.navRecognition).setOnClickListener {
            showMessage("当前已经是识别页面")
        }

        // 游戏
        findViewById<View>(R.id.navGame).setOnClickListener {
            showMessage("游戏模块即将开放")
        }

        // 我的
        findViewById<View>(R.id.navMine).setOnClickListener {
            showMessage("个人中心模块即将开放")
        }
    }

    // 统一提示
    private fun showMessage(message: String) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
