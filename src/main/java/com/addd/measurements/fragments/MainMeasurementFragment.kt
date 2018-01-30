package com.addd.measurements.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.addd.measurements.*
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.network.NetworkController
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.main_measurement_fragment.*

/**
 * Created by addd on 30.01.2018.
 */
class MainMeasurementFragment : Fragment() {
    private lateinit var alert: AlertDialog
    private var status: Int = 0
    private lateinit var measurement: Measurement
    private lateinit var bundle: Bundle

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater?.inflate(R.layout.main_measurement_fragment, container, false)
                ?: View(context)
        bundle = this.arguments
        measurement = if (bundle.containsKey(MEASUREMENT_KEY)) {
            getSavedMeasurement()
        } else {
            Measurement()
        }

        displayMeasurement(measurement)

        return view
    }

    private fun displayMeasurement(measurement: Measurement) {
        if (measurement.color != 2) {
            fab.hide()
        }

        setStatus(measurement)

//        if (measurement.sum != null) {
//            textViewSum.text = "Сумма " + measurement.sum.toString()
//            textViewSum.layoutParams = textViewLayoutParams
//            mainLayout.addView(textViewSum)
//        }
//        if (measurement.prepayment != null) {
//            textViewPrep.text = "Предоплата " + measurement.prepayment.toString()
//            textViewPrep.layoutParams = textViewLayoutParams
//            mainLayout.addView(textViewPrep)
//        }


        address.text = measurement.address.toString()
        time.text = measurement.time.toString()
        date.text = formatDate(measurement.date ?: "2000-20-20")
        if (measurement.worker == null) {
            worker_name.text = getString(R.string.not_distributed)
        } else {
            worker_name.text = measurement.worker.firstName + " " + measurement.worker.lastName
        }
        setColorWorker(measurement)
        comment.text = measurement.addressComment.toString()
    }

    private fun selectColorVersion(item: TextView, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            item.setTextColor(resources.getColor(color, context.theme))
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
            4, 5 -> {
                textViewStatus.text = getString(R.string.measurement_reject)
                selectColorVersion(textViewStatus, R.color.red)
                status = 1
            }
        }

    }

    private fun getSavedMeasurement(): Measurement {
        val json = bundle.getString(MEASUREMENT_KEY)
        measurement = if (json.isNullOrEmpty()) {
            Measurement()
        } else {
            val type = object : TypeToken<Measurement>() {
            }.type
            gson.fromJson(json, type)
        }
        return measurement
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 200) {
            showDialog()
            NetworkController.getOneMeasurement(measurement.id.toString())
//            setResult(200)
        }
    }

    private fun showDialog() {
//        val builder = AlertDialog.Builder(this)
        layoutInflater.inflate(R.layout.get_one_dialog, null)
//        builder.setView(viewAlert)
//                .setCancelable(false)
//        alert = builder.create()
//        alert.show()
    }
}