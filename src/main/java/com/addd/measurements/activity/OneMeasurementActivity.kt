package com.addd.measurements.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.addd.measurements.R
import com.addd.measurements.adapters.ClientAdapter
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.network.NetworkController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_measurement.*
import kotlinx.android.synthetic.main.content_one_measurement.*


class OneMeasurementActivity : AppCompatActivity(), NetworkController.CallbackUpdateOneMeasurement {
    lateinit var measurement: Measurement
    private lateinit var alert: AlertDialog
    private lateinit var intentIdKey: String
    private lateinit var intentDealKey: String
    override fun onCreate(savedInstanceState: Bundle?) {
        intentDealKey = getString(R.string.deal)
        intentIdKey = getString(R.string.id)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_measurement)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            showPopupMenu(view)
        }
        displayMeasurement(getSavedMeasurement())


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_images, menu)
        return true
    }


    private fun getSavedMeasurement(): Measurement {
        val gson = Gson()
        if (intent != null && intent.hasExtra("measurement")) {
            val json = intent.getStringExtra("measurement")
            if (json.isEmpty()) {
                measurement = Measurement()
            } else {
                val type = object : TypeToken<Measurement>() {
                }.type
                measurement = gson.fromJson(json, type)
            }
        }
        return measurement
    }

    private fun displayMeasurement(measurement: Measurement) {
        title = "Замер ${intent.getStringExtra(getString(R.string.id))}"
        setStatus(measurement)


        if (measurement.company?.symbol?.length == 1) {
            symbol.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30F)
        }
        symbol.text = measurement.company?.symbol.toString()
        setColorSymbol(measurement)

        address.text = measurement.address.toString()
        time.text = measurement.time.toString()
        date.text = measurement.date.toString()
        if (measurement.workerName == null) {
            worker_name.text = getString(R.string.not_distributed)
        } else {
            worker_name.text = measurement.workerName.toString()
        }
        setColorWorker(measurement)
        comment.text = measurement.managerComment.toString()

        list_clients.adapter = ClientAdapter(measurement.clients!!)
        val layoutManager = LinearLayoutManager(applicationContext)
        list_clients.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(list_clients.context, layoutManager.orientation)
        list_clients.addItemDecoration(dividerItemDecoration)

    }

    private fun setColorSymbol(measurement: Measurement) {
        when (measurement.company?.id) {
            1 -> selectColorVersion(symbol, R.color.green)
            2 -> selectColorVersion(symbol, R.color.orange)
            3 -> selectColorVersion(symbol, R.color.blue)
        }
    }


    private fun selectColorVersion(item: TextView, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            item.setTextColor(resources.getColor(color, applicationContext.theme))
        } else {
            item.setTextColor(resources.getColor(color))
        }
    }

    private fun setColorWorker(measurement: Measurement) {
        when (measurement.color) {
            1 -> selectColorVersion(worker_name, R.color.red)
            2 -> selectColorVersion(worker_name, R.color.green)
            3 -> selectColorVersion(worker_name, R.color.blue)
        }
    }

    private fun setStatus(measurement: Measurement) {
        when (measurement.status) {
            0, 1 -> textViewStatus.text = getString(R.string.measurement_not_closed)
            2, 3 -> {
                textViewStatus.text = getString(R.string.measurement_closed)
                selectColorVersion(textViewStatus, R.color.red)
            }
            4 -> {
                textViewStatus.text = getString(R.string.measurement_reject)
                selectColorVersion(textViewStatus, R.color.red)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 200) {
            showDialog()
            NetworkController.getOneMeasurement(measurement.id.toString())
            setResult(200)
        }
    }

    private fun showPopupMenu(view: View) {
        var popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        //для отображения иконок
        try {
            val classPopupMenu = Class.forName(popupMenu.javaClass.name)
            val mPopup = classPopupMenu.getDeclaredField("mPopup")
            mPopup.isAccessible = true
            val menuPopupHelper = mPopup.get(popupMenu)
            val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
            val setForceIcons = classPopupHelper.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
            setForceIcons.invoke(menuPopupHelper, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.complete -> {
                    val intent = Intent(applicationContext, CompleteActivity::class.java)
                    intent.putExtra(intentIdKey, measurement.id.toString())
                    intent.putExtra(intentDealKey, measurement.deal)
                    startActivityForResult(intent, 0)
                    true
                }
                R.id.shift -> {
                    val intent = Intent(applicationContext, TransferActivity::class.java)
                    intent.putExtra(intentIdKey, measurement.id.toString())
                    startActivityForResult(intent, 0)
                    true
                }
                R.id.reject -> {
                    val intent = Intent(applicationContext, RejectActivity::class.java)
                    intent.putExtra(intentIdKey, measurement.id.toString())
                    startActivityForResult(intent, 0)
                    true
                }
                R.id.problem -> {
                    val intent = Intent(applicationContext, ProblemActivity::class.java)
                    intent.putExtra(intentIdKey, measurement.id.toString())
                    intent.putExtra(intentDealKey, measurement.deal.toString())
                    startActivityForResult(intent, 0)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val viewAlert = layoutInflater.inflate(R.layout.update_dialog, null)
        builder.setView(viewAlert)
                .setCancelable(false)
        alert = builder.create()
        alert.show()
    }

    override fun resultUpdate(measurement: Measurement?) {
        if (measurement != null) {
            displayMeasurement(measurement)
        } else {
            Toast.makeText(applicationContext, getString(R.string.update_error), Toast.LENGTH_SHORT).show()
        }
        alert.dismiss()
    }

    override fun onResume() {
        NetworkController.registerUpdateOneMeasurementCallback(this)
        super.onResume()
    }

    override fun onStop() {
        NetworkController.registerUpdateOneMeasurementCallback(null)
        super.onStop()
    }
}
