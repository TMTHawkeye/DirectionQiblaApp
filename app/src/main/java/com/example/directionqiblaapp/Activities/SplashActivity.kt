package com.example.directionqiblaapp.Activities

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.directionqiblaapp.MainActivity
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

    lateinit var binding:ActivitySplashBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        val rotationAnimator = ObjectAnimator.ofFloat(binding.splashIconId, "rotation", 0f, 360f)
        rotationAnimator.duration = 2000
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE
        rotationAnimator.interpolator = LinearInterpolator()
        rotationAnimator.start()

        Handler().postDelayed({

            if (isFirstRun()) {
                navigateToLanguagesActivity()
            } else {
                // Run next activity directly
                navigateToNextActivity()
            }
        }, 5000)


    }

        private fun isFirstRun(): Boolean {
            val firstRunKey = "first_run"
            val isFirstRun = sharedPreferences.getBoolean(firstRunKey, true)

            // If it's the first run, set the flag to false
            if (isFirstRun) {
                with(sharedPreferences.edit()) {
                    putBoolean(firstRunKey, false)
                    apply()
                }
            }

            return isFirstRun
        }

        private fun navigateToLanguagesActivity() {
            val intent = Intent(this, LanguagesActivity::class.java)
            startActivity(intent)
            finish()
        }

        private fun navigateToNextActivity() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
}