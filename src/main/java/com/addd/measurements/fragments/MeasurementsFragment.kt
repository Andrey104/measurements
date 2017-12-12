package com.addd.measurements.fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.addd.measurements.R
import com.addd.measurements.adapters.DataAdapter
import com.addd.measurements.middleware.MiddlewareImplementation
import com.addd.measurements.modelAPI.Measurement
import kotlinx.android.synthetic.main.measurements_fragment.*
import java.util.*


/**
 * Created by addd on 03.12.2017.
 */
class MeasurementsFragment : Fragment(), MiddlewareImplementation.Callback {
    val middleware = MiddlewareImplementation()
    private lateinit var date: String
    lateinit var alert: AlertDialog

    override fun callingBack(listMeasurements: List<Measurement>, result: Int) {
        if (result == 0) {
            Toast.makeText(context, "Данные загружены из сети", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Отсутствует связь с ИНТЕРНЕТ! Данные загружены из локального хранилища", Toast.LENGTH_SHORT).show()
        }
        recyclerList.adapter = DataAdapter(listMeasurements)
        recyclerList.layoutManager = LinearLayoutManager(activity.applicationContext)
        alert.dismiss()
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        middleware.registerCallBack(this)
        val view: View = inflater!!.inflate(R.layout.measurements_fragment, container, false)
        val bundle = this.arguments

        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottomNavigation)
        if (bundle != null) {
            when (bundle.getInt("check")) {
                0 -> onClickCurrent(bottomNavigationView)
                1 -> onClickRejected(bottomNavigationView)
                2 -> onClickClosed(bottomNavigationView)
            }
        }


//        todayMeasurements()
        return view
    }

    private fun onClickCurrent(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.today -> {
                    showDialog()
                    middleware.getTodayNormalMeasurements(context)
                }
                R.id.tomorrow -> {
//                    tomorrowMeasurements()
                }
                R.id.date -> {
//                    dateMeasurements()
                }
            }
            true
        }
    }

    private fun onClickRejected(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.today -> {
                    Toast.makeText(context, "rejected", Toast.LENGTH_SHORT).show()
                }
                R.id.tomorrow -> {
                    Toast.makeText(context, "rejected", Toast.LENGTH_SHORT).show()
                }
                R.id.date -> {
                    Toast.makeText(context, "rejected", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    private fun onClickClosed(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.today -> {
                    Toast.makeText(context, "closed", Toast.LENGTH_SHORT).show()
                }
                R.id.tomorrow -> {
                    Toast.makeText(context, "closed", Toast.LENGTH_SHORT).show()
                }
                R.id.date -> {
                    Toast.makeText(context, "closed", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    fun showDialog() {
        val builder = AlertDialog.Builder(context)
        val viewAlert = layoutInflater.inflate(R.layout.load_dialog, null)
        builder.setView(viewAlert)
                .setCancelable(false)
        alert = builder.create()
        alert.show()
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
//        getMeasurements()
    }

    private fun dateMeasurements() {
        val myCallBack = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            var day: String = if (dayOfMonth < 10) {
                "0" + dayOfMonth
            } else {
                dayOfMonth.toString()
            }
            date = "$year-${monthOfYear + 1}-$day"
//            getMeasurements()
        }
        val calendar = Calendar.getInstance()
        val datePikerDialog = DatePickerDialog(context, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePikerDialog.show()
    }

}
