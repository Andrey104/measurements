package com.addd.measurements.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.addd.measurements.MeasurementsAPI
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Measurement

import kotlinx.android.synthetic.main.activity_one_measurement.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OneMeasurementActivity : AppCompatActivity() {
    private lateinit var APP_TOKEN: String
    private val serviceAPI = MeasurementsAPI.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        APP_TOKEN = getString(R.string.token)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_measurement)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        Toast.makeText(applicationContext, intent.getStringExtra("symbol"), Toast.LENGTH_SHORT).show()
//        getOneMeasurement()
    }

    //берем один замер по id
    private fun getOneMeasurement() {
        val mSettings = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        var token = ""
        if (mSettings.contains(APP_TOKEN)) {
            token = "Token " + mSettings.getString(APP_TOKEN, "")
        }
        val call = serviceAPI.getOneMeasurement(token, intent.getStringExtra("id"))
        call.enqueue(object : Callback<Measurement> {
            override fun onResponse(call: Call<Measurement>?, response: Response<Measurement>?) {
                var phoneStr = response!!.body().clients!![0].client!!.phones!![0].number.toString()
                Toast.makeText(applicationContext, phoneStr, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<Measurement>?, t: Throwable?) {
            }

        })
    }




}
