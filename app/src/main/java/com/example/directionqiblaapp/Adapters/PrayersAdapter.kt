package com.example.directionqiblaapp.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.directionqiblaapp.Fragments.PrayerTimeSettingsFragment
import com.example.directionqiblaapp.Fragments.Prayer
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.databinding.ItemPrayerBinding

class PrayersAdapter(var ctxt: Context, var prayersData: ArrayList<Prayer>) : RecyclerView.Adapter<PrayersAdapter.viewHolder>() {
    lateinit var binding:ItemPrayerBinding

    inner class viewHolder(var binding: ItemPrayerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding=ItemPrayerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return prayersData.size

    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.binding.prayerNameId.text=prayersData[position].name
        holder.binding.prayerTimeValue.text=prayersData[position].time

        // Set click listener for settings icon
        holder.binding.settingsIconId.setOnClickListener {
            // Get the FragmentManager
            val fragmentManager = (ctxt as AppCompatActivity).supportFragmentManager

            // Create a bundle to pass data to the fragment
            val bundle = Bundle()
            bundle.putString("prayerName", prayersData?.get(position)?.name)

            // Replace the current fragment with PrayerSettingsFragment
            val prayerSettingsFragment = PrayerTimeSettingsFragment()
            prayerSettingsFragment.arguments = bundle

            fragmentManager.beginTransaction()
                .replace(R.id.container, prayerSettingsFragment)
                .addToBackStack(null)
                .commit()
        }

    }
}