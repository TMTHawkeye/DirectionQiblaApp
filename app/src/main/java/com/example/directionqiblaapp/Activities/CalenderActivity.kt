package com.example.directionqiblaapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.directionqiblaapp.Adapters.EventsAdapter
import com.example.directionqiblaapp.Fragments.EventsBottomSheet
import com.example.directionqiblaapp.Interfaces.EventSelectionListner
import com.example.directionqiblaapp.ModelClasses.model.EventsModel.Event
import com.example.directionqiblaapp.databinding.ActivityCalenderBinding
import io.paperdb.Paper
import java.text.SimpleDateFormat
import java.util.Calendar

class CalenderActivity : AppCompatActivity() , EventSelectionListner{
    lateinit var binding:ActivityCalenderBinding

    private var isBottomSheetVisible = false
    lateinit var adapter : EventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCalenderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter=EventsAdapter(this)
        binding.eventsRV.layoutManager=LinearLayoutManager(this@CalenderActivity)

//        getCalenderData()

        val eventsListFromPaperDb= Paper.book().read<ArrayList<Event?>>("EVENTS_LIST")
        if(eventsListFromPaperDb!=null) {
            adapter.setList(eventsListFromPaperDb)
            binding.eventsRV.adapter = adapter
        }

        binding.addEventId.setOnClickListener{
            if (!isBottomSheetVisible) {
                val bottomSheetFragment = EventsBottomSheet(this@CalenderActivity)
                bottomSheetFragment.setListener(this)
                bottomSheetFragment.show(supportFragmentManager,"EVENT_SELECTION")
                isBottomSheetVisible = true
            }
        }

    }

//    private fun getCalenderData() {
//        val calendar1 = Calendar.getInstance()
//        val currentDate = calendar1.time
//        val dateFormat1 = SimpleDateFormat("dd-MM-yyyy")
//        val dateFormat = SimpleDateFormat("dd LLLL , yyyy")
//        val dateString = dateFormat.format(currentDate)
//        binding.georgedate.text = dateString
//        val dateString1 = dateFormat1.format(currentDate)
//        hijriDate(dateString1)
//    }

    override fun onDismisBottomSheet(isDismissed: Boolean) {
        if (isDismissed) {
            isBottomSheetVisible = false
        }
    }

    override fun onEventSelected(eventsList: ArrayList<Event?>) {
        adapter.setList(eventsList)
        binding.eventsRV.adapter=adapter
    }


}