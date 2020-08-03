package cu.lexz451.rentmanager.utils

import android.os.Environment
import android.util.Log
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class ExceptionHandler(localPath: String?) : Thread.UncaughtExceptionHandler {
    private var defaultUEH: Thread.UncaughtExceptionHandler? = null
    private var localPath: String? = null

    init {
        this.localPath = localPath
        //Getting the the default exception handler
        //that's executed when uncaught exception terminates a thread
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler()
    }

    override fun uncaughtException(
        t: Thread?,
        e: Throwable
    ) {
        //Write a printable representation of this Throwable
        //The StringWriter gives the lock used to synchronize access to this writer.
        val stringBuffSync: Writer = StringWriter()
        val printWriter = PrintWriter(stringBuffSync)
        e.printStackTrace(printWriter)
        val stacktrace: String = stringBuffSync.toString()
        printWriter.close()
        if (localPath != null) {
            writeToFile(stacktrace)
        }
        //Used only to prevent from any code getting executed.
        // Not needed in this example
        t?.let {
            defaultUEH?.uncaughtException(t, e)
        }
    }

    private fun writeToFile(currentStacktrace: String) {
        try { //Gets the Android external storage directory & Create new folder Crash_Reports
            val dir = File(
                Environment.getExternalStorageDirectory(),
                "Crash_Reports"
            )
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val dateFormat = SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss",
                Locale.getDefault()
            )
            val date = Date()
            val filename: String = dateFormat.format(date).toString() + ".STACKTRACE"
            // Write the file into the folder
            val reportFile = File(dir, filename)
            val fileWriter = FileWriter(reportFile)
            fileWriter.append(currentStacktrace)
            fileWriter.flush()
            fileWriter.close()
        } catch (e: Exception) {
            Log.e("ExceptionHandler", e.message ?: "")
        }
    }
}