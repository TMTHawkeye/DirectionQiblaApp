package com.example.directionqiblaapp.Fragments

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
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
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.directionqiblaapp.Activities.CalenderActivity
import com.example.directionqiblaapp.MainActivity
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.databinding.FragmentQiblaDirectionBinding
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

    private var vibrator: Vibrator? = null
    private var sensorManager: SensorManager? = null
    private var locationManager: LocationManager? = null
    private var userLocation: Location? = null
    private var isBottomSheetVisible = false
    var skinsList = ArrayList<Skins>()
    private var currentSkinIndex = 0

    private var renderScript: RenderScript? = null
    private var blurScript: ScriptIntrinsicBlur? = null



    private val kaabaLocation = Location("Kaaba").apply {
        latitude = 21.3891
        longitude = 39.8579
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQiblaDirectionBinding.inflate(layoutInflater)

        initSkinsList()
        // Get the skins for the current index
        val currentSkins = skinsList[currentSkinIndex]
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
                requireContext().getDrawable(R.drawable.outer_core_1),
                requireContext().getDrawable(R.drawable.campass_img)
            )
        )

        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_core_2),
                requireContext().getDrawable(R.drawable.inner_compass_2)
            )
        )
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_core_1),
                requireContext().getDrawable(R.drawable.inner_compass_3)
            )
        )
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_core_3),
                requireContext().getDrawable(R.drawable.inner_compass_3)
            )
        )
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_core_1),
                requireContext().getDrawable(R.drawable.inner_compass_4)
            )
        )
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_core_2),
                requireContext().getDrawable(R.drawable.inner_compass_4)
            )
        )
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_core_5),
                requireContext().getDrawable(R.drawable.inner_compass_5)
            )
        )
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_core_1),
                requireContext().getDrawable(R.drawable.inner_compass_6)
            )
        )
        skinsList.add(
            Skins(
                requireContext().getDrawable(R.drawable.outer_core_2),
                requireContext().getDrawable(R.drawable.inner_compass_6)
            )
        )


    }

    private fun updateRewardAdVisibility() {
        if (currentSkinIndex >= 4) {
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


        binding.hijriCalenderCardId.setOnClickListener {
            startActivity(Intent(requireContext(), CalenderActivity::class.java))
        }

        binding.nextCompassId.setOnClickListener {
            if (currentSkinIndex == skinsList.size - 1) {
                Toast.makeText(
                    requireContext(),
                    "No more skins available",
                    Toast.LENGTH_SHORT
                ).show()
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
                Toast.makeText(
                    requireContext(),
                    "No more previous skins available",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                currentSkinIndex = (currentSkinIndex - 1 + skinsList.size) % skinsList.size
                val currentSkins = skinsList[currentSkinIndex]
                binding.outerCore.setImageDrawable(currentSkins.outerSkin)
                binding.innerCompassId.setImageDrawable(currentSkins.innerSkin)
                updateRewardAdVisibility()

            }
        }


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


    override fun onSensorChanged(event: SensorEvent?) {
        if (currentSkinIndex >= 4) {
            binding.outerCore.rotation=0f
            binding.innerCompassId.rotation=0f
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