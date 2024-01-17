package com.example.directionqiblaapp.Fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import com.example.directionqiblaapp.Activities.AdManager
import com.example.directionqiblaapp.Activities.DhikrHistoryActivity
import com.example.directionqiblaapp.BuildConfig
import com.example.directionqiblaapp.Interfaces.DhikrSelectionListner
import com.example.directionqiblaapp.ModelClasses.model.DhikrModel.Dhikr
import com.example.directionqiblaapp.R
import com.example.directionqiblaapp.databinding.CustomDialogDeleteBinding
import com.example.directionqiblaapp.databinding.CustomDialogResetDhikrBinding
import com.example.directionqiblaapp.databinding.CustomDialogSaveDhikrBinding
import com.example.directionqiblaapp.databinding.FragmentTasbeehCounterBinding
import io.paperdb.Paper

class TasbeehCounterFragment : Fragment(),DhikrSelectionListner {
    lateinit var binding:FragmentTasbeehCounterBinding
    private var isBottomSheetVisible = false
    private var isDhikrSelected=false

    private var isVibrationEnabled = true
    private var isSoundEnabled = true

    private lateinit var vibrator: Vibrator
    private lateinit var mediaPlayer: MediaPlayer
    var dhikrItem:Dhikr? = null
    var countTasbeeh=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentTasbeehCounterBinding.inflate(layoutInflater,container,false)

        AdManager.getInstance().loadNativeAd(
            requireContext(),
            BuildConfig.native_home,
            binding.adFrame,
            binding.shimmerViewContainer
        )

        binding.countId.text=countTasbeeh.toString()

        // Restore saved states from PaperDB
        isVibrationEnabled = Paper.book().read("isVibrationEnabled", true)!!
        isSoundEnabled = Paper.book().read("isSoundEnabled", true)!!

        // Update drawables based on saved states
        updateVibrationDrawable(binding.cardVibrate)
        updateSoundDrawable(binding.cardSoundOn)

        binding.cardVibrate.setOnClickListener {
            toggleVibration()
            updateVibrationDrawable(binding.cardVibrate)
            saveVibrationState()
        }

        binding.cardSoundOn.setOnClickListener {
            toggleSound()
            updateSoundDrawable(binding.cardSoundOn)
            saveSoundState()
        }

        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.beep) // Add a short beep sound file (e.g., beep_sound.mp3) to the "res/raw" directory


        binding.countBtnId.setOnClickListener {
            if(dhikrItem!=null) {
                if(dhikrItem?.dhikrLimit.equals("infinite")){
                    countTasbeeh++
                    binding.countId.text = countTasbeeh.toString()
                }
                else{
                    if (countTasbeeh < dhikrItem?.dhikrLimit.toString().toInt()) {
                        countTasbeeh++
                        binding.countId.text = countTasbeeh.toString()
                    } else {
                        dhikrItem?.dhikrCount = binding.countId.text.toString().toInt()
                        updateDhikrCountInPaperDB(dhikrItem)
                    }
                }

                if (isVibrationEnabled) {
                    vibrateDevice()
                }

                if (isSoundEnabled) {
                    playBeepSound()
                }


            }
            else{
                Toast.makeText(requireContext(), "Please select the Dhikr first!!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.resetId.setOnClickListener {
            showResetDialog()
        }

        binding.saveId.setOnClickListener {
            if(dhikrItem!=null && !binding.countId.text.equals("0")){
                showSaveDialog()
            }
            else{
                Toast.makeText(requireContext(), "Count cannot be 0", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addDhikrId.setOnClickListener {
            if (!isBottomSheetVisible) {
                val bottomSheetFragment = DhikarBottomSheet()
                bottomSheetFragment.setListener(this)
                bottomSheetFragment.show(childFragmentManager,"EVENT_SELECTION")
                isBottomSheetVisible = true
            }
        }

        binding.cardHistory.setOnClickListener{
            if(dhikrItem?.dhikrCount!=0) {
                startActivity(Intent(requireContext(), DhikrHistoryActivity::class.java))
            }
        }

        return binding.root
    }

    private fun showSaveDialog() {
            val dialogBinding = CustomDialogSaveDhikrBinding.inflate(LayoutInflater.from(requireContext()))
            val dialog = Dialog(requireContext())
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
                dhikrItem?.dhikrCount = binding.countId.text.toString().toInt()
                updateDhikrCountInPaperDB(dhikrItem)
                dialog.dismiss()
            }

            dialogBinding.noId.setOnClickListener {
                dialog.dismiss()
            }

    }
    private fun showResetDialog() {
        val dialogBinding = CustomDialogResetDhikrBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = Dialog(requireContext())
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
            countTasbeeh=0
            binding.countId.text=countTasbeeh.toString()
            binding.dhikrNameId.text=""
            binding.dhikrNameId.visibility=View.GONE
            binding.addDhikrId.visibility=View.VISIBLE
            dhikrItem=null
            dialog.dismiss()
        }

        dialogBinding.noId.setOnClickListener {
            dialog.dismiss()
        }

    }


    private fun vibrateDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // Deprecated in API 26, but still applicable for older devices
            vibrator.vibrate(100)
        }
    }

    private fun toggleVibration() {
        isVibrationEnabled = !isVibrationEnabled
    }

    private fun toggleSound() {
        isSoundEnabled = !isSoundEnabled
    }

    private fun updateVibrationDrawable(imageView: ImageView) {
        val drawableRes = if (isVibrationEnabled) {
            R.drawable.vibrate_img
        } else {
            R.drawable.no_vibrate_img
        }
        imageView.setImageResource(drawableRes)
    }

    private fun updateSoundDrawable(imageView: ImageView) {
        val drawableRes = if (isSoundEnabled) {
            R.drawable.tune_img
        } else {
            R.drawable.no_beep_img
        }
        imageView.setImageResource(drawableRes)
    }

    private fun saveVibrationState() {
        Paper.book().write("isVibrationEnabled", isVibrationEnabled)
    }

    private fun saveSoundState() {
        Paper.book().write("isSoundEnabled", isSoundEnabled)
    }

    private fun playBeepSound() {
        mediaPlayer.seekTo(0) // Reset the media player to the beginning
        mediaPlayer.start()
    }

    private fun updateDhikrCountInPaperDB(updatedDhikr: Dhikr?) {
        val dhikrList = Paper.book().read<ArrayList<Dhikr?>>("Dhikr_LIST", ArrayList())
        val index = dhikrList?.indexOfFirst { it?.dhikrNAme == updatedDhikr?.dhikrNAme }
        if (index != -1) {
            dhikrList?.get(index!!)?.dhikrCount = updatedDhikr?.dhikrCount ?: 0
            Paper.book().write("Dhikr_LIST", dhikrList!!)
        }
        else{
            val dhikrList=ArrayList<Dhikr>()
            dhikrList.add(updatedDhikr!!)
           Paper.book().write("Dhikr_LIST",dhikrList)
        }
        Toast.makeText(requireContext(), "Saved in history successfully!!", Toast.LENGTH_SHORT).show()

        countTasbeeh=0
        binding.countId.text=countTasbeeh.toString()
        binding.dhikrNameId.text=""
        binding.dhikrNameId.visibility=View.GONE
        binding.addDhikrId.visibility=View.VISIBLE
        dhikrItem=null
    }

    override fun onDismiss(isDismissed: Boolean, isDhikrSelected:Boolean) {
        if (isDismissed) {
            isBottomSheetVisible = false
        }
        if(isDhikrSelected){
            binding.addDhikrId.visibility=View.INVISIBLE
            binding.dhikrNameId.visibility=View.VISIBLE

        }
        else{
            binding.addDhikrId.visibility=View.VISIBLE
            binding.dhikrNameId.visibility=View.INVISIBLE
        }
    }

    override fun onDhikrSelected(dhikr: Dhikr?) {
        binding.dhikrNameId.text=dhikr?.dhikrNAme
        this.dhikrItem=dhikr
    }

}

data class Tasbeeh(val dhikarName:String?,val countValue:String?,val savedDate:String?)