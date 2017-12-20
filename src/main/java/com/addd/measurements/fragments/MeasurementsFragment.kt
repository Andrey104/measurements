package com.addd.measurements.fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.addd.measurements.R
import com.addd.measurements.adapters.DataAdapter
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.network.NetworkController
import kotlinx.android.synthetic.main.measurements_fragment.*
import java.util.*




/**
 * Created by addd on 03.12.2017.
 */

class MeasurementsFragment : Fragment(), NetworkController.CallbackListMeasurements {
    private lateinit var date: String
    lateinit var alert: AlertDialog

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        NetworkController.registerCallBack(this)
        val view: View = inflater?.inflate(R.layout.measurements_fragment, container, false) ?: View(context)
        val bundle = this.arguments

        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottomNavigation)
        if (bundle != null) {
            when (bundle.getInt(getString(R.string.check))) {
                0 -> onClickCurrent(bottomNavigationView)

                1 -> onClickRejected(bottomNavigationView)

                2 -> onClickClosed(bottomNavigationView)

            }
        }

        if (bundle != null) {
            showDialog()
            when (bundle.getInt(getString(R.string.check))) {
                0 -> NetworkController.getTodayCurrentMeasurements(context)
                1 -> NetworkController.getTodayRejectMeasurements(context)
                2 -> NetworkController.getTodayClosedMeasurements(context)
            }
        }

        return view
    }

    override fun onResume() {
        NetworkController.registerCallBack(this)
        super.onResume()
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
            date = String.format("$year-%02d-%02dT00:00", monthOfYear + 1, dayOfMonth)
            showDialog()
            NetworkController.getDateCurrentMeasurements(context, date)
        }
        val calendar = Calendar.getInstance()
        val datePikerDialog = DatePickerDialog(context, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePikerDialog.show()
    }

    private fun dateRejectMeasurements() {
        val myCallBack = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            date = String.format("$year-%02d-%02dT00:00", monthOfYear + 1, dayOfMonth)
            showDialog()
            NetworkController.getDateRejectMeasurements(context, date)
        }
        val calendar = Calendar.getInstance()
        val datePikerDialog = DatePickerDialog(context, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePikerDialog.show()
    }

    private fun dateClosedMeasurements() {
        val myCallBack = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            date = String.format("$year-%02d-%02dT00:00", monthOfYear + 1, dayOfMonth)
            showDialog()
            NetworkController.getDateClosedMeasurements(context, date)
        }
        val calendar = Calendar.getInstance()
        val datePikerDialog = DatePickerDialog(context, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePikerDialog.show()
    }

    fun updateList() {
        showDialog()
        NetworkController.updateListInFragment(context)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 200) {
            updateList()
        }
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(context)
        val viewAlert = layoutInflater.inflate(R.layout.load_dialog, null)
        builder.setView(viewAlert)
                .setCancelable(false)
        alert = builder.create()
        alert.show()
    }

    override fun resultList(listMeasurements: List<Measurement>, result: Int, date: String, allMeasurements: Int?, myMeasurements: Int?, notDistributed: Int?) {
        if (listMeasurements.isEmpty()) {
            if (result == 1) {
                Toast.makeText(context, getString(R.string.no_save_data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, getString(R.string.nothing_show), Toast.LENGTH_SHORT).show()
            }
        } else {
            if (result == 0) {
//                Toast.makeText(context, "Данные загружены из сети", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        }
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.title = "$date В:$allMeasurements Н:$notDistributed M:$myMeasurements"

        recyclerList.adapter = DataAdapter(listMeasurements, this)
        val layoutManager = LinearLayoutManager(activity.applicationContext)
        recyclerList.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerList.context, layoutManager.orientation)
        recyclerList.addItemDecoration(dividerItemDecoration)




        onChange(date, listMeasurements)
        alert.dismiss()
    }

    private fun onChange(date: String, list: List<Measurement>) {
        val toolbar = (activity as AppCompatActivity).supportActionBar
        if (list.isEmpty()) {
            toolbar?.title = "$date"
        }
        var my = 0
        var wrong = 0
        for (measurement in list) {
            if (measurement.color == 1) {
                wrong++
            }
            if (measurement.color == 2) {
                my++
            }
        }
        toolbar?.title = "$date В:${list.size} Н:$wrong M:$my"
    }

    override fun onStop() {
        NetworkController.registerCallBack(null)
        super.onStop()
    }
}
