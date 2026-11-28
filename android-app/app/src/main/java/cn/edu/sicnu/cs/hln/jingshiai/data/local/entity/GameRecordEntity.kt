package cn.edu.sicnu.cs.hln.jingshiai.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// 垃圾分类游戏成绩记录
@Entity(tableName = "game_record")
data class GameRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // 本局得分
    val score: Int,
    @ColumnInfo(name = "correct_count")
    val correctCount: Int,
    @ColumnInfo(name = "total_count")
    val totalCount: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
