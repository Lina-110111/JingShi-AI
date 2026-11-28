package cn.edu.sicnu.cs.hln.jingshiai.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// 本地用户资料，暂不涉及登录注册，固定只有一条记录
@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey
    val id: Int = DEFAULT_ID,
    val nickname: String,
    val bio: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
) {
    companion object {
        const val DEFAULT_ID = 1
    }
}
