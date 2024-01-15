package com.example.directionqiblaapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.databinding.ActivityOnboarding1Binding

class Onboarding1Activity : AppCompatActivity() {
    lateinit var binding: ActivityOnboarding1Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboarding1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.onboarding_background)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        // Set the activity's theme
//        setTheme(R.style.Base_Theme_Qibla)

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, OnboardingActivity2::class.java))

        }
    }
}