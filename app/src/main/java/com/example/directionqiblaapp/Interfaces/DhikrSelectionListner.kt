package com.example.directionqiblaapp.Interfaces

import com.example.directionqiblaapp.ModelClasses.model.DhikrModel.Dhikr
import com.example.directionqiblaapp.ModelClasses.model.EventsModel.Event

interface DhikrSelectionListner {

    fun onDismiss(isDismissed:Boolean, isDhikrSelected:Boolean)

    fun onDhikrSelected(dhikrList:Dhikr?)

}