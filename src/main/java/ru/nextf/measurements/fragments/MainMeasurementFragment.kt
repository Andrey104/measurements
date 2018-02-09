package ru.nextf.measurements.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import ru.nextf.measurements.activity.CompleteActivity
import ru.nextf.measurements.activity.OneDealActivity
import ru.nextf.measurements.activity.RejectActivity
import ru.nextf.measurements.*
import ru.nextf.measurements.activity.TransferActivity
import ru.nextf.measurements.adapters.ClientAdapter
import ru.nextf.measurements.modelAPI.Measurement
import ru.nextf.measurements.network.NetworkController
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.main_measurement_fragment.view.*

/**
 * Created by addd on 30.01.2018.
 */
class MainMeasurementFragment : Fragment() {
    private lateinit var measurement: Measurement
    private lateinit var bundle: Bundle
    private lateinit var fabOpen: Animation
    private lateinit var fabOpen08: Animation
    private lateinit var fabClose: Animation
    private lateinit var mView: View
    private var isFabOpen = false
    private var ONLY_DEAL = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater?.inflate(ru.nextf.measurements.R.layout.main_measurement_fragment, container, false)
                ?: View(context)

        fabOpen = AnimationUtils.loadAnimation(context, ru.nextf.measurements.R.anim.fab_open)
        fabOpen08 = AnimationUtils.loadAnimation(context, ru.nextf.measurements.R.anim.fab_open_08)
        fabClose = AnimationUtils.loadAnimation(context, ru.nextf.measurements.R.anim.fab_close)

        bundle = this.arguments
        measurement = if (bundle.containsKey(MEASUREMENT_KEY)) {
            getSavedMeasurement()
        } else {
            Measurement()
        }

        displayMeasurement(measurement)

        mView.fabComplete.setOnClickListener {
            hideFub()
            completeMeasurement()
        }

        mView.fabReject.setOnClickListener {
            hideFub()
            rejectMeasurement()
        }

        mView.fabTransfer.setOnClickListener {
            hideFub()
            transferMeasurement()
        }

        mView.fabGoDeal.setOnClickListener {
            closeOnlyDealFAB()
            goDeal()
        }

        mView.mainConstraintLayout.setOnTouchListener { _, _ ->
            if (isFabOpen) {
                if (ONLY_DEAL) {
                    closeOnlyDealFAB()
                } else {
                    hideFub()
                }
            }
            false
        }
        mView.recycleClient.setOnTouchListener { _, _ ->
            if (isFabOpen) {
                if (ONLY_DEAL) {
                    closeOnlyDealFAB()
                } else {
                    hideFub()
                }
            }
            false
        }

        return mView
    }

    private fun goDeal() {
        val intent = Intent(context, OneDealActivity::class.java)
        intent.putExtra(DEAL_ID, measurement.deal.toString())
        startActivityForResult(intent, 0)
    }

    private fun completeMeasurement() {
        val intent = Intent(context, CompleteActivity::class.java)
        intent.putExtra(ID_KEY, measurement.id.toString())
        intent.putExtra(DEAL_KEY, measurement.deal)
        startActivityForResult(intent, 0)
        true
    }

    private fun rejectMeasurement() {
        val intent = Intent(context, RejectActivity::class.java)
        intent.putExtra(ID_KEY, measurement.id.toString())
        startActivityForResult(intent, 0)
        true
    }

    private fun transferMeasurement() {
        val intent = Intent(context, TransferActivity::class.java)
        intent.putExtra(ID_KEY, measurement.id.toString())
        startActivityForResult(intent, 0)
        true
    }

    private fun displayMeasurement(measurement: Measurement) {
        if (measurement.color != 2) {
            mView.fabMain.hide()
        }

        val mp = ResourcesCompat.getDrawable(ru.nextf.measurements.MyApp.instance.resources, ru.nextf.measurements.R.drawable.mp, null)
        val n = ResourcesCompat.getDrawable(ru.nextf.measurements.MyApp.instance.resources, ru.nextf.measurements.R.drawable.n, null)
        val b = ResourcesCompat.getDrawable(ru.nextf.measurements.MyApp.instance.resources, ru.nextf.measurements.R.drawable.b, null)

        when (measurement.company?.id) {
            1 -> mView.symbol.background = mp
            2 -> mView.symbol.background = b
            3 -> mView.symbol.background = n
        }

        if (measurement.sum == null) {
            mView.constraintLayoutHide.visibility = View.GONE
        } else {
            if (measurement.prepayment == null) {
                mView.textViewSum.text = "${measurement.sum} р"
            } else {
                mView.textViewSum.text = "${measurement.sum} р (Предоплата: ${measurement.prepayment} р)"
            }
        }

        setStatus(measurement)

        mView.recycleClient.adapter = ClientAdapter(measurement.clients
                ?: emptyList(), layoutInflater, activity)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mView.recycleClient.layoutManager = layoutManager

        mView.address.text = measurement.address.toString()
        mView.time.text = measurement.time.toString()
        mView.date.text = formatDate(measurement.date ?: "2000-20-20")
        if (measurement.worker == null) {
            mView.worker_name.text = getString(ru.nextf.measurements.R.string.not_distributed)
        } else {
            mView.worker_name.text = measurement.worker.firstName + " " + measurement.worker.lastName
        }
        setColorWorker(measurement)
        if (measurement.addressComment.isNullOrEmpty()) {
            mView.comment.visibility = View.GONE
        } else {
            mView.comment.text = measurement.addressComment.toString()
        }
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
            1 -> selectColorVersion(mView.worker_name, ru.nextf.measurements.R.color.red)
            2 -> selectColorVersion(mView.worker_name, ru.nextf.measurements.R.color.green)
            3 -> selectColorVersion(mView.worker_name, ru.nextf.measurements.R.color.blue)
        }
    }

    private fun setStatus(measurement: Measurement) {
        when (measurement.status) {
            0, 1 -> {
                ONLY_DEAL = false
                mView.textViewStatus.text = getString(ru.nextf.measurements.R.string.measurement_not_closed)
                mView.fabMain.setOnClickListener { showFubs() }
                mView.fabMainClose.setOnClickListener { hideFub() }
            }
            2, 3 -> {
                ONLY_DEAL = true
                mView.textViewStatus.text = getString(ru.nextf.measurements.R.string.measurement_closed)
                selectColorVersion(mView.textViewStatus, ru.nextf.measurements.R.color.red)
                mView.fabMain.setOnClickListener {
                    isFabOpen = true
                    mView.fabMainClose.startAnimation(fabOpen)
                    mView.fabGoDeal.startAnimation(fabOpen08)
                    mView.fabMainClose.isClickable = true
                    mView.fabGoDeal.isClickable = true
                    mView.fabMain.isClickable = false
                }
                mView.fabMainClose.setOnClickListener {
                    closeOnlyDealFAB()
                }
            }
            4, 5 -> {
                ONLY_DEAL = false
                mView.textViewStatus.text = getString(ru.nextf.measurements.R.string.measurement_reject)
                selectColorVersion(mView.textViewStatus, ru.nextf.measurements.R.color.red)
                mView.fabMain.hide()
                mView.fabTransfer.hide()
                mView.fabReject.hide()
                mView.fabComplete.hide()
            }
        }

    }

    private fun closeOnlyDealFAB() {
        if (isFabOpen) {
            mView.fabMainClose.startAnimation(fabClose)
            mView.fabGoDeal.startAnimation(fabClose)
            mView.fabMain.startAnimation(fabOpen)
            mView.fabMainClose.isClickable = false
            mView.fabGoDeal.isClickable = false
            mView.fabMain.isClickable = true
        }
    }

    private fun hideFub() {
        if (isFabOpen) {
            mView.fabComplete.startAnimation(fabClose)
            mView.fabMainClose.startAnimation(fabClose)
            mView.fabReject.startAnimation(fabClose)
            mView.fabTransfer.startAnimation(fabClose)
            mView.fabMain.startAnimation(fabOpen)
            mView.fabComplete.isClickable = false
            mView.fabMainClose.isClickable = false
            mView.fabReject.isClickable = false
            mView.fabTransfer.isClickable = false
            mView.fabMain.isClickable = true
            isFabOpen = false
        }
    }

    private fun showFubs() {
        mView.fabMain.isClickable = false
        mView.fabMain.startAnimation(fabClose)
        mView.fabMainClose.startAnimation(fabOpen)
        mView.fabTransfer.startAnimation(fabOpen08)
        mView.fabReject.startAnimation(fabOpen08)
        mView.fabComplete.startAnimation(fabOpen08)
        mView.fabReject.isClickable = true
        mView.fabMainClose.isClickable = true
        mView.fabComplete.isClickable = true
        mView.fabTransfer.isClickable = true
        isFabOpen = true

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
            activity.supportFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.measurementContainerLayout, LoadFragment()).commit()
            NetworkController.getOneMeasurement(measurement.id.toString())
            activity.setResult(200)
        }
    }
}