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
import com.example.directionqiblaapp.Activities.DhikrHistoryActivity
import com.example.directionqiblaapp.ModelClasses.model.DhikrModel.Dhikr
import com.example.directionqiblaapp.databinding.CustomDialogDeleteBinding
import com.example.directionqiblaapp.databinding.CustomDialogLocationBinding
import com.example.directionqiblaapp.databinding.ItemHistoryDhikrBinding
import io.paperdb.Paper

class DhikrAdapter(val ctxt: Context, val dhikrList: ArrayList<Dhikr?>) : RecyclerView.Adapter<DhikrAdapter.viewHolder>() {
    lateinit var binding:ItemHistoryDhikrBinding

    inner class viewHolder(var binding: ItemHistoryDhikrBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        binding=ItemHistoryDhikrBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dhikrList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.binding.dhikrNameId.text=dhikrList.get(position)?.dhikrNAme
        holder.binding.dateSavedId.text=dhikrList.get(position)?.date
        holder.binding.countValue.text=dhikrList.get(position)?.dhikrCount.toString()

        holder.binding.deleteItemId.setOnClickListener {
            showDeleteDialog(position)
        }

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
            deleteDhikr(position)
        }

        dialogBinding.noId.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun deleteDhikr(position: Int) {
        dhikrList.removeAt(position)
        Paper.book().write("Dhikr_LIST", dhikrList)
        notifyDataSetChanged()
    }


}