package com.example.directionqiblaapp.Interfaces

import com.example.directionqiblaapp.ModelClasses.model.EventsModel.Event

interface EventSelectionListner {
    fun onDismisBottomSheet(isDismissed:Boolean)
    fun onEventSelected(eventsList:ArrayList<Event?>)

}