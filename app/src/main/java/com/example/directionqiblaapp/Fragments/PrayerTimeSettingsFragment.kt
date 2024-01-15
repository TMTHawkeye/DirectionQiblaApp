package com.example.directionqiblaapp.Fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.directionqiblaapp.Adapters.PrayerSettingsAdapter
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.databinding.FragmentPrayerTimeSettingsBinding

class PrayerTimeSettingsFragment : Fragment() {
    lateinit var binding: FragmentPrayerTimeSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPrayerTimeSettingsBinding.inflate(layoutInflater)
        val prayerData = arguments?.getSerializable("prayerData") as Prayer

        var prayerNotifList=getPrayerNotificationList()
        binding.prayersRV.layoutManager=LinearLayoutManager(requireContext())
        binding.prayersRV.adapter=PrayerSettingsAdapter(requireContext(),prayerNotifList,prayerData)


//        binding.textViewPrayerName.text = prayerName
        return binding.root
    }

    fun getPrayerNotificationList():ArrayList<PrayerNotification>{
        val prayersList = ArrayList<PrayerNotification>()
        prayersList.add(PrayerNotification("Mute",requireContext().getDrawable(R.drawable.mute_icon),false ))
        prayersList.add(PrayerNotification("Vibrate", requireContext().getDrawable(R.drawable.vibrate_icon),false ))
        prayersList.add(PrayerNotification("Beep", requireContext().getDrawable(R.drawable.beep_icon),false ))
        prayersList.add(PrayerNotification("Takbeer", requireContext().getDrawable(R.drawable.takbeer_icon),false ))
        prayersList.add(PrayerNotification("Full Adhan", requireContext().getDrawable(R.drawable.takbeer_icon),false ))
        return prayersList
    }
}

data class PrayerNotification(
    val notificationName: String,
    val notificationIcon: Drawable?,
    val notificationState: Boolean
)