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
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import com.addd.measurements.*
import com.addd.measurements.adapters.ClientAdapter
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.network.NetworkController
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_measurement.*
import kotlinx.android.synthetic.main.content_one_measurement.*
import java.util.*
import android.widget.LinearLayout


class OneMeasurementActivity : AppCompatActivity(), NetworkController.CallbackUpdateOneMeasurement {
    lateinit var measurement: Measurement
    private lateinit var alert: AlertDialog
    private var status: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerUpdateOneMeasurementCallback(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_measurement)
        setSupportActionBar(toolbarAst)
        if (!intent.hasExtra(MEASUREMENT_EXPANDED)) {
            measurement = getSavedMeasurement()
            displayMeasurement(measurement)
        } else {
            showDialog()
            NetworkController.getOneMeasurement(intent.getIntExtra(ID_KEY, 0).toString())
        }


        fab.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_images, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.images) {
            val intent = Intent(applicationContext, ImagesActivity::class.java)
            val json = gson.toJson(measurement)
            intent.putExtra(MEASUREMENT_KEY, json)
            startActivityForResult(intent, 1)
        }
        return true
    }

    private fun getSavedMeasurement(): Measurement {
        val json = intent?.getStringExtra(MEASUREMENT_KEY)
        measurement = if (json.isNullOrEmpty()) {
            Measurement()
        } else {
            val type = object : TypeToken<Measurement>() {
            }.type
            gson.fromJson(json, type)
        }
        return measurement
    }

    private fun displayMeasurement(measurement: Measurement) {
        if (measurement.color != 2) {
            fab.hide()
        }

        title = String.format("Замер %05d", measurement.deal)
        setStatus(measurement)

        val mainLayout = findViewById<LinearLayout>(R.id.linearLayoutOneMeasurement)
        val textViewSum = TextView(applicationContext)
        val textViewPrep = TextView(applicationContext)
        val textViewLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        if (measurement.sum != null) {
            textViewSum.text = "Сумма " + measurement.sum.toString()
            textViewSum.layoutParams = textViewLayoutParams
            mainLayout.addView(textViewSum)
        }
        if (measurement.prepayment != null) {
            textViewPrep.text = "Предоплата " + measurement.prepayment.toString()
            textViewPrep.layoutParams = textViewLayoutParams
            mainLayout.addView(textViewPrep)
        }

        if (measurement.company?.symbol?.length == 1) {
            symbol.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30F)
        }
        symbol.text = measurement.company?.symbol.toString()
        setColorSymbol(measurement)

        address.text = measurement.address.toString()
        time.text = measurement.time.toString()
        val strBuilder = StringBuilder(measurement.date.toString())
        var index = -1
        for (char in strBuilder) {
            index++
            if (char == 'T' || char == 'Т') break
        }
        strBuilder.delete(index, strBuilder.length)
        date.text = strBuilder.toString()
        if (measurement.worker == null) {
            worker_name.text = getString(R.string.not_distributed)
        } else {
            worker_name.text = measurement.worker.firstName + " " + measurement.worker.lastName
        }
        setColorWorker(measurement)
        comment.text = measurement.managerComment.toString()

        list_clients.adapter = ClientAdapter(measurement.clients ?: Collections.emptyList())
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
                status = 2
            }
            4 -> {
                textViewStatus.text = getString(R.string.measurement_reject)
                selectColorVersion(textViewStatus, R.color.red)
                status = 1
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
        if (status == 1) {
            popupMenu.menu.findItem(R.id.complete).isVisible = false
            popupMenu.menu.findItem(R.id.shift).isVisible = false
            popupMenu.menu.findItem(R.id.reject).isVisible = false
        }
        if (status == 2) {
            popupMenu.menu.findItem(R.id.complete).isVisible = false
            popupMenu.menu.findItem(R.id.shift).isVisible = false
            popupMenu.menu.findItem(R.id.reject).isVisible = false
            popupMenu.menu.findItem(R.id.deal).isVisible = !intent.hasExtra(FROM_DEAL)
        }


        // для отображения иконок
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
                    intent.putExtra(ID_KEY, measurement.id.toString())
                    intent.putExtra(DEAL_KEY, measurement.deal)
                    startActivityForResult(intent, 0)
                    true
                }
                R.id.shift -> {
                    val intent = Intent(applicationContext, TransferActivity::class.java)
                    intent.putExtra(ID_KEY, measurement.id.toString())
                    startActivityForResult(intent, 0)
                    true
                }
                R.id.reject -> {
                    val intent = Intent(applicationContext, RejectActivity::class.java)
                    intent.putExtra(ID_KEY, measurement.id.toString())
                    startActivityForResult(intent, 0)
                    true
                }
                R.id.problem -> {
                    val intent = Intent(applicationContext, ProblemActivity::class.java)
                    intent.putExtra(DEAL_KEY, measurement.deal.toString())
                    startActivityForResult(intent, 0)
                    true
                }
                R.id.deal -> {
                    val intent = Intent(applicationContext, OneDealActivity::class.java)
                    intent.putExtra(DEAL_ID, measurement.deal.toString())
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
        var viewAlert: View = if (intent.hasExtra(MEASUREMENT_EXPANDED)) {
            layoutInflater.inflate(R.layout.get_one_dialog, null)
        } else {
            layoutInflater.inflate(R.layout.update_dialog, null)
        }
        builder.setView(viewAlert)
                .setCancelable(false)
        alert = builder.create()
        alert.show()
    }

    override fun resultUpdate(measurement: Measurement?) {
        if (measurement != null) {
            this.measurement = measurement
            displayMeasurement(measurement)
        } else {
            toast(R.string.update_error)
        }
        alert.dismiss()
    }

    override fun onDestroy() {
        NetworkController.registerUpdateOneMeasurementCallback(null)
        super.onDestroy()
    }
}
