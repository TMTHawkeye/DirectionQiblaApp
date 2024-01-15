package com.example.directionqiblaapp.Fragments

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.directionqiblaapp.Adapters.PrayersAdapter
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.ViewModels.PrayerTimesViewModel
import com.example.directionqiblaapp.databinding.FragmentTimingsBinding
import com.example.directionqiblaapp.retrofit.Resources
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


class TimingsFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val prayerTimesViewModel: PrayerTimesViewModel by viewModel()
    lateinit var binding: FragmentTimingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimingsBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        requestLocationUpdates()
        return binding.root
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun observePrayersTimimgs() {
        prayerTimesViewModel.prayerTimes.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resources.Failure -> {
                    Log.d("Prayers_Data", "observePrayersTimimgs: Failure, ${it.message}")
                }

                is Resources.Loading -> {
                    Log.d("Prayers_Data", "observePrayersTimimgs: Loading...")

                }

                is Resources.Success -> {
                    Log.d(
                        "Prayers_Data",
                        "observePrayersTimimgs: Success, ${it.data?.data?.timings}"
                    )
                    val prayersList = ArrayList<Prayer>()
                    val prayers = it.data?.data?.timings
                    val prayerNotification = PrayerNotification(
                        "Beep", requireContext().getDrawable(
                            R.drawable.mute_icon
                        ), false
                    )
                    prayersList.add(Prayer("Fajar", prayers?.Fajr ?: "6:00 AM", prayerNotification))
                    prayersList.add(
                        Prayer(
                            "Duhar",
                            prayers?.Dhuhr ?: "6:00 AM",
                            prayerNotification
                        )
                    )
                    prayersList.add(Prayer("Asar", prayers?.Asr ?: "6:00 AM", prayerNotification))
                    prayersList.add(
                        Prayer(
                            "Maghrib",
                            prayers?.Maghrib ?: "6:00 AM",
                            prayerNotification
                        )
                    )
                    prayersList.add(Prayer("Isha", prayers?.Isha ?: "6:00 AM", prayerNotification))

                    binding.prayersRV.layoutManager = LinearLayoutManager(requireContext())
                    binding.prayersRV.adapter = PrayersAdapter(requireContext(), prayersList)

                    val nextPrayer = getNextPrayerTime(prayersList)
                    if (nextPrayer != null) {
                        Log.d("Prayers_Data", "Next prayer: ${nextPrayer.name} at ${nextPrayer.time}")
                        binding.comingPrayerId.text="${nextPrayer?.name} prayer in ${nextPrayer?.time}"
                    } else {
                        Log.d("Prayers_Data", "No upcoming prayer times found.")
                        binding.comingPrayerId.text="No upcoming prayer times found"
                    }
                }

//                else -> {}
            }
        })
    }



    private fun getNextPrayerTime(prayersList: List<Prayer>): Prayer? {
        val currentTimeMillis = System.currentTimeMillis()
        val dateFormat12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime12 = dateFormat12.format(currentTimeMillis)

        for (prayer in prayersList) {
            val prayerTimeFormatted = convertTo12HourFormat(prayer.time)
            Log.d("TAG_prayer", "getNextPrayerTime: $prayerTimeFormatted and $formattedTime12")

            // Parse time strings to Date objects for proper comparison
            val formattedTimeDate = dateFormat12.parse(formattedTime12)
            val prayerTimeDate = dateFormat12.parse(prayerTimeFormatted)

            // Compare Date objects
            if (formattedTimeDate.before(prayerTimeDate)) {
                return prayer
            }
        }

        return null
    }

    private fun convertTo12HourFormat(time: String): String {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(time)
        return outputFormat.format(date)
    }



    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                binding.prayersRV.visibility = View.VISIBLE
                binding.locationPermissionId.visibility = View.GONE
                // Got last known location
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    val timestamp = it.time
                    val date = getCurrentDate(timestamp)

                    Log.d("data", "onResponse:$latitude , $longitude , $date")

//                    val prayerstimeApi = Retrofit.getInstance().create(PrayersTimeApi::class.java)
                    prayerTimesViewModel.getPrayerTimes(date, latitude, longitude, 2)
                    observePrayersTimimgs()
                }
            }
            .addOnFailureListener {
                Log.d("data", "onResponse:${it.message}")
                binding.prayersRV.visibility = View.GONE
                binding.locationPermissionId.visibility = View.VISIBLE
            }
    }

    private fun getCurrentDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = Date(timestamp)
        return dateFormat.format(currentDate)
    }


    private fun formatMillisToHHMM(millis: Long): String {
        val isNegative = millis < 0
        val absoluteMillis = if (isNegative) -millis else millis

        val hours = TimeUnit.MILLISECONDS.toHours(absoluteMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(absoluteMillis) % 60

        val formattedTime = String.format("%02d:%02d", hours, minutes)

        return if (isNegative) "-$formattedTime" else formattedTime
    }


}

class Prayer(
    var name: String, var time: String, var selectedNotification: PrayerNotification
) :Serializable
