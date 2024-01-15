package com.example.directionqiblaapp.Activities

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.directionqiblaapp.MainActivity
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.databinding.ActivityOnBoarding3Binding
import com.example.directionqiblaapp.databinding.CustomDialogLocationBinding
import com.example.directionqiblaapp.databinding.CustomDialogShareAppBinding
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.material.card.MaterialCardView

class OnBoardingActivity3 : AppCompatActivity() {
    lateinit var binding:ActivityOnBoarding3Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityOnBoarding3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            if (checkLocationPermission()) {
                startNextActivity()
            } else {
                showPermissionDialog()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if the location permission is granted
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun startNextActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showPermissionDialog() {

        val dialog_binding = CustomDialogLocationBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialog_binding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)

        dialog.show()

        dialog_binding.dontAllowId.setOnClickListener {
            startNextActivity()
            dialog.dismiss()
        }

        dialog_binding.allowId.setOnClickListener {
            requestLocationPermission()

            dialog.dismiss()
        }

        dialog.show()
    }

    // Function to request location permission
    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    // Override onRequestPermissionsResult to handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                // Check if the location permission is granted
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If permission is granted, proceed with your logic
                    startNextActivity()
                } else {
                    // If permission is not granted, you may show a message or take appropriate action
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            // Handle other permission requests if needed
        }
    }

    // Add a constant for the location permission request code
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }




}