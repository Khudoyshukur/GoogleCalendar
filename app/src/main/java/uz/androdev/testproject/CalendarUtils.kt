package uz.androdev.testproject

import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.provider.CalendarContract
import java.util.TimeZone
import java.util.UUID

/**
 * Created by: androdev
 * Date: 11-07-2023
 * Time: 3:01 PM
 * Email: Khudoyshukur.Juraev.001@mail.ru
 */

fun ContentResolver.getPrimaryGoogleCalendarId(): Long? {
    val eventProjection: Array<String> = arrayOf(
        CalendarContract.Calendars._ID,
        CalendarContract.Calendars.IS_PRIMARY,
    )
    val selection = "${CalendarContract.Calendars.ACCOUNT_TYPE}=?"
    val selectionArgs = arrayOf("com.google")

    this@getPrimaryGoogleCalendarId.query(
        CalendarContract.Calendars.CONTENT_URI,
        eventProjection,
        selection,
        selectionArgs,
        null
    )?.use { cursor ->
        while (cursor.moveToNext()) {
            val calID: Long = cursor.getLong(0)
            val isPrimary: Boolean = cursor.getInt(1) == 1

            if (isPrimary) {
                return calID
            }
        }
    }

    return null
}

fun ContentResolver.createEventInCalendar(calendarId: Long): Long? {
    val randomUUID = UUID.randomUUID()

    val eventValues = ContentValues().apply {
        put(CalendarContract.Events.CALENDAR_ID, calendarId)
        put(CalendarContract.Events.TITLE, "Event $randomUUID")
        put(CalendarContract.Events.DESCRIPTION, "Event Description $randomUUID")
        put(CalendarContract.Events.DTSTART, System.currentTimeMillis())
        put(CalendarContract.Events.DTEND, System.currentTimeMillis() + 3_600_000)
        put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
    }

    val eventUri: Uri? = this.insert(CalendarContract.Events.CONTENT_URI, eventValues)

    return eventUri?.lastPathSegment?.toLongOrNull()
}