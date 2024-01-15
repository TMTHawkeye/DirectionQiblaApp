package com.example.directionqiblaapp.DI

import com.example.directionqiblaapp.PrayersRepository
import com.example.directionqiblaapp.ViewModels.PrayerTimesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    single { PrayersRepository(context = get()) }
    viewModel { PrayerTimesViewModel(repository = get())}
}

