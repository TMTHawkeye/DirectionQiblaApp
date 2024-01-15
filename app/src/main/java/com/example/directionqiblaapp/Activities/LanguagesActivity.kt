package com.example.directionqiblaapp.Activities

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.directionqiblaapp.Adapters.LanguagesAdapter
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.databinding.ActivityLanguagesBinding
import com.zeugmasolutions.localehelper.LocaleHelper
import java.util.Locale

class LanguagesActivity : AppCompatActivity() {
    lateinit var binding:ActivityLanguagesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLanguagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val languageList=getLanguagesList()
        binding.languagesRV.layoutManager=LinearLayoutManager(this@LanguagesActivity)
        val adapter=LanguagesAdapter(this@LanguagesActivity,languageList)
        binding.languagesRV.adapter=adapter


    }

    private fun getLanguagesList():ArrayList<Language> {
        var languageList=ArrayList<Language>()
        languageList.add(Language(getDrawable(R.drawable.english_flag), "English (Default)", "en"))
        languageList.add(Language(getDrawable(R.drawable.afrikan_flag), "Afrikan", "af"))
        languageList.add(Language(getDrawable(R.drawable.russian_flag), "French", "fr"))
        languageList.add(Language(getDrawable(R.drawable.german_flag), "German", "de"))
        languageList.add(Language(getDrawable(R.drawable.indian_flag), "Hindi", "hi"))

        return languageList
    }


    override fun attachBaseContext(newBase: Context) {
        val locale = Locale("en")
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }


}

data class Language(val flag: Drawable?,var languageName:String?,val languageCode:String?)