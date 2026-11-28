package cn.edu.sicnu.cs.hln.jingshiai.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// 图片识别记录
@Entity(tableName = "recognition_record")
data class RecognitionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // 识别出的物品名称
    @ColumnInfo(name = "object_name")
    val objectName: String,
    // 垃圾分类类别，如“可回收物”
    val category: String,
    // 置信度，取值范围 0~1
    val confidence: Float,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
