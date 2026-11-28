package cn.edu.sicnu.cs.hln.jingshiai.ui.screens

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.BorderColor
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.CardWhite
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.EnglishLabelStyle
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.GreenPrimary
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.PageBackground
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.SubtleSurface
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextAccent
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextFooter
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextHint
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextOnPrimary
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextPrimary
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 识别页专用色值，取自原 activity_recognition.xml 的 drawable 与文本色
private val TechBlue = Color(0xFF1565C0)
private val PanelGreen = Color(0xFFEAF4EC)
private val CardStroke = Color(0xFFE3EBE4)
private val SuggestionText = Color(0xFF52645A)

@Composable
fun RecognitionScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 已选择的图片与识别结果展示状态
    var previewBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var showResult by remember { mutableStateOf(false) }

    fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun onImageSelected(bitmap: ImageBitmap?) {
        if (bitmap != null) {
            previewBitmap = bitmap
            showResult = false
            toast("图片添加成功，可以开始识别")
        } else {
            toast("未获取到照片，请重新拍摄")
        }
    }

    // 相册选择
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    runCatching {
                        context.contentResolver.openInputStream(uri)?.use {
                            BitmapFactory.decodeStream(it)
                        }
                    }.getOrNull()
                }
                if (bitmap != null) {
                    onImageSelected(bitmap.asImageBitmap())
                } else {
                    toast("图片读取失败，请重新选择")
                }
            }
        }
    }

    // 系统相机
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        onImageSelected(bitmap?.asImageBitmap())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PageBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 16.dp)
    ) {
        RecognitionHeader(onBack = onBack)

        Spacer(Modifier.height(12.dp))
        Text(
            text = "拍摄或上传垃圾图片，让 AI 帮你快速识别分类",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(Modifier.height(16.dp))
        UploadPanel(
            previewBitmap = previewBitmap,
            onCameraClick = {
                runCatching { cameraLauncher.launch(null) }
                    .onFailure { toast("暂时无法打开相机") }
            },
            onGalleryClick = { galleryLauncher.launch("image/*") }
        )

        Spacer(Modifier.height(18.dp))
        StartRecognitionButton(enabled = previewBitmap != null) {
            if (previewBitmap == null) {
                toast("请先选择或拍摄一张图片")
            } else {
                showResult = true
                toast("演示识别完成")
            }
        }

        if (showResult) {
            Spacer(Modifier.height(22.dp))
            DemoResultCard()
        }

        Spacer(Modifier.height(22.dp))
        Text(
            text = "净识AI · 让垃圾分类更简单",
            style = MaterialTheme.typography.labelSmall,
            color = TextFooter,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
    }
}

// 顶部导航栏
@Composable
private fun RecognitionHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(50))
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = GreenPrimary,
                fontSize = 34.sp
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AI 智能识别",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Text(
                text = "SMART RECOGNITION",
                style = EnglishLabelStyle.copy(fontSize = 8.sp, letterSpacing = 0.12.em),
                color = TextHint
            )
        }
        Box(
            modifier = Modifier.size(42.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "♻",
                color = GreenPrimary,
                fontSize = 27.sp
            )
        }
    }
}

// 上传区域
@Composable
private fun UploadPanel(
    previewBitmap: ImageBitmap?,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PanelGreen)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "上传识别图片",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "IMAGE UPLOAD",
                style = EnglishLabelStyle,
                color = TextAccent
            )
        }

        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(CardWhite)
                .border(1.dp, CardStroke, RoundedCornerShape(20.dp))
        ) {
            if (previewBitmap == null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "▧",
                        color = GreenPrimary,
                        fontSize = 48.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "选择一张垃圾图片",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = "支持 JPG、PNG 等常见图片格式",
                        color = TextHint,
                        fontSize = 11.sp
                    )
                }
            } else {
                Image(
                    bitmap = previewBitmap,
                    contentDescription = "待识别图片",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }
        }

        Spacer(Modifier.height(15.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCameraClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor = TextOnPrimary
                ),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "📷  拍照",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            OutlinedButton(
                onClick = onGalleryClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, GreenPrimary),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = CardWhite,
                    contentColor = GreenPrimary
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "▧  相册选择",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 开始识别按钮：未选择图片时置灰
@Composable
private fun StartRecognitionButton(enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(15.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(GreenPrimary, TechBlue)))
            .alpha(if (enabled) 1f else 0.55f),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextOnPrimary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = TextOnPrimary
        ),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = "✦  开始智能识别",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// 识别结果卡片：当前仍为演示数据，明确标注“演示结果”
@Composable
private fun DemoResultCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardWhite)
            .border(1.dp, CardStroke, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "识别结果",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "演示结果",
                color = GreenPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(PanelGreen)
                    .padding(horizontal = 11.dp, vertical = 6.dp)
            )
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = "识别物品",
            color = TextHint,
            fontSize = 12.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "塑料瓶",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )

        Spacer(Modifier.height(17.dp))
        HorizontalDivider(color = BorderColor, thickness = 1.dp)

        Spacer(Modifier.height(15.dp))
        Text(
            text = "所属垃圾类别",
            color = TextHint,
            fontSize = 12.sp
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = "可回收物",
            color = GreenPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(PanelGreen)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )

        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "识别置信度",
                color = TextHint,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "96.8%",
                color = GreenPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(15.dp))
        Text(
            text = "投放建议：清空瓶内液体，简单冲洗后投入可回收物收集容器。",
            color = SuggestionText,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(SubtleSurface)
                .padding(13.dp)
        )
    }
}
