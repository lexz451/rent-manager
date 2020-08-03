package cu.lexz451.rentmanager.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.Room
import cu.lexz451.rentmanager.utils.AlarmScheduler
import cu.lexz451.rentmanager.utils.ExceptionHandler
import cu.lexz451.rentmanager.utils.NotificationHelper
import cu.lexz451.rentmanager.utils.setupWithNavController
import kotlinx.android.synthetic.main.activity_manager.*

class ManagerActivity : AppCompatActivity() {

    private val navGraphIds = listOf(
        R.navigation.rooms,
        R.navigation.reports,
        R.navigation.settings)

    private var currentController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)
        setSupportActionBar(toolbar)
        if (savedInstanceState == null) {
            setupNavigation()
        }
        handleNotificationIntent(intent)

        Thread.setDefaultUncaughtExceptionHandler(
            ExceptionHandler(Environment.getExternalStorageDirectory().path)
        )

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            handleNotificationIntent(intent)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupNavigation()
    }

    private fun handleNotificationIntent(intent: Intent) {
        if (intent.action === getString(R.string.action_notify_room_done) && intent.type === "type-remove") {
            if (intent.extras != null) {
                val roomID = intent.extras!!.get("id") as Int
                Log.d("Alarm", "Removing notification from Activity")
                AlarmScheduler.cancelAlarm(this, roomID)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentController?.value?.navigateUp() ?: false
    }

    private fun setupNavigation() {
        val controller = navigation.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.container,
            intent = intent
        )
        controller.observe(this, Observer {
            setupActionBarWithNavController(it)
        })
        currentController = controller
    }
}
