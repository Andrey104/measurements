package com.addd.measurements.middleware

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.addd.measurements.MeasurementsAPI
import com.addd.measurements.R
import com.addd.measurements.adapters.DataAdapter
import com.addd.measurements.modelAPI.Measurement
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.measurements_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by addd on 11.12.2017.
 */
class MiddlewareImplementation : IMiddleware {
    private var APP_TOKEN: String = "token"
    private val APP_LIST_TODAY_NORMAL = "listTodayNormal"
    private val APP_LIST = "listMeasurements"
    private lateinit var date: String
    private val serviceAPI = MeasurementsAPI.Factory.create()
    lateinit var callback: Callback
    private lateinit var listMeasurements: List<Measurement>
    private lateinit var mSettings: SharedPreferences

    private fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        var day: String
        day = if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            "0" + calendar.get(Calendar.DAY_OF_MONTH)
        } else {
            calendar.get(Calendar.DAY_OF_MONTH).toString()
        }
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-$day"
    }

    private fun getTomorrowDate() : String {
        val calendar = Calendar.getInstance()
        var day: String
        day = if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            "0" + calendar.get(Calendar.DAY_OF_MONTH)
        } else {
            calendar.get(Calendar.DAY_OF_MONTH).toString()
        }
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-$day"
    }

    override fun getTodayNormalMeasurements(context: Context) {
        date = getTodayDate()
        mSettings = PreferenceManager.getDefaultSharedPreferences(context)
        var token = ""
        if (mSettings.contains(APP_TOKEN)) {
            token = "Token " + mSettings.getString(APP_TOKEN, "")
        }


        val call = serviceAPI.getMeasurements(token, date)
        call.enqueue(object : retrofit2.Callback<List<Measurement>> {
            override fun onResponse(call: Call<List<Measurement>>?, response: Response<List<Measurement>>?) {
                if (response!!.body() != null) {
                    listMeasurements = response.body()
                    saveMeasurementsList(context, listMeasurements,APP_LIST_TODAY_NORMAL)
                    callback.callingBack(listMeasurements, 0)
                }
            }

            override fun onFailure(call: Call<List<Measurement>>?, t: Throwable?) {
                listMeasurements = loadSharedPreferencesList(context,APP_LIST_TODAY_NORMAL)
                callback.callingBack(listMeasurements, 1)
            }
        })
    }

    override fun getTomorrowNormalMeasurements(context: Context) {
        val calendar = Calendar.getInstance()
        var day: String
        day = if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            "0" + calendar.get(Calendar.DAY_OF_MONTH)
        } else {
            calendar.get(Calendar.DAY_OF_MONTH).toString()
        }
        date = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-$day"


        val mSettings: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var token = ""
        if (mSettings.contains(APP_TOKEN)) {
            token = "Token " + mSettings.getString(APP_TOKEN, "")
        }


        val call = serviceAPI.getMeasurements(token, date)
        call.enqueue(object : retrofit2.Callback<List<Measurement>> {
            override fun onResponse(call: Call<List<Measurement>>?, response: Response<List<Measurement>>?) {
                if (response!!.body() != null) {
                    listMeasurements = response.body()
                    saveMeasurementsList(context, listMeasurements,APP_LIST_TODAY_NORMAL)
                    callback.callingBack(listMeasurements, 0)
                }
            }

            override fun onFailure(call: Call<List<Measurement>>?, t: Throwable?) {
                listMeasurements = loadSharedPreferencesList(context,APP_LIST_TODAY_NORMAL)
                callback.callingBack(listMeasurements, 1)
            }
        })
    }


    override fun getTodayEndMeasurements(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTodayRejectMeasurements(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun getTomorrowEndMeasurements(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTomorrowRejectMeasurements(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDateNormalMeasurements(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDateEndMeasurements(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDateRejectMeasurements(context: Context) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    interface Callback {
        fun callingBack(listMeasurements: List<Measurement>, result: Int)
    }

    fun registerCallBack(callback: Callback) {
        this.callback = callback
    }

    private fun saveMeasurementsList(context: Context, list: List<Measurement>, name:String) {
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val prefsEditor = mPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        prefsEditor.putString(name, json)
        prefsEditor.commit()
    }

    private fun loadSharedPreferencesList(context: Context, name: String): List<Measurement> {
        var callLog: List<Measurement>
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json = mPrefs.getString(name, "")
        if (json!!.isEmpty()) {
            callLog = ArrayList<Measurement>()
        } else {
            val type = object : TypeToken<List<Measurement>>() {
            }.type
            callLog = gson.fromJson(json, type)
        }
        return callLog
    }
}