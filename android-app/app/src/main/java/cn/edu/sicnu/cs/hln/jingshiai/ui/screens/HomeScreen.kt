package cn.edu.sicnu.cs.hln.jingshiai.ui.screens

import androidx.annotation.DrawableRes
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.sicnu.cs.hln.jingshiai.R
import cn.edu.sicnu.cs.hln.jingshiai.data.local.KnowledgeData
import cn.edu.sicnu.cs.hln.jingshiai.data.local.KnowledgeItem
import cn.edu.sicnu.cs.hln.jingshiai.ui.components.SectionTitle
import cn.edu.sicnu.cs.hln.jingshiai.ui.components.StatsRow
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.CardWhite
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.GreenLight
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.GreenPrimary
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.GreenPrimaryDark
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.PageBackground
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextHint
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextOnPrimary
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextPrimary
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextSecondary
import java.util.Calendar

@Composable
fun HomeScreen(
    uiState: StatsUiState,
    onStartRecognition: () -> Unit,
    onOpenGame: () -> Unit,
    onOpenKnowledge: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 同一天展示同一条知识，内容来自本地预置数据
    val todayKnowledge = remember {
        KnowledgeData.todayItem(Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PageBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 24.dp)
    ) {
        BrandHeader()

        Spacer(Modifier.height(20.dp))
        AiRecognitionCard(onStartRecognition)

        Spacer(Modifier.height(26.dp))
        SectionTitle("快捷功能")
        Spacer(Modifier.height(12.dp))
        QuickActions(
            onStartRecognition = onStartRecognition,
            onOpenGame = onOpenGame,
            onOpenKnowledge = onOpenKnowledge
        )

        Spacer(Modifier.height(26.dp))
        SectionTitle(
            title = "今日分类知识",
            actionText = "查看更多",
            onAction = onOpenKnowledge
        )
        Spacer(Modifier.height(12.dp))
        TodayKnowledgeCard(
            item = todayKnowledge,
            onClick = onOpenKnowledge
        )

        Spacer(Modifier.height(26.dp))
        SectionTitle("环保数据")
        Spacer(Modifier.height(12.dp))
        StatsRow(
            recognitionCount = uiState.recognitionCount,
            gameCount = uiState.gameCount
        )
    }
}

// 顶部品牌区域
@Composable
private fun BrandHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(GreenPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_recycle),
                contentDescription = "净识AI 循环环保标识",
                tint = TextOnPrimary,
                modifier = Modifier.size(26.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = "净识AI",
                style = MaterialTheme.typography.headlineSmall,
                color = GreenPrimary
            )
            Text(
                text = "让垃圾分类更简单",
                style = MaterialTheme.typography.labelSmall,
                color = TextHint
            )
        }
        Text(
            text = "AI 环保助手",
            style = MaterialTheme.typography.labelSmall,
            color = GreenPrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(GreenLight)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// AI 智能识别主卡片
@Composable
private fun AiRecognitionCard(onStartRecognition: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(GreenPrimary, GreenPrimaryDark)))
    ) {
        // 背景装饰图标，不参与交互
        Icon(
            painter = painterResource(R.drawable.ic_nav_scan),
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.16f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 14.dp)
                .size(110.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .padding(22.dp)
        ) {
            Text(
                text = "AI 智能识别",
                color = TextOnPrimary,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "拍摄或上传一张图片，快速判断垃圾类别",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onStartRecognition,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TextOnPrimary,
                    contentColor = GreenPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("立即识别")
            }
        }
    }
}

// 三个快捷功能入口
@Composable
private fun QuickActions(
    onStartRecognition: () -> Unit,
    onOpenGame: () -> Unit,
    onOpenKnowledge: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        QuickAction(
            iconRes = R.drawable.ic_camera,
            label = "拍照识别",
            onClick = onStartRecognition,
            modifier = Modifier.weight(1f)
        )
        QuickAction(
            iconRes = R.drawable.ic_nav_game,
            label = "垃圾分类游戏",
            onClick = onOpenGame,
            modifier = Modifier.weight(1f)
        )
        QuickAction(
            iconRes = R.drawable.ic_knowledge,
            label = "分类知识",
            onClick = onOpenKnowledge,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickAction(
    @DrawableRes iconRes: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(108.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(CardWhite)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(GreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

// 今日分类知识卡片
@Composable
private fun TodayKnowledgeCard(item: KnowledgeItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardWhite)
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        Text(
            text = item.category,
            style = MaterialTheme.typography.labelSmall,
            color = GreenPrimary,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(GreenLight)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = item.content,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}
