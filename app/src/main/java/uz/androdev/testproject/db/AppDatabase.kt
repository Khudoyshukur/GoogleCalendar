package uz.androdev.testproject.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Created by: androdev
 * Date: 11-07-2023
 * Time: 2:06 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Database(
    entities = [
        AppSpecificEventIdEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract val appSpecificEventIdDao: AppSpecificEventIdDao

    companion object {
        private const val DATABASE_NAME = "app_database.db"

        private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            DATABASE_NAME
                        ).build()
                    }
                }
            }
            return instance!!
        }
    }
}