package com.example.directionqiblaapp.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.directionqiblaapp.Interfaces.ComingPrayerListener
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.Service.NotificationService
import com.example.directionqiblaapp.Service.TestNotificationService
import com.example.directionqiblaapp.databinding.FragmentPrayerTimeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import io.paperdb.Paper

class PrayerTimeFragment : Fragment(), ComingPrayerListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val OVERLAY_PERMISSION_REQUEST_CODE = 123
    var comingPrayer: String = ""

    lateinit var binding: FragmentPrayerTimeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrayerTimeBinding.inflate(layoutInflater)
        comingPrayerListener = this
        return binding.root
    }

    companion object {
        var comingPrayerListener: ComingPrayerListener? = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchState = Paper.book().read<Boolean>("switchState", false)
        binding.notifSwitch.isChecked = switchState!!

//        val receivedData = arguments?.getString("sunrise")
        val timimgFragment = TimingsFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, timimgFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        binding.notifSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!hasOverlayPermission(requireContext())) {
                    requestOverlayPermission(requireActivity(), OVERLAY_PERMISSION_REQUEST_CODE)
                } else {
//                    if (!comingPrayer.isNullOrEmpty()) {
                        Log.d("TAG", "onViewCreated: $comingPrayer")
                        requireContext().startService(
                            Intent(
                                requireContext(),
                                NotificationService::class.java
                            ).putExtra(NotificationService.EXTRA_PRAYER_TEXT,comingPrayer)
                        )
//                    }
//                    requireContext().startService(Intent(requireContext(), TestNotificationService::class.java))
                }
            } else {
                if (!comingPrayer.isNullOrEmpty()) {
                    requireContext().stopService(
                        Intent(
                            requireContext(),
                            NotificationService::class.java
                        )
                    )
//                requireContext().stopService(Intent(requireContext(), TestNotificationService::class.java))
                }
            }

            Paper.book().write("switchState", isChecked)

        }

    }

    fun requestOverlayPermission(activity: Activity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.packageName)
            )
            activity.startActivityForResult(intent, requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (hasOverlayPermission(requireContext())) {
                requireContext().startService(
                    Intent(
                        requireContext(),
                        TestNotificationService::class.java
                    )
                )
            }
        } else {
            binding.notifSwitch.isChecked = false
        }
    }


    fun hasOverlayPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context)
        }
        return true // On versions below Marshmallow, overlay permission is not required.
    }

    override fun onComingPrayerTextChanged(text: String) {
        comingPrayer = text
    }


}


