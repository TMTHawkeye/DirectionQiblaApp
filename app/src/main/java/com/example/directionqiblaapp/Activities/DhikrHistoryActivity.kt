package com.example.directionqiblaapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.directionqiblaapp.Adapters.DhikrAdapter
import com.example.directionqiblaapp.BuildConfig
import com.example.directionqiblaapp.MainApplication
import com.example.directionqiblaapp.ModelClasses.model.DhikrModel.Dhikr
import com.example.directionqiblaapp.databinding.ActivityDhikrHistoryBinding
import io.paperdb.Paper

class DhikrHistoryActivity : AppCompatActivity() {
    lateinit var binding:ActivityDhikrHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDhikrHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AdManager.getInstance().loadNativeAd(
            this@DhikrHistoryActivity,
            BuildConfig.native_home,
            binding.adFrame,
            binding.shimmerViewContainer
        )
        setHistoryRecyclerView()

        binding.backBtnId.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        (application as MainApplication).loadAd(this)
    }

    fun setHistoryRecyclerView(){
        var dhikrList= Paper.book().read<ArrayList<Dhikr?>>("Dhikr_LIST",ArrayList())
        if(dhikrList?.size!=0){
            binding.dhikrRV.visibility= View.VISIBLE
            binding.noHistoryFoundId.visibility= View.GONE
            binding.dhikrRV.layoutManager=LinearLayoutManager(this@DhikrHistoryActivity)
            binding.dhikrRV.adapter=DhikrAdapter(this@DhikrHistoryActivity,dhikrList!!)
        }
        else{
            binding.dhikrRV.visibility= View.GONE
            binding.noHistoryFoundId.visibility= View.VISIBLE
        }
    }
}