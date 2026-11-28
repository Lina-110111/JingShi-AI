package cn.edu.sicnu.cs.hln.jingshiai.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.sicnu.cs.hln.jingshiai.R
import cn.edu.sicnu.cs.hln.jingshiai.data.local.entity.GameRecordEntity
import cn.edu.sicnu.cs.hln.jingshiai.data.local.entity.RecognitionEntity
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.CardWhite
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.GreenLight
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.GreenPrimary
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.PageBackground
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextHint
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextPrimary
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 记录页：识别历史 / 游戏记录，数据为空时展示明确的空状态
@Composable
fun <T> RecordsScreen(
    title: String,
    @DrawableRes iconRes: Int,
    emptyTitle: String,
    emptyHint: String,
    records: List<T>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {
        // 顶部返回栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "返回",
                    tint = GreenPrimary
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
        }

        if (records.isEmpty()) {
            EmptyState(
                iconRes = iconRes,
                emptyTitle = emptyTitle,
                emptyHint = emptyHint,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(records) { record ->
                    itemContent(record)
                }
            }
        }
    }
}

// 空状态
@Composable
private fun EmptyState(
    @DrawableRes iconRes: Int,
    emptyTitle: String,
    emptyHint: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(GreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = emptyTitle,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = emptyHint,
            style = MaterialTheme.typography.bodyMedium,
            color = TextHint,
            textAlign = TextAlign.Center
        )
    }
}

// 识别历史条目
@Composable
fun RecognitionRecordCard(record: RecognitionEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardWhite)
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = record.category,
                style = MaterialTheme.typography.labelSmall,
                color = GreenPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(GreenLight)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = formatTime(record.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = TextHint
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = record.objectName,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "置信度 ${String.format(Locale.getDefault(), "%.1f", record.confidence * 100)}%",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

// 游戏记录条目
@Composable
fun GameRecordCard(record: GameRecordEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardWhite)
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "本局得分",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = formatTime(record.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = TextHint
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = record.score.toString(),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            Text(
                text = "分",
                style = MaterialTheme.typography.labelSmall,
                color = TextHint,
                modifier = Modifier.padding(start = 3.dp, bottom = 3.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "答对 ${record.correctCount}/${record.totalCount} 题",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

// 记录时间格式化
private fun formatTime(time: Long): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(time))
