package com.tohed.islampro.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tohed.islampro.R
import org.json.JSONObject

class NotificationAdapter(
    private val notifications: List<JSONObject>,
    private val listener: OnNotificationClickListener
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    interface OnNotificationClickListener {
        fun onNotificationClick(position: Int)
        fun onNotificationDelete(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_item, parent, false)
        return NotificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.titleTextView.text = notification.getString("title")
        holder.bodyTextView.text = notification.getString("body")

        val isRead = if (notification.has("read")) {
            notification.getBoolean("read")
        } else {
            false  // Default to unread
        }

        if (isRead) {
            holder.titleTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.gray))
        } else {
            holder.titleTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
        }

        holder.itemView.setOnClickListener {
            listener.onNotificationClick(position)
        }
    }

    override fun getItemCount() = notifications.size

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.notificationTitle)
        val bodyTextView: TextView = itemView.findViewById(R.id.notificationBody)

    }
}
