package com.example.directionqiblaapp.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.directionqiblaapp.Interfaces.EventSelectionListner
import com.example.directionqiblaapp.ModelClasses.model.EventsModel.Event
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.databinding.FragmentEventsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paperdb.Paper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventsBottomSheet(context: Context) : BottomSheetDialogFragment() {
    lateinit var binding: FragmentEventsBottomSheetBinding
    lateinit var eventsSelectionListner: EventSelectionListner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventsBottomSheetBinding.inflate(layoutInflater)
        eventsSelectionListner.onDismisBottomSheet(true)

//        val eventData=collectEventData()


        binding.createEventId.setOnClickListener {
            val eventData=collectEventData()
            val eventsUpdatedList=addToPaperDB(eventData)
            eventsSelectionListner.onEventSelected(eventsUpdatedList)
            dismiss()
        }


        return binding.root
    }

    private fun addToPaperDB(eventData: Event):ArrayList<Event?> {
        var eventList=Paper.book().read<ArrayList<Event?>>("EVENTS_LIST",ArrayList())
        eventList?.add(eventData)
        Paper.book().write("EVENTS_LIST",eventList!!)
        return eventList
    }

    fun collectEventData(): Event {
        val currentDate = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(Date())

        var eventData = Event(
            binding.eventNameETId.text.toString(),
            binding.dateETId.text.toString(),
            binding.starttimeETId.text.toString(),
            binding.endtimeETId.text.toString(),
            currentDate
        )

        return eventData
    }


    fun setListener(listener: EventSelectionListner) {
        this.eventsSelectionListner = listener
    }
}