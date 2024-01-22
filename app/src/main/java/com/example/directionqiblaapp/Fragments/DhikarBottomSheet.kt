package com.example.directionqiblaapp.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.directionqiblaapp.Interfaces.DhikrSelectionListner
import com.example.directionqiblaapp.ModelClasses.model.DhikrModel.Dhikr
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.databinding.FragmentDhikarBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paperdb.Paper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DhikarBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding:FragmentDhikarBottomSheetBinding
    lateinit var dhikrSelectionListner: DhikrSelectionListner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentDhikarBottomSheetBinding.inflate(layoutInflater)
        dhikrSelectionListner.onDismiss(true,false)

        binding.confirmDhikrId.setOnClickListener {
            if(binding.customLimitETId.text.isNullOrEmpty()&&!binding.dhikrNameETId.text.isNullOrEmpty()){
                val limit="infinite"
                val dhikrData=collectDhikrData(limit)
                addToPaperDB(dhikrData)
                dhikrSelectionListner.onDhikrSelected(dhikrData)
                dhikrSelectionListner.onDismiss(true,true)
                dismiss()
            }
            else if(!binding.dhikrNameETId.text.isNullOrEmpty()&&!binding.customLimitETId.text.toString().equals("0")){
                val dhikrData=collectDhikrData(binding.customLimitETId.text.toString())
                addToPaperDB(dhikrData)
                dhikrSelectionListner.onDhikrSelected(dhikrData)
                dhikrSelectionListner.onDismiss(true,true)
                dismiss()
            }
            else{
                Toast.makeText(requireContext(), "Fields cannot be empty or 0!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.customLimitETId.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                if (editable?.length == 0) {
                    binding.cardLimit.setImageDrawable(requireContext().getDrawable(R.drawable.inifinite_limit_img))
                }
                else{
                    binding.cardLimit.setImageDrawable(requireContext().getDrawable(R.drawable.no_infinite_img))
                }
            }

        })
        return binding.root
    }

    private fun addToPaperDB(dhikrData: Dhikr) {
        var dhikrList= Paper.book().read<ArrayList<Dhikr?>>("Dhikr_LIST",ArrayList())
        dhikrList?.add(dhikrData)
        Paper.book().write("Dhikr_LIST",dhikrList!!)
    }

    fun collectDhikrData(limit:String): Dhikr {
        val currentDate = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(Date())

        var dhikrData = Dhikr(
            binding.dhikrNameETId.text.toString(),
            limit,
            0,
            currentDate,
            null
        )
        return dhikrData
    }

    fun setListener(listener: DhikrSelectionListner) {
        this.dhikrSelectionListner = listener
    }
}