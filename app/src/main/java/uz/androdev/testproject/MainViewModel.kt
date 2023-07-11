package uz.androdev.testproject

import android.content.ContentUris
import android.content.Context
import android.provider.CalendarContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import uz.androdev.testproject.db.AppDatabase
import uz.androdev.testproject.db.AppSpecificEventIdEntity

/**
 * Created by: androdev
 * Date: 11-07-2023
 * Time: 12:02 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

class MainViewModel : ViewModel() {
    private val _creatingEventsStream = MutableStateFlow(false)
    private val _deletingEventsStream = MutableStateFlow(false)
    val isProcessing: Flow<Boolean> =
        _creatingEventsStream.combine(_deletingEventsStream, Boolean::or)

    private val _event = MutableStateFlow<MainScreenEvent?>(null)
    val event get() = _event.asStateFlow()

    fun createCalendarEvents(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        if (_creatingEventsStream.value || _deletingEventsStream.value) return@launch
        _creatingEventsStream.emit(true)

        val contentResolver = context.contentResolver
        val calendarId = contentResolver.getPrimaryGoogleCalendarId()

        if (calendarId == null) {
            _event.emit(MainScreenEvent.GoogleCalendarNotFound)
        } else {
            repeat(3) {
                val createdEventId = contentResolver.createEventInCalendar(calendarId)

                createdEventId?.let {
                    val eventIdEntity = AppSpecificEventIdEntity(createdEventId)
                    AppDatabase.getInstance(context).appSpecificEventIdDao.insert(eventIdEntity)
                }
            }
            _event.emit(MainScreenEvent.AppCalendarEventsCreated)
        }

        _creatingEventsStream.emit(false)
    }


    fun deleteCalendarEvents(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        if (_creatingEventsStream.value || _deletingEventsStream.value) return@launch

        _deletingEventsStream.emit(true)

        val dao = AppDatabase.getInstance(context).appSpecificEventIdDao
        val appCreateEvents = dao.getEventIdsStream().first()
        appCreateEvents.forEach { entity ->
            val eventId = entity.eventId
            val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
            context.contentResolver.delete(uri, null, null)

            dao.remove(entity)
        }
        _event.emit(MainScreenEvent.AppCalendarEventsDeleted)

        _deletingEventsStream.emit(false)

    }

    fun setScreenEventConsumed() = viewModelScope.launch {
        _event.emit(null)
    }
}

sealed interface MainScreenEvent {
    object AppCalendarEventsDeleted : MainScreenEvent
    object AppCalendarEventsCreated : MainScreenEvent
    object AppCalendarEventsCreateFailed : MainScreenEvent
    object GoogleCalendarNotFound : MainScreenEvent
}