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

        if (bundle != null) {
            showDialog()
            when (bundle.getInt("check")) {
                0 -> middleware.getTodayCurrentMeasurements(context)
                1 -> middleware.getTodayRejectMeasurements(context)
                2 -> middleware.getTodayClosedMeasurements(context)
            }
        }

        return view
    }

    override fun onStop() {
        super.onStop()
    }

    private fun onClickCurrent(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.today -> {
                    showDialog()
                    middleware.getTodayCurrentMeasurements(context)
                }
                R.id.tomorrow -> {
                    showDialog()
                    middleware.getTomorrowCurrentMeasurements(context)
                }
                R.id.date -> {
                    dateCurrentMeasurements()
                }
            }
            true
        }
    }


    private fun onClickRejected(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.today -> {
                    showDialog()
                    middleware.getTodayRejectMeasurements(context)
                }
                R.id.tomorrow -> {
                    showDialog()
                    middleware.getTomorrowRejectMeasurements(context)
                }
                R.id.date -> {
                    dateRejectMeasurements()
                }
            }
            true
        }
    }

    private fun onClickClosed(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.today -> {
                    showDialog()
                    middleware.getTodayClosedMeasurements(context)
                }
                R.id.tomorrow -> {
                    showDialog()
                    middleware.getTomorrowClosedMeasurements(context)
                }
                R.id.date -> {
                    dateClosedMeasurements()
                }
            }
            true
        }
    }


    private fun dateCurrentMeasurements() {
        val myCallBack = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            var day: String = if (dayOfMonth < 10) {
                "0" + dayOfMonth
            } else {
                dayOfMonth.toString()
            }
            date = "$year-${monthOfYear + 1}-$day"
            showDialog()
            middleware.getDateCurrentMeasurements(context, date)
        }
        val calendar = Calendar.getInstance()
        val datePikerDialog = DatePickerDialog(context, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePikerDialog.show()
    }

    private fun dateRejectMeasurements() {
        val myCallBack = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            var day: String = if (dayOfMonth < 10) {
                "0" + dayOfMonth
            } else {
                dayOfMonth.toString()
            }
            date = "$year-${monthOfYear + 1}-$day"
            showDialog()
            middleware.getDateRejectMeasurements(context, date)
        }
        val calendar = Calendar.getInstance()
        val datePikerDialog = DatePickerDialog(context, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePikerDialog.show()
    }

    private fun dateClosedMeasurements() {
        val myCallBack = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            var day: String = if (dayOfMonth < 10) {
                "0" + dayOfMonth
            } else {
                dayOfMonth.toString()
            }
            date = "$year-${monthOfYear + 1}-$day"
            showDialog()
            middleware.getDateClosedMeasurements(context, date)
        }
        val calendar = Calendar.getInstance()
        val datePikerDialog = DatePickerDialog(context, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePikerDialog.show()
    }


    private fun showDialog() {
        val builder = AlertDialog.Builder(context)
        val viewAlert = layoutInflater.inflate(R.layout.load_dialog, null)
        builder.setView(viewAlert)
                .setCancelable(false)
        alert = builder.create()
        alert.show()
    }

    override fun callingBack(listMeasurements: List<Measurement>, result: Int) {
        if (listMeasurements.isEmpty()) {
            if (result == 1) {
                Toast.makeText(context, "Нет сохраненных данных на заданную дату, проверьте подключение к интернету", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "По данному запросу нет данных", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (result == 0) {
                Toast.makeText(context, "Данные загружены из сети", Toast.LENGTH_SHORT).show()
            } else {

                Toast.makeText(context, "Отсутствует связь с ИНТЕРНЕТ! Данные загружены из локального хранилища", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerList.adapter = DataAdapter(listMeasurements)
        recyclerList.layoutManager = LinearLayoutManager(activity.applicationContext)
        alert.dismiss()
    }

}
