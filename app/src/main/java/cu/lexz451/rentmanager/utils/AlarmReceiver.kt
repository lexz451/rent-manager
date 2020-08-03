package cu.lexz451.rentmanager.utils

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import cu.lexz451.rentmanager.R

class AlarmReceiver : BroadcastReceiver() {

    val TAG = AlarmReceiver::class.java.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "OnReceive called with context: [$context], intent: [$intent]")
        if (context != null && intent != null && intent.action != null) {
            if (intent.action!!.equals(context.getString(R.string.action_notify_room), ignoreCase = true)) {
                if (intent.extras != null) {
                    val roomID = intent.extras!!.get("id") as Int
                    val title = intent.extras!!.get("title") as String
                    val content = intent.extras!!.get("content") as String
                    NotificationHelper.createNotificationForRoom(context, roomID, title, content)
                }
            }
        }
    }
}