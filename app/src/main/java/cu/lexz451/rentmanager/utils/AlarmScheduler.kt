package cu.lexz451.rentmanager.utils

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.Client
import cu.lexz451.rentmanager.data.model.Room
import cu.lexz451.rentmanager.ui.ManagerActivity
import org.dizitart.no2.objects.Id
import java.util.*
import java.util.Calendar.*

object AlarmScheduler {

    fun scheduleAlarmForReminder(
        context: Context,
        roomId: Int,
        title: String,
        content: String,
        hour: Int,
        minute: Int) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = createPendingIntent(context, roomId, title, content)
        scheduleAlarm(hour, minute, alarmIntent, alarmMgr)
    }

    private fun scheduleAlarm(hour: Int, minute: Int, alarmIntent: PendingIntent?, alarmMgr: AlarmManager) {

        if (alarmIntent == null) return

        val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
        datetimeToAlarm.timeInMillis = System.currentTimeMillis()
        datetimeToAlarm.set(HOUR_OF_DAY, hour)
        datetimeToAlarm.set(MINUTE, minute)
        datetimeToAlarm.set(SECOND, 0)
        datetimeToAlarm.set(MILLISECOND, 0)

        // Remove the alarm if timeout
        val currentDateTime = Calendar.getInstance(Locale.getDefault()).apply {
            timeInMillis = System.currentTimeMillis()
        }

        if (currentDateTime.after(datetimeToAlarm)) {
            alarmMgr.cancel(alarmIntent)
            alarmIntent.cancel()
            return
        }

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, datetimeToAlarm.timeInMillis, 60000, alarmIntent)

        Log.d("Schedule Alarm for: ", datetimeToAlarm.time.toString())
    }

    fun cancelAlarm(context: Context, roomId: Int) {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            action = context.getString(R.string.action_notify_room)
            type = "type-${roomId}"
        }
        val alarmIntent = PendingIntent.getBroadcast(context, roomId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(alarmIntent)
        alarmIntent.cancel()
    }

    private fun createPendingIntent(context: Context, roomId: Int, title: String, content: String): PendingIntent? {
        // create the intent using a unique type
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            action = context.getString(R.string.action_notify_room)
            type = "type-${roomId}"
            putExtra("id", roomId)
            putExtra("title", title)
            putExtra("content", content)
        }

        return PendingIntent.getBroadcast(context, roomId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}