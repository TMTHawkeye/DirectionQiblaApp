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
import com.example.directionqiblaapp.ViewModels.PrayerTimesViewModel
import com.example.directionqiblaapp.databinding.FragmentTimingsBinding
import com.example.directionqiblaapp.retrofit.Resources
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.ext.android.viewModel
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
        binding= FragmentTimingsBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        requestLocationUpdates()
        return binding.root
    }

    private fun convertTo12HourFormat(time24: String?): String {
        val dateFormat24 = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat12 = SimpleDateFormat("hh:mm a", Locale.getDefault())

        return try {
            val date = dateFormat24.parse(time24)
            dateFormat12.format(date)
        } catch (e: ParseException) {
            Log.e("PrayersTimeFragment", "Error converting time", e)
            time24 ?: ""
        }
    }

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
                    prayersList.add(Prayer("Fajar", prayers?.Fajr ?: "6:00 AM"))
                    prayersList.add(Prayer("Duhar", prayers?.Dhuhr ?: "6:00 AM"))
                    prayersList.add(Prayer("Asar", prayers?.Asr ?: "6:00 AM"))
                    prayersList.add(Prayer("Maghrib", prayers?.Maghrib ?: "6:00 AM"))
                    prayersList.add(Prayer("Isha", prayers?.Isha ?: "6:00 AM"))

                    val remainingTimePrayer = getRemainingTimeAndNameForNextPrayer(prayersList)

                    remainingTimePrayer?.let { (prayerName, remainingTime) ->
                        Log.d(
                            "PrayerTimeFragment",
                            "Next prayer: $prayerName in $remainingTime"
                        )

                        val tt = convertTo12HourFormat(remainingTime)
                        binding.comingPrayerId.text = "$prayerName prayer in $tt"
                        // Use prayerName and remainingTime as needed
                    } ?: Log.d("PrayerTimeFragment", "No upcoming prayer found")


                    binding.prayersRV.layoutManager = LinearLayoutManager(requireContext())
                    binding.prayersRV.adapter = PrayersAdapter(requireContext(), prayersList)

                }

//                else -> {}
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                binding.prayersRV.visibility=View.VISIBLE
                binding.locationPermissionId.visibility=View.GONE
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
                binding.prayersRV.visibility=View.GONE
                binding.locationPermissionId.visibility=View.VISIBLE
            }
    }

    private fun getCurrentDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = Date(timestamp)
        return dateFormat.format(currentDate)
    }

    private fun getRemainingTimeAndNameForNextPrayer(prayersList: List<Prayer>): Pair<String, String>? {
        val currentTime = System.currentTimeMillis()
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())

        for (prayer in prayersList) {
            try {
                val prayerTime = sdf.parse(prayer.time)?.time ?: 0

                Log.d(
                    "PrayerTimeFragment",
                    "Current Time: $currentTime and Prayer Time: $prayerTime"
                )

                val timeDifference = prayerTime - currentTime

                Log.d("PrayerTimeFragment", "Time Difference: $timeDifference milliseconds")

                if (timeDifference > 0) {
                    val remainingTime = formatMillisToHHMM(timeDifference)
                    Log.d("PrayerTimeFragment", "Remaining time: $remainingTime")
                    return Pair(prayer.name, remainingTime)
                } else {
                    Log.d("PrayerTimeFragment", "Prayer time has already passed for the day")
                    val nextDayPrayerTime = sdf.parse(prayersList[0].time)?.time ?: 0
                    val nextDayTimeDifference = currentTime - nextDayPrayerTime

                    val remainingTimeNextDay = formatMillisToHHMM(nextDayTimeDifference)
                    Log.d("PrayerTimeFragment", "Next day remaining time: $remainingTimeNextDay")
                    return Pair(prayersList[0].name, remainingTimeNextDay)
                }


            } catch (e: ParseException) {
                Log.e("PrayerTimeFragment", "Error parsing prayer time", e)
            }
        }

        return null
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

class Prayer(var name: String, var time: String)
