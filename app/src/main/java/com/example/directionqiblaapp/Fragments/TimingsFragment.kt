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
import java.text.SimpleDateFormat
import java.util.Calendar
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

                    val nextPrayer = getNextPrayerTime(prayersList)
                    if (nextPrayer != null) {
                        val timeDifferenceMillis = nextPrayer.second
                        val formattedTimeDifference = formatMillisToHHMM(timeDifferenceMillis)

                        Log.d(
                            "Prayers_Data",
                            "Next prayer: ${nextPrayer.first?.time} at ${nextPrayer?.second}"
                        )
                        binding.comingPrayerId.text =
                            "${nextPrayer?.first?.name} prayer in ${formattedTimeDifference}"
                    } else {
                        Log.d("Prayers_Data", "No upcoming prayer times found.")
                        binding.comingPrayerId.text = "No upcoming prayer times found"
                    }

                    binding.prayersRV.layoutManager = LinearLayoutManager(requireContext())
                    val adapter = PrayersAdapter(requireContext(), prayersList, nextPrayer?.first)
                    binding.prayersRV.adapter = adapter


                }

//                else -> {}
            }
        })
    }


    private fun getNextPrayerTime(prayersList: List<Prayer>): Pair<Prayer?, Long> {
        val currentTimeMillis = System.currentTimeMillis()
        val dateFormat12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime12 = dateFormat12.format(currentTimeMillis)

        for (prayer in prayersList) {
            val prayerTimeFormatted = convertTo12HourFormat(prayer.time)
            Log.d("TAG_prayer", "getNextPrayerTime: $prayerTimeFormatted and $formattedTime12")

            val formattedTimeDate = dateFormat12.parse(formattedTime12)
            val prayerTimeDate = dateFormat12.parse(prayerTimeFormatted)

            if (formattedTimeDate.before(prayerTimeDate)) {
                val timeDifferenceMillis = prayerTimeDate.time - formattedTimeDate.time

                return Pair(prayer, timeDifferenceMillis)
            }
            else{
                if (prayersList.isNotEmpty()) {
                    val nextDayPrayer = prayersList.first() // Assuming the list is ordered by prayer times
                    val nextDayPrayerTimeFormatted = convertTo12HourFormat(nextDayPrayer.time)
                    val formattedTimeDate = dateFormat12.parse(formattedTime12)
                    val nextDayPrayerTimeDate = dateFormat12.parse(nextDayPrayerTimeFormatted)
                    val timeDifferenceMillis = formattedTimeDate.time-nextDayPrayerTimeDate.time

                    return Pair(nextDayPrayer, timeDifferenceMillis)
                }
            }
        }


        return Pair(null, 0)
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
        val formattedTime = when {
            hours > 0 && minutes > 0 -> "$hours hr and $minutes Mins"
            hours > 0 -> "$hours hr"
            minutes > 0 -> "$minutes Mins"
            else -> "0 Mins"
        }

        return if (isNegative) "-$formattedTime" else formattedTime
    }


}

class Prayer(
    var name: String, var time: String, var selectedNotification: PrayerNotification
) : Serializable
