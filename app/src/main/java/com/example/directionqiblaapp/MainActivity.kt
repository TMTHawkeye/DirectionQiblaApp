package com.example.directionqiblaapp

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.directionqiblaapp.Activities.CalenderActivity
import com.example.directionqiblaapp.Activities.LanguagesActivity
import com.example.directionqiblaapp.Fragments.NamesOfAllahFragment
import com.example.directionqiblaapp.Fragments.PrayerTimeFragment
import com.example.directionqiblaapp.Fragments.QiblaDirectionFragment
import com.example.directionqiblaapp.Fragments.TasbeehCounterFragment
import com.example.directionqiblaapp.Interfaces.ComingPrayerListener
import com.example.directionqiblaapp.databinding.ActivityMainBinding
import com.example.directionqiblaapp.databinding.CustomDialogExitAppsBinding
import com.example.directionqiblaapp.databinding.CustomDialogMoreAppsBinding
import com.example.directionqiblaapp.databinding.CustomDialogPrivacyPolicyBinding
import com.example.directionqiblaapp.databinding.CustomDialogShareAppBinding
import com.google.android.gms.ads.AdView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adView = AdView(this)

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
            showPrivacyPolicyDialog()
            binding.drawerLayout.closeDrawer(GravityCompat.START)

        }

        shareApp.setOnClickListener {
            showShareAppDialog()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
//            shareApplication()
        }

        language.setOnClickListener {
            startActivity(Intent(this@MainActivity,LanguagesActivity::class.java))
        }

        moreApps.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            showMoreAppsDialog()
        }

        calender.setOnClickListener {
            startActivity(Intent(this@MainActivity,CalenderActivity::class.java))
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


//        binding.bottomNav.itemIconTintList = iconTintList
//
//        binding.bottomNav.itemTextColor = textColors

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
                    loadfragment(NamesOfAllahFragment())
                }
                R.id.Tasbeeh->{
                    loadfragment(TasbeehCounterFragment())
                }
                else -> {}

            }

            true

        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    showExitDialog()
                }
            }

        })




    }



    private fun showExitDialog() {
        val dialog_binding = CustomDialogExitAppsBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialog_binding.root)

        val window: Window = dialog.window!!
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)

        dialog.show()

        dialog_binding.noId.setOnClickListener {
            dialog.dismiss()
        }

        dialog_binding.yesId.setOnClickListener {
            dialog.dismiss()
            finishAffinity()
        }

        dialog_binding.rateUsId.setOnClickListener {
            dialog.dismiss()
            rateUs()
        }

    }

    private fun rateUs() {
        val appPackageName = packageName
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    private fun showShareAppDialog() {
        val dialog_binding = CustomDialogShareAppBinding.inflate(layoutInflater)
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

        dialog_binding.yesId.setOnClickListener {
            dialog.dismiss()
            shareApplication()
        }

        dialog_binding.noId.setOnClickListener {
            dialog.dismiss()
            // Handle the case when the user clicks "No"
        }
    }

    private fun showMoreAppsDialog() {
        val dialogBinding = CustomDialogMoreAppsBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)

        dialog.show()

        dialogBinding.yesId.setOnClickListener {
            dialog.dismiss()
            openMoreAppsOnPlayStore()
        }

        dialogBinding.noId.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showPrivacyPolicyDialog() {
        val dialogBinding = CustomDialogPrivacyPolicyBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)

        dialog.show()

        dialogBinding.dontAllowId.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.allowId.setOnClickListener {
            dialog.dismiss()
            privacyPolicy()
        }
    }

    private fun openMoreAppsOnPlayStore() {
        val appPackageName = "your.package.name"  // Replace with the package name of your other apps
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            intent.setPackage("com.android.vending")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // If Play Store app is not available, open the Play Store website
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            )
            startActivity(intent)
        }
    }



    override fun onResume() {
        super.onResume()
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        (application as MainApplication).loadAd(this)
        adView.resume()
    }

    override fun onPause() {
        super.onPause()
        adView.pause()

        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
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

    private fun privacyPolicy(){
        val privacyPolicyUrl = "https://sites.google.com/view/alawraq-studio/home"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
        startActivity(browserIntent)
    }

    private fun shareApplication() {
        val appPackageName = packageName
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Check out this amazing app: https://play.google.com/store/apps/details?id=$appPackageName"
        )
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, "Share via"))
    }

}