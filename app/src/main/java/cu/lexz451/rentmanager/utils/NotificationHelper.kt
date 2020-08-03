package cu.lexz451.rentmanager.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.ui.ManagerActivity

object NotificationHelper {

    fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean, name: String, description: String) {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        val channelId = "${context.packageName}-$name"
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = description
        channel.setShowBadge(showBadge)

        // Register the channel with the system
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    fun createSampleDataNotification(context: Context, title: String, message: String,
                                     bigText: String, autoCancel: Boolean) {

        val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"
        val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_warning_black_24dp)
            setContentTitle(title)
            setContentText(message)
            setAutoCancel(autoCancel)
            setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            priority = NotificationCompat.PRIORITY_HIGH

            val intent = Intent(context, ManagerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            setContentIntent(pendingIntent)
        }

        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(1001, notificationBuilder.build())
    }

    fun createNotificationForRoom(context: Context, roomId: Int, title: String, content: String) {
        val notificationBuilder = buildNotificationForRoom(context, roomId, title, content)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(roomId, notificationBuilder.build())
    }

    private fun buildNotificationForRoom(context: Context, roomId: Int, title: String, content: String): NotificationCompat.Builder {

        val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"

        return NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_warning_black_24dp)
            setContentTitle(title)
            setAutoCancel(true)
            setContentText(content)
            setGroup(roomId.toString())

            // Launches the app to open the reminder edit screen when tapping the whole notification
            val intent = Intent(context, ManagerActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                action = context.getString(R.string.action_notify_room_done)
                type = "type-remove"
                putExtra("id", roomId)
            }

            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            setContentIntent(pendingIntent)
        }
    }

}