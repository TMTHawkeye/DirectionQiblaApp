package com.example.directionqiblaapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.directionqiblaapp.Activities.CalenderActivity
import com.example.directionqiblaapp.ModelClasses.model.EventsModel.Event
import com.example.directionqiblaapp.databinding.ItemEventBinding
import io.paperdb.Paper

class EventsAdapter(val ctxt: Context) : RecyclerView.Adapter<EventsAdapter.viewHolder>() {
    lateinit var binding:ItemEventBinding
    var eventList=ArrayList<Event?>()

    inner class viewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding=ItemEventBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return eventList.size?:0
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.binding.eventNameId.text=eventList.get(position)?.eventName
        holder.binding.eventDateId.text=eventList.get(position)?.eventDate
        holder.binding.dateId.text=eventList.get(position)?.eventAddedDate

        holder.binding.deleteItemId.setOnClickListener {
            eventList.removeAt(position)
            Paper.book().write("EVENTS_LIST", eventList)
            notifyDataSetChanged()
        }

    }

    fun setList(eventsList: ArrayList<Event?>){
        this.eventList=eventsList
        notifyDataSetChanged()
    }
}