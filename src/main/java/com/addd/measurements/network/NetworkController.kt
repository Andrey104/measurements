package com.addd.measurements.network

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.widget.TextView
import com.addd.measurements.R
import com.addd.measurements.middleware.IMiddleware
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.modelAPI.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by addd on 11.12.2017.
 */
object NetworkController : IMiddleware {
    private var APP_TOKEN: String = "token"
    private val APP_LIST_TODAY_CURRENT = "listTodayCurrent"
    private val APP_LIST_TOMORROW_CURRENT = "listTomorrowCurrent"
    private val APP_LIST_TODAY_CLOSED = "listTodayClosed"
    private val APP_LIST_TOMORROW_CLOSED = "listTomorrowClosed"
    private val APP_LIST_TODAY_REJECTED = "listTodayRejected"
    private val APP_LIST_TOMORROW_REJECTED = "listTomorrowRejected"
    private val APP_USER_INFO: String = "userInfo"
    private lateinit var date: String
    private lateinit var token: String
    private val serviceAPI = MeasurementsAPI.Factory.create()
    var callback: Callback? = null
    var userCallback: UserInfoCallback? = null
    private lateinit var listMeasurements: List<Measurement>
    private lateinit var mSettings: SharedPreferences

    private fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        var day: String
        var realMonth: String
        day = if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            "0" + calendar.get(Calendar.DAY_OF_MONTH)
        } else {
            calendar.get(Calendar.DAY_OF_MONTH).toString()
        }
        val monthNumber = calendar.get(Calendar.MONTH) + 1
        if (monthNumber < 10) {
            realMonth = "0$monthNumber"
        } else {
            realMonth = "$monthNumber"
        }

        return "${calendar.get(Calendar.YEAR)}-$realMonth-$day"
    }

    private fun getTomorrowDate(): String {
        var day: String
        var realMonth: String
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        day = if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            "0" + calendar.get(Calendar.DAY_OF_MONTH)
        } else {
            calendar.get(Calendar.DAY_OF_MONTH).toString()
        }
        val monthNumber = calendar.get(Calendar.MONTH) + 1
        if (monthNumber < 10) {
            realMonth = "0$monthNumber"
        } else {
            realMonth = "$monthNumber"
        }
        return "${calendar.get(Calendar.YEAR)}-$realMonth-$day"
    }


    override fun getTodayCurrentMeasurements(context: Context) {
        date = getTodayDate()
        token = getToken(context)


        val call = serviceAPI.getCurrentMeasurement(token, date)
        call.enqueue(object : retrofit2.Callback<List<Measurement>> {
            override fun onResponse(call: Call<List<Measurement>>?, response: Response<List<Measurement>>?) {
                if (response!!.body() != null) {
                    listMeasurements = response.body()
                    saveMeasurementsList(context, listMeasurements, APP_LIST_TODAY_CURRENT)
                    callback?.result(listMeasurements, 0, date)
                }
            }

            override fun onFailure(call: Call<List<Measurement>>?, t: Throwable?) {
                listMeasurements = loadSharedPreferencesList(context, APP_LIST_TODAY_CURRENT)
                callback?.result(listMeasurements, 1, date)
            }
        })
    }

    override fun getTomorrowCurrentMeasurements(context: Context) {
        date = getTomorrowDate()
        token = getToken(context)

        val call = serviceAPI.getCurrentMeasurement(token, date)
        call.enqueue(object : retrofit2.Callback<List<Measurement>> {
            override fun onResponse(call: Call<List<Measurement>>?, response: Response<List<Measurement>>?) {
                if (response!!.body() != null) {
                    listMeasurements = response.body()
                    saveMeasurementsList(context, listMeasurements, APP_LIST_TOMORROW_CURRENT)
                    callback?.result(listMeasurements, 0, date)
                }
            }

            override fun onFailure(call: Call<List<Measurement>>?, t: Throwable?) {
                listMeasurements = loadSharedPreferencesList(context, APP_LIST_TOMORROW_CURRENT)
                callback?.result(listMeasurements, 1, date)
            }
        })
    }

    override fun getDateCurrentMeasurements(context: Context, date: String) {
        token = getToken(context)

        val call = serviceAPI.getCurrentMeasurement(token, date)
        call.enqueue(object : retrofit2.Callback<List<Measurement>> {
            override fun onResponse(call: Call<List<Measurement>>?, response: Response<List<Measurement>>?) {
                if (response!!.body() != null) {
                    listMeasurements = response.body()
                    callback?.result(listMeasurements, 0, date)
                }
            }

            override fun onFailure(call: Call<List<Measurement>>?, t: Throwable?) {
                callback?.result(emptyList<Measurement>(), 1, date)
            }
        })
    }

    override fun getTodayRejectMeasurements(context: Context) {
        date = getTodayDate()
        token = getToken(context)


        val call = serviceAPI.getRejectedMeasurement(token, date)
        call.enqueue(object : retrofit2.Callback<List<Measurement>> {
            override fun onResponse(call: Call<List<Measurement>>?, response: Response<List<Measurement>>?) {
                if (response!!.body() != null) {
                    listMeasurements = response.body()
                    saveMeasurementsList(context, listMeasurements, APP_LIST_TODAY_REJECTED)
                    callback?.result(listMeasurements, 0, date)
                }
            }

            override fun onFailure(call: Call<List<Measurement>>?, t: Throwable?) {
                listMeasurements = loadSharedPreferencesList(context, APP_LIST_TODAY_REJECTED)
                callback?.result(listMeasurements, 1, date)
            }
        })

    }

    override fun getTomorrowRejectMeasurements(context: Context) {
        date = getTomorrowDate()
        token = getToken(context)

        val call = serviceAPI.getRejectedMeasurement(token, date)
        call.enqueue(object : retrofit2.Callback<List<Measurement>> {
            override fun onResponse(call: Call<List<Measurement>>?, response: Response<List<Measurement>>?) {
                if (response!!.body() != null) {
                    listMeasurements = response.body()
                    saveMeasurementsList(context, listMeasurements, APP_LIST_TOMORROW_REJECTED)
                    callback?.result(listMeasurements, 0, date)
                }
            }

            override fun onFailure(call: Call<List<Measurement>>?, t: Throwable?) {
                listMeasurements = loadSharedPreferencesList(context, APP_LIST_TOMORROW_REJECTED)
                callback?.result(listMeasurements, 1, date)
            }
        })
    }

    override fun getDateRejectMeasurements(context: Context, date: String) {
        token = getToken(context)

        val call = serviceAPI.getRejectedMeasurement(token, date)
        call.enqueue(object : retrofit2.Callback<List<Measurement>> {
            override fun onResponse(call: Call<List<Measurement>>?, response: Response<List<Measurement>>?) {
                if (response!!.body() != null) {
                    listMeasurements = response.body()
                    callback?.result(listMeasurements, 0, date)
                }
            }

            override fun onFailure(call: Call<List<Measurement>>?, t: Throwable?) {
                callback?.result(emptyList<Measurement>(), 1, date)
            }
        })
    }

    override fun getTodayClosedMeasurements(context: Context) {
        date = getTodayDate()
        token = getToken(context)


        val call = serviceAPI.getClosedMeasurement(token, date)
        call.enqueue(object : retrofit2.Callback<List<Measurement>> {
            override fun onResponse(call: Call<List<Measurement>>?, response: Response<List<Measurement>>?) {
                if (response!!.body() != null) {
                    listMeasurements = response.body()
                    saveMeasurementsList(context, listMeasurements, APP_LIST_TODAY_CLOSED)
                    callback?.result(listMeasurements, 0, date)
                }
            }

            override fun onFailure(call: Call<List<Measurement>>?, t: Throwable?) {
                listMeasurements = loadSharedPreferencesList(context, APP_LIST_TODAY_CLOSED)
                callback?.result(listMeasurements, 1, date)
            }
        })
    }

    override fun getTomorrowClosedMeasurements(context: Context) {
        date = getTomorrowDate()
        token = getToken(context)

        val call = serviceAPI.getClosedMeasurement(token, date)
        call.enqueue(object : retrofit2.Callback<List<Measurement>> {
            override fun onResponse(call: Call<List<Measurement>>?, response: Response<List<Measurement>>?) {
                if (response!!.body() != null) {
                    listMeasurements = response.body()
                    saveMeasurementsList(context, listMeasurements, APP_LIST_TOMORROW_CLOSED)
                    callback?.result(listMeasurements, 0, date)
                }
            }

            override fun onFailure(call: Call<List<Measurement>>?, t: Throwable?) {
                listMeasurements = loadSharedPreferencesList(context, APP_LIST_TOMORROW_CLOSED)
                callback?.result(listMeasurements, 1, date)
            }
        })
    }

    override fun getDateClosedMeasurements(context: Context, date: String) {
        token = getToken(context)

        val call = serviceAPI.getClosedMeasurement(token, date)
        call.enqueue(object : retrofit2.Callback<List<Measurement>> {
            override fun onResponse(call: Call<List<Measurement>>?, response: Response<List<Measurement>>?) {
                if (response!!.body() != null) {
                    listMeasurements = response.body()
                    callback?.result(listMeasurements, 0, date)
                }
            }

            override fun onFailure(call: Call<List<Measurement>>?, t: Throwable?) {
                callback?.result(emptyList<Measurement>(), 1, date)
            }
        })
    }


    interface Callback {
        fun result(listMeasurements: List<Measurement>, result: Int, date: String)
    }

    fun registerCallBack(callback: Callback?) {
        NetworkController.callback = callback
    }

    interface UserInfoCallback {
        fun result(user: User)
    }

    fun registerUserInfoCallBack(callback: UserInfoCallback?) {
        NetworkController.userCallback = callback
    }

    private fun saveMeasurementsList(context: Context, list: List<Measurement>, name: String) {
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val prefsEditor = mPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        prefsEditor.putString(name, json)
        prefsEditor.commit()
    }

    private fun loadSharedPreferencesList(context: Context, name: String): List<Measurement> {
        var callLog: List<Measurement>
        val mSettings = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json = mSettings.getString(name, "")
        if (json!!.isEmpty()) {
            callLog = ArrayList<Measurement>()
        } else {
            val type = object : TypeToken<List<Measurement>>() {
            }.type
            callLog = gson.fromJson(json, type)
        }
        return callLog
    }

    private fun getToken(context: Context): String {
        mSettings = PreferenceManager.getDefaultSharedPreferences(context)
        var token = ""
        if (mSettings.contains(APP_TOKEN)) {
            token = "Token " + mSettings.getString(APP_TOKEN, "")
        }
        return token
    }

    fun getInfoUser(context: Context) {
        mSettings = PreferenceManager.getDefaultSharedPreferences(context)
        val token = getToken(context)
        val call = serviceAPI.userInfo(token)

        call.enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: Call<User>?, response: Response<User>?) {
                response?.let {
                    val userInfo: User
                    if (response.code() == 200) {
                        userInfo = response.body()
                        saveUserInfo(userInfo, context)
                    } else {
                        userInfo = loadSharedPreferencesUser(context)
                    }
                    userCallback?.result(userInfo)
                }

            }

            override fun onFailure(call: Call<User>?, t: Throwable?) {
                val userInfo = loadSharedPreferencesUser(context)
                userCallback?.result(userInfo)
            }
        })
    }

    private fun saveUserInfo(user: User, context: Context) {
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val prefsEditor = mPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(user)
        prefsEditor.putString(APP_USER_INFO, json)
        prefsEditor.commit()
    }

    private fun loadSharedPreferencesUser(context: Context): User {
        var user: User
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json = mPrefs.getString(APP_USER_INFO, "")
        user = if (json!!.isEmpty()) {
            User()
        } else {
            val type = object : TypeToken<User>() {
            }.type
            gson.fromJson(json, type)
        }
        return user
    }
}
