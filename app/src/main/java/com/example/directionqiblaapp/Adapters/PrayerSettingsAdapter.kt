package com.example.directionqiblaapp.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.directionqiblaapp.Fragments.Prayer
import com.example.directionqiblaapp.Fragments.PrayerNotification
import com.example.directionqiblaapp.Fragments.PrayerTimeSettingsFragment
import com.example.directionqiblaapp.Fragments.TimingsFragment
import com.example.directionqiblaapp.R
import io.paperdb.Paper

class PrayerSettingsAdapter(
    var ctxt: Context,
    var prayerNotifList: ArrayList<PrayerNotification>,
    var prayerData: Prayer,
) :
    RecyclerView.Adapter<PrayerSettingsAdapter.viewHolder>() {
    private var selectedPosition = 0

    init {
        selectNotificationForPrayer(prayerData.name)
    }
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

        // Set the checked state based on the selected position
        holder.radioNotif.isChecked = position == selectedPosition

        // Set a click listener for the radio button
        holder.radioNotif.setOnClickListener {
            saveSelectedNotificationPosition(position)
            // Update the selected position
            selectedPosition = holder.adapterPosition
            saveSelectedNotificationMethod(prayerNotifList[selectedPosition].notificationName)

            val prayerSettingsFragment = TimingsFragment()

            val fragmentManager = (ctxt as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.container, prayerSettingsFragment)
                .addToBackStack(null)
                .commit()
            // Notify the adapter about the data set changes
            notifyDataSetChanged()
        }
    }

    private fun saveSelectedNotificationMethod(notificationMethod: String) {
        Paper.book().write("selectedNotificationMethod", notificationMethod)
        Log.d("PrayerSettingsAdapter", "Selected notification method: $notificationMethod")
    }

    private fun saveSelectedNotificationPosition(position: Int) {
        Paper.book().write("selectedNotificationPosition", position)
    }


    override fun getItemCount(): Int {
        return prayerNotifList.size
    }

    fun selectNotificationForPrayer(prayerName: String) {
        val savedPosition=getSelectedNotificationPosition()
        val index = prayerNotifList.indexOfFirst { it.notificationName == prayerNotifList.get(savedPosition!!).notificationName }
        if (index != -1) {
            selectedPosition = index
            Log.d("TAG", "selectNotificationForPrayer: $index")
            notifyDataSetChanged()
        }
    }

    fun getSelectedNotificationPosition() : Int?{
        return Paper.book().read<Int>("selectedNotificationPosition",0)
    }



}

