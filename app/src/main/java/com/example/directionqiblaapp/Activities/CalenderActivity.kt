package com.example.directionqiblaapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.CalendarView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.directionqiblaapp.Adapters.EventsAdapter
import com.example.directionqiblaapp.Fragments.EventsBottomSheet
import com.example.directionqiblaapp.Interfaces.EventSelectionListner
import com.example.directionqiblaapp.ModelClasses.model.EventsModel.Event
import com.example.directionqiblaapp.databinding.ActivityCalenderBinding
import io.paperdb.Paper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.Executors

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

        getCalenderData()

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

    private fun getCalenderData() {
        val calendar1 = Calendar.getInstance()
        val currentDate = calendar1.time
        val dateFormat1 = SimpleDateFormat("dd-MM-yyyy")
        val dateFormat = SimpleDateFormat("dd LLLL , yyyy")
        val dateString = dateFormat.format(currentDate)
        binding.georgedate.text = dateString
        val dateString1 = dateFormat1.format(currentDate)
        hijriDate(dateString1)

        binding.calender.setOnDateChangeListener(
            CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar[year, month] = dayOfMonth
                val date = calendar.time
                val dateFormat = SimpleDateFormat("dd LLLL , yyyy")
                val dateString = dateFormat.format(date)
                binding.georgedate.text = dateString

                val dateFormat1 = SimpleDateFormat("dd-MM-yyyy")
                val dateString1 = dateFormat1.format(date)

                hijriDate(dateString1)

//                binding.progressdate.visibility = View.VISIBLE
//                binding.calenderlayout.visibility = View.GONE

            })
    }

    override fun onDismisBottomSheet(isDismissed: Boolean) {
        if (isDismissed) {
            isBottomSheetVisible = false
        }
    }

    override fun onEventSelected(eventsList: ArrayList<Event?>) {
        adapter.setList(eventsList)
        binding.eventsRV.adapter=adapter
    }

    fun hijriDate(date:String)
    {
        var day = ""
        var month  = ""
        var year  = ""
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.aladhan.com/v1/timingsByAddress/$date?address=Rawalpindi,PK&method=5")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                val responseData = response.body?.string()

                executor.execute(Runnable {
                    try {
                        val jsonObject = JSONObject(responseData)
                        val data_json = jsonObject.getJSONObject("data")
                        val time_data1 = data_json.getString("date")
                        val time_data = JSONObject(time_data1)
                        val t1 = time_data.getString("hijri")
                        var t2 = JSONObject(t1)
                        day = t2.getString("day")
                        var t5 = t2.getString("month")
                        var t4 = JSONObject(t5)
                        month = t4.getString("en")
                        year = t2.getString("year")
                    }
                    catch (e: JSONException)
                    {
                        e.printStackTrace()
                    }

                    handler.post(Runnable {
                        if (this != null)
                        {
                            runOnUiThread {
                                binding.hijridate.text = "$day $month , $year"
//                                binding.progressdate.visibility = View.GONE
                                binding.calenderlayout.visibility = View.VISIBLE
                            }
                        }

                    })
                })

            }
            override fun onFailure(call: Call, e: IOException) {
            }
        })
    }


}