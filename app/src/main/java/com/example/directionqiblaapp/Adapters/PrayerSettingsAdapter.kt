package com.example.directionqiblaapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.directionqiblaapp.Fragments.PrayerNotification
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.databinding.ItemNotificationSettingsBinding

class PrayerSettingsAdapter(var ctxt: Context, var prayerNotifList: ArrayList<PrayerNotification>) :
    RecyclerView.Adapter<PrayerSettingsAdapter.viewHolder>() {

    var selectedItemPosition: Int = 0 // To keep track of the selected item position

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNotification: TextView = itemView.findViewById(R.id.text_notification)
        val notifIcon: ImageView = itemView.findViewById(R.id.notif_icon)
        val radioNotif: RadioButton = itemView.findViewById(R.id.radio_notif)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_notification_settings,
            parent,
            false
        )
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.textNotification.text = prayerNotifList[position].notificationName
        holder.notifIcon.setImageDrawable(prayerNotifList[position].notificationIcon)
        holder.radioNotif.isChecked = position == selectedItemPosition

        // Set click listener on the item
        holder.itemView.setOnClickListener {
            setSelectedItem(position)
        }
    }

    override fun getItemCount(): Int {
        return prayerNotifList.size
    }

    // Function to set the selected item and notify data set changed
    private fun setSelectedItem(position: Int) {
        selectedItemPosition = position
        notifyDataSetChanged()
    }
}

