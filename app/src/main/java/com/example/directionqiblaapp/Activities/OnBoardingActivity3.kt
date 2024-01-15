package com.example.directionqiblaapp.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.directionqiblaapp.MainActivity
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.databinding.ActivityOnBoarding3Binding
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes

class OnBoardingActivity3 : AppCompatActivity() {
    lateinit var binding:ActivityOnBoarding3Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityOnBoarding3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, OnBoardingActivity3::class.java))


            checkLocationPermission()


        }
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted, request it from the user
                showPermissionDialog()
            } else {
                // Permission is already granted

                if (isLocationEnabled(this@OnBoardingActivity3)){
                    startActivity(Intent(this, MainActivity::class.java))
                }else{
                    displayLocationSettingsRequest(this@OnBoardingActivity3)
                }

            }
        } else {
            // For devices running SDK versions below 23, permissions are granted at installation time
            // Proceed with your logic or start location-related tasks here
        }
    }

    private fun showPermissionDialog() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Location Permission Required")
        alertDialogBuilder.setMessage("This app requires location permission to function properly. Please grant the permission.")
        alertDialogBuilder.setPositiveButton("Allow") { _, _ ->
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                123
            )
        }
        alertDialogBuilder.setNegativeButton("Cancel", null)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your logic or start location-related tasks here
                if (isLocationEnabled(this@OnBoardingActivity3)) {
                    startActivity(Intent(this, MainActivity::class.java))
                }else{
                    displayLocationSettingsRequest(this@OnBoardingActivity3)
                }
            }
        }
    }

    private fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .build()
        googleApiClient.connect()

        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 10000 / 2
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback(object : ResultCallback<LocationSettingsResult> {
            override fun onResult(result: LocationSettingsResult) {
                val status: Status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS -> Log.i("TAG", "All location settings are satisfied.")
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i("TAG", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ")
                        try {
                            // Show the dialog by calling startResolutionForResult()
                            status.startResolutionForResult(
                                this@OnBoardingActivity3,
                                123
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            Log.i("TAG", "PendingIntent unable to execute request.")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                        "TAG",
                        "Location settings are inadequate, and cannot be fixed here. Dialog not created."

                    )
                }
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 123) {
            when (resultCode) {
                RESULT_OK -> {
                    Toast.makeText(this, "Location ON ", Toast.LENGTH_SHORT).show()

                }
                RESULT_CANCELED -> {
                    // User clicked "Cancel" on the location settings dialog
                    // Handle the cancellation if needed
                }
            }
        }
    }
}