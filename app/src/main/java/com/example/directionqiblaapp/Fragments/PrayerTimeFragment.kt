package com.example.directionqiblaapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.databinding.FragmentPrayerTimeBinding
import com.google.android.gms.location.FusedLocationProviderClient

class PrayerTimeFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var binding: FragmentPrayerTimeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrayerTimeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receivedData = arguments?.getString("sunrise")
        val timimgFragment = TimingsFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, timimgFragment)
        transaction.addToBackStack(null)
        transaction.commit()


//        binding.service.setOnClickListener {
//
//            GlobalScope.launch(Dispatchers.IO ) {
//
//                delay(5000)
//                startStopService()
//            }
//
//            /*  val code = 100
//              val alarmManager = requireContext().getSystemService(ALARM_SERVICE) as AlarmManager
//
//                 // val time: Int = Integer.parseInt(binding.time.text.toString())
//                  val triggerTime: Long = System.currentTimeMillis() + (10 * 1000)
//
//                  val iBroadcast = Intent(requireContext(), AlarmReceiver::class.java)
//                  val pendingIntent = PendingIntent.getBroadcast(requireContext(), code, iBroadcast, PendingIntent.FLAG_IMMUTABLE)
//
//                  alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)*/
//        }
    }



}


