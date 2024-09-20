package com.tohed.islampro.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import com.onesignal.OneSignal
import com.onesignal.OSNotificationOpenedResult
import com.tohed.islampro.ui.activities.HomeActivity
import org.json.JSONArray
import org.json.JSONObject

val ONESIGNAL_APP_ID = "98706fc4-4c36-4e2b-9a55-0bd425ab2c24"

class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()

        // OneSignal Initialization
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

        // Handle when a notification is opened
        OneSignal.setNotificationOpenedHandler { result: OSNotificationOpenedResult ->
            saveNotification(result.notification.title, result.notification.body)

            val intent = Intent(applicationContext, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
        }

        // Handle when a notification is received
        OneSignal.setNotificationWillShowInForegroundHandler { notificationReceivedEvent ->
            val notification = notificationReceivedEvent.notification
            saveNotification(notification.title, notification.body)
            notificationReceivedEvent.complete(notification)
        }
    }

    // Save the notification in SharedPreferences with read/unread status
    private fun saveNotification(title: String?, body: String?) {
        val sharedPref = getSharedPreferences("notifications", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Get the existing notifications as JSON array
        val notificationsJson = sharedPref.getString("notifications", "[]")
        val notificationsArray = JSONArray(notificationsJson)

        // Create a new notification JSON object with a 'read' flag
        val notificationObj = JSONObject().apply {
            put("title", title)
            put("body", body)
            put("read", false)  // New notifications are unread by default
        }

        // Add the new notification to the array
        notificationsArray.put(notificationObj)

        // Save the updated array back to SharedPreferences
        editor.putString("notifications", notificationsArray.toString())
        editor.apply()
    }
}