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
import com.addd.measurements.*
import com.addd.measurements.activity.OneMeasurementActivity
import com.addd.measurements.adapters.DataAdapter
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.network.NetworkController
import kotlinx.android.synthetic.main.measurements_fragment.*
import java.util.*


/**
 * Created by addd on 03.12.2017.
 */

class MeasurementsFragment : Fragment(), NetworkController.CallbackListMeasurements, DataAdapter.CustomAdapterCallback, NetworkController.ResponsibleCallback {
    private lateinit var date: String
    lateinit var alert: AlertDialog
    lateinit var fragmentListMeasurements: List<Measurement>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        NetworkController.registerCallBack(this)
        NetworkController.registerResponsibleCallback(this)
        val view: View = inflater?.inflate(R.layout.measurements_fragment, container, false) ?: View(context)


        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottomNavigation)

        onClickMenu(bottomNavigationView)

        val bundle = this.arguments
        date = getTodayDate()
        showDialog()
        bundle?.let {
            when (bundle.getInt(CHECK)) {
                STATUS_CURRENT -> NetworkController.getCurrentMeasurements(date, APP_LIST_TODAY_CURRENT)
                STATUS_REJECT -> NetworkController.getRejectMeasurements(date, APP_LIST_TODAY_REJECTED)
                STATUS_CLOSE -> NetworkController.getCloseMeasurements(date, APP_LIST_TODAY_CLOSED)
            }
        }

        return view
    }

    private fun onClickMenu(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val bundle = this.arguments
            when (item.itemId) {
                R.id.today -> {
                    date = getTodayDate()
                    showDialog()
                    when (bundle.get(CHECK)) {
                        STATUS_CURRENT -> NetworkController.getCurrentMeasurements(date, APP_LIST_TODAY_CURRENT)
                        STATUS_REJECT -> NetworkController.getRejectMeasurements(date, APP_LIST_TODAY_REJECTED)
                        STATUS_CLOSE -> NetworkController.getCloseMeasurements(date, APP_LIST_TODAY_CLOSED)
                    }
                }
                R.id.tomorrow -> {
                    date = getTomorrowDate()
                    showDialog()
                    when (bundle.get(CHECK)) {
                        STATUS_CURRENT -> NetworkController.getCurrentMeasurements(date, APP_LIST_TOMORROW_CURRENT)
                        STATUS_REJECT -> NetworkController.getRejectMeasurements(date, APP_LIST_TOMORROW_REJECTED)
                        STATUS_CLOSE -> NetworkController.getCloseMeasurements(date, APP_LIST_TOMORROW_CLOSED)
                    }
                }
                R.id.date -> {
                    datePick()

                }
            }
            true
        }
    }

    override fun onItemClick(pos: Int) {
        val intent = Intent(context, OneMeasurementActivity::class.java)
        var id = fragmentListMeasurements[pos].id.toString()
        val json = gson.toJson(fragmentListMeasurements[pos])
        intent.putExtra("measurement", json)
        intent.putExtra("id", id)
        intent.putExtra("symbol", fragmentListMeasurements[pos].company?.symbol?.length.toString())
        startActivityForResult(intent, 0)
    }

    override fun onItemLongClick(pos: Int) {
        val ad = android.app.AlertDialog.Builder(context)
        ad.setTitle("Стать ответственным?")  // заголовок
        var id = fragmentListMeasurements[pos].id
        ad.setPositiveButton("Да") { dialog, arg1 ->
            if (id != null) {
                NetworkController.becomeResponsible(id)
            }
        }
        ad.setNegativeButton("Отмена") { dialog, arg1 -> }

        ad.setCancelable(true)
        ad.show()
    }

    private fun datePick() {
        val bundle = this.arguments
        val myCallBack = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            date = String.format("$year-%02d-%02d", monthOfYear + 1, dayOfMonth)
            showDialog()
            when (bundle.get(CHECK)) {
                STATUS_CURRENT -> NetworkController.getCurrentMeasurements(date, null)
                STATUS_REJECT -> NetworkController.getRejectMeasurements(date, null)
                STATUS_CLOSE -> NetworkController.getCloseMeasurements(date, null)
            }
        }
        val calendar = Calendar.getInstance()
        val datePikerDialog = DatePickerDialog(context, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePikerDialog.show()
    }

    fun updateList() {
        showDialog()
        NetworkController.updateListInFragment()
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
        fragmentListMeasurements = listMeasurements
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

        recyclerList.adapter = DataAdapter(listMeasurements,this)
        val layoutManager = LinearLayoutManager(activity.applicationContext)
        recyclerList.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerList.context, layoutManager.orientation)
        recyclerList.addItemDecoration(dividerItemDecoration)




        onChange(date, listMeasurements)
        alert.dismiss()
    }

    override fun resultResponsible(result: Boolean) {
        if (result) {
            updateList()
        }
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

    override fun onResume() {
        NetworkController.registerCallBack(this)
//        alert.dismiss()
        super.onResume()
    }

    override fun onStop() {
        NetworkController.registerCallBack(null)
        super.onStop()
    }
}
