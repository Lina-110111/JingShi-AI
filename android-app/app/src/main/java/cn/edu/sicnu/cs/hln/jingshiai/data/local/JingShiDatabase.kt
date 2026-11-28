package cn.edu.sicnu.cs.hln.jingshiai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.edu.sicnu.cs.hln.jingshiai.data.local.dao.GameRecordDao
import cn.edu.sicnu.cs.hln.jingshiai.data.local.dao.RecognitionDao
import cn.edu.sicnu.cs.hln.jingshiai.data.local.dao.UserDao
import cn.edu.sicnu.cs.hln.jingshiai.data.local.entity.GameRecordEntity
import cn.edu.sicnu.cs.hln.jingshiai.data.local.entity.RecognitionEntity
import cn.edu.sicnu.cs.hln.jingshiai.data.local.entity.UserEntity

@Database(
    entities = [
        RecognitionEntity::class,
        GameRecordEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class JingShiDatabase : RoomDatabase() {

    abstract fun recognitionDao(): RecognitionDao

    abstract fun gameRecordDao(): GameRecordDao

    abstract fun userDao(): UserDao

    companion object {
        private const val DB_NAME = "jingshi_ai.db"

        @Volatile
        private var instance: JingShiDatabase? = null

        fun getInstance(context: Context): JingShiDatabase =
            instance ?: synchronized(this) {
                instance ?: create(context).also { instance = it }
            }

        private fun create(context: Context): JingShiDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                JingShiDatabase::class.java,
                DB_NAME
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // 首次创建数据库时写入默认本地用户资料
                        db.execSQL(
                            "INSERT INTO user_profile (id, nickname, bio, created_at) " +
                                "VALUES (1, '净识用户', '让垃圾分类更简单', 0)"
                        )
                    }
                })
                .build()
    }
}
