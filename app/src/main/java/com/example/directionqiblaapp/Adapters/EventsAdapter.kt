package com.example.directionqiblaapp.Adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.RecyclerView
import com.example.directionqiblaapp.ModelClasses.model.EventsModel.Event
import com.example.directionqiblaapp.databinding.CustomDialogDeleteBinding
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
            showDeleteDialog(position)
        }

    }

    fun setList(eventsList: ArrayList<Event?>){
        this.eventList=eventsList
        notifyDataSetChanged()
    }

    private fun showDeleteDialog(position: Int) {
        val dialogBinding = CustomDialogDeleteBinding.inflate(LayoutInflater.from(ctxt))
        val dialog = Dialog(ctxt)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setGravity(Gravity.CENTER)

        dialog.show()

        dialogBinding.yesId.setOnClickListener {
            dialog.dismiss()
            deleteEvent(position)
        }

        dialogBinding.noId.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun deleteEvent(position: Int) {
        eventList.removeAt(position)
        Paper.book().write("EVENTS_LIST", eventList)
        notifyDataSetChanged()
    }
}