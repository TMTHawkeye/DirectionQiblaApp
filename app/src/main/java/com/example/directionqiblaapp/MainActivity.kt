package com.example.directionqiblaapp

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.directionqiblaapp.Fragments.PrayerTimeFragment
import com.example.directionqiblaapp.Fragments.QiblaDirectionFragment
import com.example.directionqiblaapp.Fragments.TasbeehCounterFragment
import com.example.directionqiblaapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window: Window = this@MainActivity.window
        window.addFlags(
            WindowManager.LayoutParams
                .FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.theme)


        val selectedColor = ContextCompat.getColor(this, R.color.bottom_nav_icon_selected)

        val unselectedColor = ContextCompat.getColor(this, R.color.bottom_nav_icon_unselected)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.menuId.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        val removeAds =
            binding.navigationView.getHeaderView(0)
                .findViewById<LinearLayout>(R.id.removeAds_layout)

        val calender =
            binding.navigationView.getHeaderView(0)
                .findViewById<LinearLayout>(R.id.calender_layout)

        val privacyPolicy =
            binding.navigationView.getHeaderView(0)
                .findViewById<LinearLayout>(R.id.privacy_layout)

        val shareApp =
            binding.navigationView.getHeaderView(0)
                .findViewById<LinearLayout>(R.id.share_app_layout)

        val language =
            binding.navigationView.getHeaderView(0)
                .findViewById<LinearLayout>(R.id.language_layout)

        val moreApps =
            binding.navigationView.getHeaderView(0)
                .findViewById<LinearLayout>(R.id.moreApps_layout)

        privacyPolicy.setOnClickListener {
//            privacyPolicy()
            binding.drawerLayout.closeDrawer(GravityCompat.START)

        }

        shareApp.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
//            shareApplication()
        }

        language.setOnClickListener {
//            binding.drawerLayout.closeDrawer(GravityCompat.START)
            Toast.makeText(this@MainActivity, "Comming Soon!", Toast.LENGTH_SHORT).show()
        }

        moreApps.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        calender.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        removeAds.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }


        val currentDate = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(Date())
        binding.txtDate.text = currentDate



        val iconTintList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_selected),
                intArrayOf(-android.R.attr.state_selected)
            ),
            intArrayOf(selectedColor, unselectedColor)
        )
        val textColors = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_selected),
                intArrayOf(-android.R.attr.state_selected)
            ),
            intArrayOf(selectedColor, unselectedColor)
        )


        binding.bottomNav.itemIconTintList = iconTintList

        binding.bottomNav.itemTextColor = textColors

        val menu = binding.bottomNav.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            val iconResId = when (i) {
                0 -> R.drawable.selector_home_icon
                1 -> R.drawable.selector_prayers_icon
                2 -> R.drawable.selector_tasbeeh_icon
                3 -> R.drawable.selector_names_icon
                else -> throw IllegalArgumentException("Invalid item index")
            }
            menuItem.icon = AppCompatResources.getDrawable(this, iconResId)
        }

        binding.bottomNav.selectedItemId = R.id.Qibla
        loadfragment(QiblaDirectionFragment())

        binding.bottomNav.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.Qibla -> {
                    loadfragment(QiblaDirectionFragment())
                }
                R.id.Prayers -> {
                    loadfragment(PrayerTimeFragment())
//                    checkLocationPermission()
                }
                R.id.Names-> {

                }
                R.id.Tasbeeh->{
                    loadfragment(TasbeehCounterFragment())
                }
                else -> {}

            }

            true

        }


    }

    private fun loadfragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.continerView, fragment)
        fragmentTransaction.commit()

    }

    fun updateLocationText(location: String) {
        binding.txtLocation.text = location
    }
}