package cn.edu.sicnu.cs.hln.jingshiai.data.repository

import cn.edu.sicnu.cs.hln.jingshiai.data.local.JingShiDatabase
import cn.edu.sicnu.cs.hln.jingshiai.data.local.entity.GameRecordEntity
import cn.edu.sicnu.cs.hln.jingshiai.data.local.entity.RecognitionEntity
import cn.edu.sicnu.cs.hln.jingshiai.data.local.entity.UserEntity
import cn.edu.sicnu.cs.hln.jingshiai.data.model.Stats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

// 统一的数据访问入口：页面只与 Repository 交互，不直接操作 DAO
class JingShiRepository(private val database: JingShiDatabase) {

    // 累计识别次数 + 游戏完成次数，数据库变化时自动推送新值
    fun observeStats(): Flow<Stats> = combine(
        database.recognitionDao().observeCount(),
        database.gameRecordDao().observeCount()
    ) { recognitionCount, gameCount ->
        Stats(recognitionCount = recognitionCount, gameCount = gameCount)
    }

    fun observeUser(): Flow<UserEntity?> = database.userDao().observeUser()

    fun observeRecognitionHistory(): Flow<List<RecognitionEntity>> =
        database.recognitionDao().observeAll()

    fun observeGameHistory(): Flow<List<GameRecordEntity>> =
        database.gameRecordDao().observeAll()

    // 识别成功后写入识别记录
    suspend fun addRecognition(objectName: String, category: String, confidence: Float) {
        database.recognitionDao().insert(
            RecognitionEntity(
                objectName = objectName,
                category = category,
                confidence = confidence,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    // 完成一局游戏后写入成绩
    suspend fun addGameRecord(score: Int, correctCount: Int, totalCount: Int) {
        database.gameRecordDao().insert(
            GameRecordEntity(
                score = score,
                correctCount = correctCount,
                totalCount = totalCount,
                createdAt = System.currentTimeMillis()
            )
        )
    }
}
