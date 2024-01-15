package com.example.directionqiblaapp.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.directionqiblaapp.PrayersRepository
import com.example.directionqiblaapp.retrofit.Resources
import com.example.qibla.api.model.PrayersTimeResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PrayerTimesViewModel(val repository:PrayersRepository) : ViewModel() {

    val _prayerTime : MutableLiveData<Resources<PrayersTimeResponse>> = MutableLiveData(Resources.Loading())
    val prayerTimes : LiveData<Resources<PrayersTimeResponse>> = _prayerTime

    fun getPrayerTimes(date: String?,latitude: Double, longitude: Double, method: Int) = viewModelScope.launch {
        _prayerTime.value=Resources.Loading()
        repository.getPrayerTimes(date,latitude,longitude,method).collect{
            _prayerTime.value=it
        }
    }
}