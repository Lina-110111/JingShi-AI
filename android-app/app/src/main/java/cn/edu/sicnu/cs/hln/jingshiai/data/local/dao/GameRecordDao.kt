package cn.edu.sicnu.cs.hln.jingshiai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cn.edu.sicnu.cs.hln.jingshiai.data.local.entity.GameRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameRecordDao {

    @Insert
    suspend fun insert(record: GameRecordEntity): Long

    // 按时间倒序观察游戏记录
    @Query("SELECT * FROM game_record ORDER BY created_at DESC")
    fun observeAll(): Flow<List<GameRecordEntity>>

    // 观察游戏完成次数
    @Query("SELECT COUNT(*) FROM game_record")
    fun observeCount(): Flow<Int>

    @Query("DELETE FROM game_record")
    suspend fun clear()
}
