package com.addd.measurements.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import com.addd.measurements.*
import com.addd.measurements.fragments.LoadFragment
import com.addd.measurements.fragments.MainMeasurementFragment
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.network.NetworkController
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_measurement.*


class OneMeasurementActivity : AppCompatActivity(), NetworkController.CallbackUpdateOneMeasurement {
    lateinit var measurement: Measurement
    private lateinit var alert: AlertDialog
    private var status: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerUpdateOneMeasurementCallback(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_measurement)
        setSupportActionBar(toolbarAst)
        title = String.format("Замер %05d", measurement.deal)
        if (!intent.hasExtra(MEASUREMENT_EXPANDED)) {
            val bundle = Bundle()
            bundle.putString(MEASUREMENT_KEY, intent.getStringExtra(MEASUREMENT_KEY))
            val fragment = MainMeasurementFragment()
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.measurementContainerLayout, fragment).commit()
        } else {
            showDialog()
            NetworkController.getOneMeasurement(intent.getIntExtra(ID_KEY, 0).toString())
        }


    }


//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        if (item?.itemId == R.id.images) {
//            val intent = Intent(applicationContext, ImagesActivity::class.java)
//            val json = gson.toJson(measurement)
//            intent.putExtra(MEASUREMENT_KEY, json)
//            startActivityForResult(intent, 1)
//        }
//        return true
//    }

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
            val bundle = Bundle()
            bundle.putString(MEASUREMENT_KEY, gson.toJson(measurement))
            val fragment = MainMeasurementFragment()
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.measurementContainerLayout, fragment).commit()
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
