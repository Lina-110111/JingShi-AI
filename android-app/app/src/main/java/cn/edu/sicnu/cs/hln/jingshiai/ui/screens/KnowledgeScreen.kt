package cn.edu.sicnu.cs.hln.jingshiai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import cn.edu.sicnu.cs.hln.jingshiai.R
import cn.edu.sicnu.cs.hln.jingshiai.data.local.KnowledgeData
import cn.edu.sicnu.cs.hln.jingshiai.data.local.KnowledgeItem
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.CardWhite
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.GreenLight
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.GreenPrimary
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.PageBackground
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextPrimary
import cn.edu.sicnu.cs.hln.jingshiai.ui.theme.TextSecondary

// 分类知识列表页，内容来自本地预置数据
@Composable
fun KnowledgeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
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
                text = "分类知识",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(KnowledgeData.items, key = { it.id }) { item ->
                KnowledgeCard(item)
            }
        }
    }
}

@Composable
private fun KnowledgeCard(item: KnowledgeItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardWhite)
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
