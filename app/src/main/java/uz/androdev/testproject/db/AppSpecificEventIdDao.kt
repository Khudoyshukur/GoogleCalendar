package uz.androdev.testproject.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Created by: androdev
 * Date: 11-07-2023
 * Time: 2:09 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Dao
interface AppSpecificEventIdDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(eventIdEntity: AppSpecificEventIdEntity)

    @Delete()
    suspend fun remove(eventIdEntity: AppSpecificEventIdEntity)

    @Query("SELECT * FROM app_specific_events")
    fun getEventIdsStream(): Flow<List<AppSpecificEventIdEntity>>
}