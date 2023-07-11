package uz.androdev.testproject.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by: androdev
 * Date: 11-07-2023
 * Time: 2:07 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

@Entity(tableName = "app_specific_events")
data class AppSpecificEventIdEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "event_id")
    val eventId: Long
)