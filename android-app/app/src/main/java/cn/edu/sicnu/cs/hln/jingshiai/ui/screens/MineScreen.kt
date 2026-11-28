package cn.edu.sicnu.cs.hln.jingshiai.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cn.edu.sicnu.cs.hln.jingshiai.R
import cn.edu.sicnu.cs.hln.jingshiai.ui.components.SectionTitle
import cn.edu.sicnu.cs.hln.jingshiai.ui.components.StatsRow
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.BorderColor
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.CardWhite
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.GreenLight
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.GreenPrimary
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.PageBackground
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextHint
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextPrimary
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextSecondary

// 当前弹出的对话框
private enum class MineDialog { Settings, About, Feedback }

@Composable
fun MineScreen(
    stats: StatsUiState,
    onOpenRecognitionHistory: () -> Unit,
    onOpenGameHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var dialog by remember { mutableStateOf<MineDialog?>(null) }
    var feedbackText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PageBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 24.dp)
    ) {
        ProfileCard()

        Spacer(Modifier.height(26.dp))
        SectionTitle("环保数据")
        Spacer(Modifier.height(12.dp))
        StatsRow(
            recognitionCount = stats.recognitionCount,
            gameCount = stats.gameCount
        )

        Spacer(Modifier.height(26.dp))
        SectionTitle("我的记录")
        Spacer(Modifier.height(12.dp))
        RowCard {
            ListRow(title = "识别历史", onClick = onOpenRecognitionHistory)
            HorizontalDivider(
                color = BorderColor,
                modifier = Modifier.padding(horizontal = 18.dp)
            )
            ListRow(title = "游戏记录", onClick = onOpenGameHistory)
        }

        Spacer(Modifier.height(26.dp))
        SectionTitle("其他功能")
        Spacer(Modifier.height(12.dp))
        RowCard {
            ListRow(title = "设置", onClick = { dialog = MineDialog.Settings })
            HorizontalDivider(
                color = BorderColor,
                modifier = Modifier.padding(horizontal = 18.dp)
            )
            ListRow(title = "关于净识AI", onClick = { dialog = MineDialog.About })
            HorizontalDivider(
                color = BorderColor,
                modifier = Modifier.padding(horizontal = 18.dp)
            )
            ListRow(title = "意见反馈", onClick = { dialog = MineDialog.Feedback })
        }
    }

    when (dialog) {
        MineDialog.Settings -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text("设置") },
            text = {
                Text(
                    text = "当前为第一阶段版本，深色模式、通知等更多设置项将在后续版本开放。",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { dialog = null }) { Text("确定") }
            }
        )

        MineDialog.About -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text("关于净识AI") },
            text = {
                Text(
                    text = "净识AI 1.0\n\n基于 AI 的垃圾分类识别与环保学习应用，帮助你快速判断垃圾类别、积累分类知识。",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { dialog = null }) { Text("确定") }
            }
        )

        MineDialog.Feedback -> AlertDialog(
            onDismissRequest = {
                dialog = null
                feedbackText = ""
            },
            title = { Text("意见反馈") },
            text = {
                Column {
                    Text(
                        text = "请描述你遇到的问题或建议：",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("例如：识别结果不准确") },
                        minLines = 3,
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = feedbackText.isNotBlank(),
                    onClick = {
                        dialog = null
                        feedbackText = ""
                        Toast.makeText(context, "感谢你的反馈，我们会尽快处理", Toast.LENGTH_SHORT).show()
                    }
                ) { Text("提交") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        dialog = null
                        feedbackText = ""
                    }
                ) { Text("取消") }
            }
        )

        null -> Unit
    }
}

// 用户信息区域
@Composable
private fun ProfileCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardWhite)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 默认头像：人形矢量图标
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(GreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_nav_mine),
                contentDescription = "默认头像",
                tint = GreenPrimary,
                modifier = Modifier.size(30.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Text(
                text = "净识用户",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "让垃圾分类更简单",
                style = MaterialTheme.typography.labelSmall,
                color = TextHint
            )
        }
        Text(
            text = "本地账户",
            style = MaterialTheme.typography.labelSmall,
            color = GreenPrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(GreenLight)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

// 白底圆角卡片容器
@Composable
private fun RowCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardWhite)
    ) {
        content()
    }
}

// 列表行：标题 + 右侧箭头
@Composable
private fun ListRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = TextHint,
            modifier = Modifier.size(18.dp)
        )
    }
}
