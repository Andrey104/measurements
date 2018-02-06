package com.addd.measurements.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.addd.measurements.*
import com.addd.measurements.fragments.*
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.network.NetworkController
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_measurement.*


class OneMeasurementActivity : AppCompatActivity(), NetworkController.CallbackUpdateOneMeasurement,
        MeasurementPhotoFragment.MyCallbackMeasurement, CommentsMeasurementFragment.CommentCallback {
    lateinit var measurement: Measurement
    private var isMainPage = false
    private var isCommentPage = false
    private var isPicturePage = false
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
            title = String.format("Замер %05d", measurement.deal)
            mainPage()
        } else {
            bottomNavigation.visibility = View.INVISIBLE
            val fragment = LoadFragment()
            supportFragmentManager.beginTransaction().replace(R.id.measurementContainerLayout, fragment).commit()
            title = String.format("Замер %05d", intent.getIntExtra(ID_KEY, 0))
            NetworkController.getOneMeasurement(intent.getIntExtra(ID_KEY, 0).toString())
        }
        onItemClick()

    }

    private fun mainPage() {
        if (!isMainPage) {
            val bundle = Bundle()
            bundle.putString(MEASUREMENT_KEY, gson.toJson(measurement))
            val fragment = MainMeasurementFragment()
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.measurementContainerLayout, fragment).commit()
        }
    }

    private fun commentPage() {
        if (!isCommentPage) {
            supportFragmentManager.beginTransaction().replace(R.id.measurementContainerLayout, EmptyFragment()).commit()
            val fragment = CommentsMeasurementFragment()
            fragment.registerCommentCallback(this)
            val bundle = Bundle()
            bundle.putString(MEASUREMENT_KEY, gson.toJson(measurement))
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.measurementContainerLayout, fragment).commit()
        }
    }

    private fun picturePage() {
        if (!isPicturePage) {
            val fragment = MeasurementPhotoFragment()
            fragment.registerMyCallback(this)
            val json = gson.toJson(measurement) // будет ошибка если сразу нажать, не загрузив замер
            val bundle = Bundle()
            bundle.putString(MEASUREMENT_KEY, json)
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.measurementContainerLayout, fragment).commit()
        }
    }

    private fun onItemClick() {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.mainMeasurement -> {
                    mainPage()
                    isMainPage = true
                    isCommentPage = false
                    isPicturePage = false
                }
                R.id.commentsMeasurement -> {
                    commentPage()
                    isMainPage = false
                    isCommentPage = true
                    isPicturePage = false
                }
                R.id.picturesMeasurement -> {
                    picturePage()
                    isMainPage = false
                    isCommentPage = false
                    isPicturePage = true
                }
            }
            true
        }
    }


    override fun resultUpdate(measurement: Measurement?) {
        if (measurement != null) {
            bottomNavigation.visibility = View.VISIBLE
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

    override fun getMeasurement(measurement: Measurement) {
        this.measurement = measurement
    }

    override fun getMeasurementComment(measurement: Measurement) {
        this.measurement = measurement
    }

    override fun onDestroy() {
        NetworkController.registerUpdateOneMeasurementCallback(null)
        super.onDestroy()
    }
}
