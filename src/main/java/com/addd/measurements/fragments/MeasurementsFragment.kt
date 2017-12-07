package com.addd.measurements.fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.addd.measurements.MeasurementsAPI
import com.addd.measurements.R
import com.addd.measurements.adapters.DataAdapter
import com.addd.measurements.modelAPI.Measurement
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.measurements_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


/**
 * Created by addd on 03.12.2017.
 */
class MeasurementsFragment : Fragment() {
    private lateinit var APP_PREFERENCES: String
    private lateinit var APP_TOKEN: String
    private val APP_LIST = "listMeasurements"
    private val serviceAPI = MeasurementsAPI.Factory.create()
    private lateinit var date: String
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        APP_PREFERENCES = getString(R.string.my_settings)
        APP_TOKEN = getString(R.string.token)
        val view: View = inflater!!.inflate(R.layout.measurements_fragment, container, false)

        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottomNavigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.today -> {
                    todayMeasurements()
                }
                R.id.tomorrow -> {
                    tomorrowMeasurements()
                }
                R.id.date -> {
                    dateMeasurements()
                }
            }
            true
        }

        todayMeasurements()
        return view
    }

    private fun getMeasurements() {
        val mSettings: SharedPreferences = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        var token = ""
        if (mSettings.contains(APP_TOKEN)) {
            token = "Token " + mSettings.getString(APP_TOKEN, "")
        }
        val call = serviceAPI.getMeasurements(token, date)
        call.enqueue(object : Callback<List<Measurement>> {
            override fun onResponse(call: Call<List<Measurement>>?, response: Response<List<Measurement>>?) {
                if (response!!.body() != null) {
                    recyclerList.adapter = DataAdapter(response.body())
                    recyclerList.layoutManager = LinearLayoutManager(activity.applicationContext)
                    val dividerItemDecoration = DividerItemDecoration(recyclerList.context, LinearLayoutManager(activity.applicationContext).orientation) // какой-то хуевый разделитель
                    recyclerList.addItemDecoration(dividerItemDecoration)

                    saveMeasurementsList(context, response.body())

                }
            }

            override fun onFailure(call: Call<List<Measurement>>?, t: Throwable?) {
                recyclerList.adapter = DataAdapter(loadSharedPreferencesList(context))
                recyclerList.layoutManager = LinearLayoutManager(activity.applicationContext)
                val dividerItemDecoration = DividerItemDecoration(recyclerList.context, LinearLayoutManager(activity.applicationContext).orientation) // разделитель
                recyclerList.addItemDecoration(dividerItemDecoration)
            }
        })
    }

    private fun todayMeasurements() {
        val calendar = Calendar.getInstance()
        var day: String
        day = if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            "0" + calendar.get(Calendar.DAY_OF_MONTH)
        } else {
            calendar.get(Calendar.DAY_OF_MONTH).toString()
        }


        date = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-$day"
        getMeasurements()
    }

    private fun tomorrowMeasurements() {
        var day: String
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        day = if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            "0" + calendar.get(Calendar.DAY_OF_MONTH)
        } else {
            calendar.get(Calendar.DAY_OF_MONTH).toString()
        }
        date = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-$day"
        getMeasurements()
    }

    private fun dateMeasurements() {
        val myCallBack = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            var day: String = if (dayOfMonth < 10) {
                "0" + dayOfMonth
            } else {
                dayOfMonth.toString()
            }
            date = "$year-${monthOfYear + 1}-$day"
            getMeasurements()
        }
        val datePikerDialog: DatePickerDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val calendar = Calendar.getInstance()
            DatePickerDialog(context, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        datePikerDialog.show()
    }

    private fun saveMeasurementsList(context: Context, list: List<Measurement>) {
        val mPrefs = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val prefsEditor = mPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        prefsEditor.putString(APP_LIST, json)
        prefsEditor.commit()
    }

    private fun loadSharedPreferencesList(context: Context): List<Measurement> {
        var callLog: List<Measurement>
        val mPrefs = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = mPrefs.getString(APP_LIST, "")
        if (json!!.isEmpty()) {
            callLog = ArrayList<Measurement>()
        } else {
            val type = object : TypeToken<List<Measurement>>() {
            }.type
            callLog = gson.fromJson(json, type)
        }
        return callLog
    }
}
