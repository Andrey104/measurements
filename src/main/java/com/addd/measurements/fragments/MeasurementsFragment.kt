package com.addd.measurements.fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.addd.measurements.R
import com.addd.measurements.adapters.DataAdapter
import com.addd.measurements.changemenu.ChangeManager
import com.addd.measurements.middleware.NetworkController
import com.addd.measurements.modelAPI.Measurement
import kotlinx.android.synthetic.main.measurements_fragment.*
import java.util.*


/**
 * Created by addd on 03.12.2017.
 */

class MeasurementsFragment : Fragment(), NetworkController.Callback {
    private lateinit var date: String
    lateinit var alert: AlertDialog
    override fun onStop() {
        super.onStop()
        NetworkController.registerCallBack(null)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        NetworkController.registerCallBack(this)
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
                0 -> NetworkController.getTodayCurrentMeasurements(context)
                1 -> NetworkController.getTodayRejectMeasurements(context)
                2 -> NetworkController.getTodayClosedMeasurements(context)
            }
        }

        return view
    }



    private fun onClickCurrent(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.today -> {
                    showDialog()
                    NetworkController.getTodayCurrentMeasurements(context)
                }
                R.id.tomorrow -> {
                    showDialog()
                    NetworkController.getTomorrowCurrentMeasurements(context)
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
                    NetworkController.getTodayRejectMeasurements(context)
                }
                R.id.tomorrow -> {
                    showDialog()
                    NetworkController.getTomorrowRejectMeasurements(context)
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
                    NetworkController.getTodayClosedMeasurements(context)
                }
                R.id.tomorrow -> {
                    showDialog()
                    NetworkController.getTomorrowClosedMeasurements(context)
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
            val realmonth = monthOfYear+1
            if (realmonth < 10) {
                date = "$year-0$realmonth-$day"
            } else {
                date = "$year-$realmonth-$day"
            }
            showDialog()
            NetworkController.getDateCurrentMeasurements(context, date)
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
            val realmonth = monthOfYear+1
            if (realmonth < 10) {
                date = "$year-0$realmonth-$day"
            } else {
                date = "$year-$realmonth-$day"
            }
            showDialog()
            NetworkController.getDateRejectMeasurements(context, date)
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
            val realmonth = monthOfYear+1
            if (realmonth < 10) {
                date = "$year-0$realmonth-$day"
            } else {
                date = "$year-$realmonth-$day"
            }
            showDialog()
            NetworkController.getDateClosedMeasurements(context, date)
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

    override fun callingBack(listMeasurements: List<Measurement>, result: Int, date: String) {
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
        val layoutManager = LinearLayoutManager(activity.applicationContext)
        recyclerList.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerList.context, layoutManager.orientation)
        recyclerList.addItemDecoration(dividerItemDecoration)

        val changeManager = ChangeManager.instance
        changeManager.notifyOnChange(date, listMeasurements) //callback в активити для изменения title

        alert.dismiss()
    }

}
