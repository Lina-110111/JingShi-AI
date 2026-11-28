package cn.edu.sicnu.cs.hln.jingshiai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cn.edu.sicnu.cs.hln.jingshiai.data.local.entity.RecognitionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecognitionDao {

    @Insert
    suspend fun insert(record: RecognitionEntity): Long

    // 按时间倒序观察识别历史
    @Query("SELECT * FROM recognition_record ORDER BY created_at DESC")
    fun observeAll(): Flow<List<RecognitionEntity>>

    // 观察累计识别次数，供首页与“我的”统计使用
    @Query("SELECT COUNT(*) FROM recognition_record")
    fun observeCount(): Flow<Int>

    @Query("DELETE FROM recognition_record")
    suspend fun clear()
}
