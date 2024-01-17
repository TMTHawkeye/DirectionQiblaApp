package com.example.directionqiblaapp.Fragments

import android.Manifest
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.directionqiblaapp.Activities.AdManager
import com.example.directionqiblaapp.Activities.CalenderActivity
import com.example.directionqiblaapp.BuildConfig
import com.example.directionqiblaapp.MainActivity
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.databinding.CustomDialogLocationBinding
import com.example.directionqiblaapp.databinding.FragmentQiblaDirectionBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import io.paperdb.Paper
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class QiblaDirectionFragment : Fragment(), SensorEventListener, LocationListener {
    lateinit var binding: FragmentQiblaDirectionBinding
    var lockedSkinsList:MutableList<Int>?=null

    private var vibrator: Vibrator? = null
    private var sensorManager: SensorManager? = null
    private var locationManager: LocationManager? = null
    private var userLocation: Location? = null
    private var isBottomSheetVisible = false
    var skinsList = ArrayList<Skins>()
    private var currentSkinIndex = 0
    private var isLoading = false

    private var renderScript: RenderScript? = null
    private var blurScript: ScriptIntrinsicBlur? = null

    private var rewardedAd: RewardedAd? = null


    private val kaabaLocation = Location("Kaaba").apply {
        latitude = 21.3891
        longitude = 39.8579
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQiblaDirectionBinding.inflate(layoutInflater)
        AdManager.getInstance().loadNativeAd(
            requireContext(),
            BuildConfig.native_home,
            binding.adFrame,
            binding.shimmerViewContainer
        )
        initSkinsList()
        // Get the skins for the current index
        val currentSkins = skinsList[currentSkinIndex]
        lockedSkinsList= Paper.book().read<MutableList<Int>>("PAPER_KEY_UNLOCKED_SKINS",null)
        if(lockedSkinsList==null) {
            lockedSkinsList = mutableListOf(4, 5, 6, 7, 8)
        }
        updateRewardAdVisibility()


        // Update the outer and inner skins
        binding.outerCore.setImageDrawable(currentSkins.outerSkin)
        binding.innerCompassId.setImageDrawable(currentSkins.innerSkin)

        renderScript = RenderScript.create(requireContext())
        blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

        return binding.root
    }

    private fun initSkinsList() {
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_compass_1),
                requireContext().getDrawable(R.drawable.campass_img)
            )
        )

        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_compass_2),
                requireContext().getDrawable(R.drawable.inner_compass_2)
            )
        )
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_compass_1),
                requireContext().getDrawable(R.drawable.inner_compass_3)
            )
        )
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_compass_3),
                requireContext().getDrawable(R.drawable.inner_compass_3)
            )
        )
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_compass_3),
                requireContext().getDrawable(R.drawable.inner_compass_4)
            )
        )
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_compass_2),
                requireContext().getDrawable(R.drawable.inner_compass_4)
            )
        )
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_compass_4),
                requireContext().getDrawable(R.drawable.inner_compass_5)
            )
        )
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_compass_1),
                requireContext().getDrawable(R.drawable.inner_compass_6)
            )
        )
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_compass_2),
                requireContext().getDrawable(R.drawable.inner_compass_6)
            )
        )


    }

    private fun updateRewardAdVisibility() {
        if (lockedSkinsList!!.contains(currentSkinIndex)) {
            binding.rewardAdId.visibility = View.VISIBLE
            binding.textView.text = getString(R.string.locked)
//            blurImageView(binding.innerCompassId, 25f)

            Blurry.with(requireContext())
                .radius(25)
                .sampling(2)
                .async()
                .animate(500)
                .capture(binding.innerCompassId)
                .into(binding.innerCompassId)

        } else {
            binding.rewardAdId.visibility = View.GONE
            unblurImageView(binding.innerCompassId)


        }
    }

    private fun blurImageView(imageView: ImageView, radius: Float) {
        val drawable = imageView.drawable
        val bitmap = drawableToBitmap(drawable)
        val blurredBitmap = blurBitmap(bitmap, radius)
        val blurredDrawable = BitmapDrawable(resources, blurredBitmap)
        imageView.setImageDrawable(blurredDrawable)
    }

    private fun unblurImageView(imageView: ImageView) {
        // Reset the image to its original state (unblurred)
        imageView.setImageDrawable(skinsList[currentSkinIndex].innerSkin)
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun blurBitmap(bitmap: Bitmap, radius: Float): Bitmap {
        val input = Allocation.createFromBitmap(renderScript, bitmap)
        val output = Allocation.createTyped(renderScript, input.type)
        blurScript?.setRadius(radius)
        blurScript?.forEach(output)
        output.copyTo(bitmap)
        return bitmap
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Request location updates
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val lastKnownLocation =
                locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                onLocationChanged(lastKnownLocation)
            } else {
                // Request location updates with adjusted parameters
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000, // Update interval in milliseconds (e.g., 5 seconds)
                    10f,   // Update distance in meters (e.g., 10 meters)
                    this
                )
            }

        }

        if (rewardedAd == null && !isLoading) {
            loadRewardedAd()
        }

        binding.hijriCalenderCardId.setOnClickListener {
            startActivity(Intent(requireContext(), CalenderActivity::class.java))
        }

        binding.nextCompassId.setOnClickListener {
            if (currentSkinIndex == skinsList.size - 1) {
//                Toast.makeText(
//                    requireContext(),
//                    "No more skins available",
//                    Toast.LENGTH_SHORT
//                ).show()
            } else {
                currentSkinIndex = (currentSkinIndex + 1) % skinsList.size
                val currentSkins = skinsList[currentSkinIndex]
                binding.outerCore.setImageDrawable(currentSkins.outerSkin)
                binding.innerCompassId.setImageDrawable(currentSkins.innerSkin)
                updateRewardAdVisibility()

            }
        }

        binding.previousCompassId.setOnClickListener {
            if (currentSkinIndex == 0) {
//                Toast.makeText(
//                    requireContext(),
//                    "No more previous skins available",
//                    Toast.LENGTH_SHORT
//                ).show()
            } else {
                currentSkinIndex = (currentSkinIndex - 1 + skinsList.size) % skinsList.size
                val currentSkins = skinsList[currentSkinIndex]
                binding.outerCore.setImageDrawable(currentSkins.outerSkin)
                binding.innerCompassId.setImageDrawable(currentSkins.innerSkin)
                updateRewardAdVisibility()

            }
        }
        loadRewardedAd()

        binding.rewardAdId.setOnClickListener {
            showRewardedVideo()
        }


    }

    private fun loadRewardedAd() {
        if (rewardedAd == null) {
            isLoading = true

            var adRequest = AdRequest.Builder().build()
            RewardedAd.load(requireContext(), "ca-app-pub-3940256099942544/5224354917",
                adRequest, object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d("TAG_ad", adError.toString())
                        rewardedAd = null
                        isLoading = false

                    }


                    override fun onAdLoaded(ad: RewardedAd) {
                        Log.d("TAG_ad", "Ad was loaded.")
                        rewardedAd = ad
                        isLoading = false

                    }
                })
        }
    }

    private fun showRewardedVideo() {
//        binding.showVideoButton.visibility = View.INVISIBLE
        if (rewardedAd != null && !isLoading) {
            rewardedAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d("TAG_showRewardedVideo", "Ad was dismissed.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null
//                        if (googleMobileAdsConsentManager.canRequestAds) {
                        loadRewardedAd()
//                        }
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d("TAG_showRewardedVideo", "Ad failed to show.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d("TAG_showRewardedVideo", "Ad showed fullscreen content.")
                    }
                }

            rewardedAd?.show(
                requireActivity(),
                OnUserEarnedRewardListener { rewardItem ->
                    Log.d("TAG_showRewardedVideo", "$currentSkinIndex")
                    unlockSkin(currentSkinIndex)
                }
            )
        } else {
            loadRewardedAd()
//            Toast.makeText(requireContext(), "Ad is null", Toast.LENGTH_SHORT).show()
        }
    }


    private fun unlockSkin(unlockedSkinIndex: Int) {
        lockedSkinsList?.remove(unlockedSkinIndex)
        val currentSkins = skinsList[currentSkinIndex]
        binding.outerCore.setImageDrawable(currentSkins.outerSkin)
        binding.innerCompassId.setImageDrawable(currentSkins.innerSkin)
        unblurImageView(binding.innerCompassId)
        binding.rewardAdId.visibility = View.GONE

        Paper.book().write<MutableList<Int>>("PAPER_KEY_UNLOCKED_SKINS", lockedSkinsList!!)

    }


    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
        locationManager?.removeUpdates(this)
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onLocationChanged(location: Location) {
        // Update the user's location when it changes
        userLocation = location

        // Reverse geocode the location to get detailed address information
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address>? =
                geocoder.getFromLocation(location.latitude, location.longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val locationString =
                    "${location.latitude.toInt()},${location.longitude.toInt()}, ${address.locality}, ${address.adminArea}, ${address.countryName}"
//                binding.txtLocation.text = locationString
                (activity as? MainActivity)?.updateLocationText(locationString)


            } else {
//                binding.txtLocation.text = "Location not available"
                (activity as? MainActivity)?.updateLocationText("Location not available")

            }
        } catch (e: IOException) {
            e.printStackTrace()
//            binding.txtLocation.text = "Error retrieving location information"
            (activity as? MainActivity)?.updateLocationText("Error retrieving location information")

        }
    }

    override fun onProviderDisabled(provider: String) {

        showPermissionDialog()
    }

    private fun showPermissionDialog() {

        val dialog_binding = CustomDialogLocationBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
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
            Toast.makeText(
                requireContext(),
                "Some Features may not work properly!",
                Toast.LENGTH_SHORT
            ).show()

            dialog.dismiss()
        }

        dialog_binding.allowId.setOnClickListener {
            enableLocationServices()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun enableLocationServices() {
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            Toast.makeText(requireContext(), "Location Enabled!", Toast.LENGTH_SHORT).show()
        }

        task.addOnFailureListener { exception ->
            // Location settings are not satisfied.
            // Show a dialog prompting the user to enable location services.
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(requireActivity(), 123)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }
    }


    override fun onProviderEnabled(provider: String) {
        // Handle provider enabled
        Toast.makeText(
            requireContext(),
            "Location provider $provider is enabled",
            Toast.LENGTH_SHORT
        ).show()
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (lockedSkinsList!!.contains(currentSkinIndex)) {
            binding.outerCore.rotation = 0f
            binding.innerCompassId.rotation = 0f
            binding.textView.text = getString(R.string.locked)
        } else {

            val degree = event!!.values[0].roundToInt().toFloat()
            val rotationValue = -degree - 105
            val rotationAnimatorCa =
                ObjectAnimator.ofFloat(binding.outerCore, "rotation", rotationValue)

            GlobalScope.launch(Dispatchers.Main) {
                delay(200)

//                rotationAnimatorCa.duration = 800
                rotationAnimatorCa.interpolator = AccelerateDecelerateInterpolator()
                binding.outerCore.rotation = rotationValue
                rotationAnimatorCa.start()
            }

            // Calculate rotation for imageView2 to point towards center top
            val rotationValueImageView2 = -degree
            binding.innerCompassId.rotation = rotationValueImageView2

            val qiblaDirection = calculateQiblaDirection()
            binding.textView.text = "${degree.toInt()}° ${getCardinalDirection(degree)}"

            // Change the background color of the CardView when Qibla is at 256 degrees
            if (degree in 254f..258f) {
                vibrateDevice()
                binding.matchDirectionCardId.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green
                    )
                )
            } else {
                // Set a default or different color when Qibla is not at 256 degrees
                binding.matchDirectionCardId.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.white
                    )
                )
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }

    private fun calculateQiblaDirection(): Float {
        if (userLocation != null) {
            val userLatitude = userLocation!!.latitude
            val userLongitude = userLocation!!.longitude
            val kaabaLatitude = kaabaLocation.latitude
            val kaabaLongitude = kaabaLocation.longitude

            val qiblaDirection =
                calculateBearing(userLatitude, userLongitude, kaabaLatitude, kaabaLongitude)

            return qiblaDirection
        }

        return 0f
    }

    private fun vibrateDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator?.hasVibrator() == true) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    500,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && vibrator?.hasVibrator() == true) {
            vibrator?.vibrate(500)
        }
    }

    private fun calculateBearing(
        userLat: Double, userLon: Double,
        kaabaLat: Double, kaabaLon: Double
    ): Float {
        val deltaLon = kaabaLon - userLon

        val x = Math.cos(Math.toRadians(kaabaLat)) * Math.sin(Math.toRadians(deltaLon))
        val y = Math.cos(Math.toRadians(userLat)) * Math.sin(Math.toRadians(kaabaLat)) -
                Math.sin(Math.toRadians(userLat)) * Math.cos(Math.toRadians(kaabaLat)) * Math.cos(
            Math.toRadians(deltaLon)
        )

        val bearing = Math.toDegrees(Math.atan2(x, y)).toFloat()

        return (bearing + 360) % 360
    }

    private fun getCardinalDirection(degree: Float): String {
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val index = ((degree % 360 + 360) % 360 / 45).toInt()
        return directions[index]
    }

}

data class Skins(val outerSkin: Drawable?, val innerSkin: Drawable?)