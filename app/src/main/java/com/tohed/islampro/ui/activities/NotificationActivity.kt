package com.tohed.islampro.ui.activities

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tohed.islampro.R
import com.tohed.islampro.adapters.NotificationAdapter
import org.json.JSONArray
import org.json.JSONObject
import android.widget.Button
import android.widget.TextView

class NotificationActivity : AppCompatActivity(), NotificationAdapter.OnNotificationClickListener {

    private lateinit var adapter: NotificationAdapter
    private lateinit var notificationsArray: JSONArray
    private lateinit var deleteAllButton: TextView  // Declare the button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        // Initialize the "Delete All" button
        deleteAllButton = findViewById(R.id.deleteAllButton)

        // Retrieve notifications from SharedPreferences
        val sharedPref = getSharedPreferences("notifications", Context.MODE_PRIVATE)
        val notificationsJson = sharedPref.getString("notifications", "[]")
        notificationsArray = JSONArray(notificationsJson)
        val notificationsList = mutableListOf<JSONObject>()

        // Add all JSON objects from the array to the list
        for (i in 0 until notificationsArray.length()) {
            notificationsList.add(notificationsArray.getJSONObject(i))
        }

        // Check if there are any notifications
        if (notificationsList.isEmpty()) {
            Toast.makeText(this, "فی الحال کوئی نئی اطلاع موجود نہیں!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // Setup RecyclerView
            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            adapter = NotificationAdapter(notificationsList, this)
            recyclerView.adapter = adapter
        }

        // Set the click listener for the "Delete All" button
        deleteAllButton.setOnClickListener {
            // Clear all notifications
            notificationsArray = JSONArray()  // Create an empty JSON array
            saveNotifications()               // Save the empty array to SharedPreferences
            adapter.notifyDataSetChanged()    // Notify the adapter about the changes

            // Show a message that all notifications are deleted
            Toast.makeText(this, "All notifications deleted", Toast.LENGTH_SHORT).show()

            // Optionally, finish the activity if no notifications are left
            finish()
        }
    }

    // Handle marking a notification as read
    override fun onNotificationClick(position: Int) {
        val notification = notificationsArray.getJSONObject(position)
        notification.put("read", true)  // Mark the notification as read
        saveNotifications()
        adapter.notifyItemChanged(position)
    }

    // Handle deleting a notification
    override fun onNotificationDelete(position: Int) {
        notificationsArray.remove(position)
        saveNotifications()
        adapter.notifyItemRemoved(position)
    }

    // Save updated notifications back to SharedPreferences
    private fun saveNotifications() {
        val sharedPref = getSharedPreferences("notifications", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("notifications", notificationsArray.toString())
        editor.apply()
    }
}
