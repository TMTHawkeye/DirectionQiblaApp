package com.example.directionqiblaapp.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.recreate
import androidx.recyclerview.widget.RecyclerView
import com.example.directionqiblaapp.Activities.Language
import com.example.directionqiblaapp.Activities.LanguagesActivity
import com.example.directionqiblaapp.Activities.Onboarding1Activity
import com.example.directionqiblaapp.databinding.ItemLanguageBinding
import com.zeugmasolutions.localehelper.LocaleHelper
import java.util.Locale

class LanguagesAdapter(val ctxt: Context, val languageList: ArrayList<Language>) : RecyclerView.Adapter<LanguagesAdapter.viewHolder>() {
    lateinit var binding:ItemLanguageBinding

    inner class viewHolder(var binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding=ItemLanguageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.binding.languageNameId.text=languageList.get(position).languageName
        holder.binding.flagId.setImageDrawable(languageList.get(position).flag)

        holder.itemView.setOnClickListener {
            changeLanguage(languageList.get(position).languageCode!!)
        }
    }
    fun changeLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        LocaleHelper.setLocale(ctxt, locale)

        val intent = Intent(ctxt, Onboarding1Activity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        ctxt.startActivity(intent)

    }

}