package com.addd.measurements.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.addd.measurements.*
import com.addd.measurements.fragments.EmptyFragment
import com.addd.measurements.fragments.LoadFragment
import com.addd.measurements.fragments.MainMeasurementFragment
import com.addd.measurements.fragments.MeasurementPhotoFragment
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.network.NetworkController
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_measurement.*


class OneMeasurementActivity : AppCompatActivity(), NetworkController.CallbackUpdateOneMeasurement {
    lateinit var measurement: Measurement

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerUpdateOneMeasurementCallback(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_measurement)
        setSupportActionBar(toolbarAst)
        toolbarAst.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbarAst.setNavigationOnClickListener {
            finish()
        }
        if (!intent.hasExtra(MEASUREMENT_EXPANDED)) {
            measurement = getSavedMeasurement()
            val bundle = Bundle()
            bundle.putString(MEASUREMENT_KEY, intent.getStringExtra(MEASUREMENT_KEY))
            val fragment = MainMeasurementFragment()
            fragment.arguments = bundle
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.measurementContainerLayout, fragment).commit()
            title = String.format("Замер %05d", measurement.deal)
        } else {
            val fragment = LoadFragment()
            supportFragmentManager.beginTransaction().replace(R.id.measurementContainerLayout, fragment).commit()
            title = String.format("Замер %05d", intent.getIntExtra(ID_KEY, 0))
            NetworkController.getOneMeasurement(intent.getIntExtra(ID_KEY, 0).toString())
        }

        onItemClick()

    }

    private fun onItemClick() {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mainMeasurement -> {

                }
                R.id.commentsMeasurement -> {

                }
                R.id.picturesMeasurement -> {
                    val fragment = MeasurementPhotoFragment()
                    val json = gson.toJson(measurement) // будет ошибка если сразу нажать, не загрузив замер
                    val bundle = Bundle()
                    bundle.putString(MEASUREMENT_KEY, json)
                    fragment.arguments = bundle
                    supportFragmentManager.beginTransaction().replace(R.id.measurementContainerLayout, fragment).commit()
                }
            }
            true
        }
    }


    override fun resultUpdate(measurement: Measurement?) {
        if (measurement != null) {
            this.measurement = measurement
            val bundle = Bundle()
            bundle.putString(MEASUREMENT_KEY, gson.toJson(measurement))
            val fragment = MainMeasurementFragment()
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.measurementContainerLayout, fragment).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.measurementContainerLayout, EmptyFragment()).commit()
            toast(R.string.error_add_photo)
        }
    }

    private fun getSavedMeasurement(): Measurement {
        val json = intent.getStringExtra(MEASUREMENT_KEY)
        measurement = if (json.isNullOrEmpty()) {
            Measurement()
        } else {
            val type = object : TypeToken<Measurement>() {
            }.type
            gson.fromJson(json, type)
        }
        return measurement
    }

    override fun onDestroy() {
        NetworkController.registerUpdateOneMeasurementCallback(null)
        super.onDestroy()
    }
}
