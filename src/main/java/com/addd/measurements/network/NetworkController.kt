package com.addd.measurements.network

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.addd.measurements.LOGIN_KEY
import com.addd.measurements.MyApp
import com.addd.measurements.PASSWORD_KEY
import com.addd.measurements.middleware.IMiddleware
import com.addd.measurements.modelAPI.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * Created by addd on 11.12.2017.
 */
object NetworkController : IMiddleware {
    private val APP_LIST_TODAY_CURRENT = "listTodayCurrent"
    private val APP_LIST_TOMORROW_CURRENT = "listTomorrowCurrent"
    private val APP_LIST_TODAY_CLOSED = "listTodayClosed"
    private val APP_LIST_TOMORROW_CLOSED = "listTomorrowClosed"
    private val APP_LIST_TODAY_REJECTED = "listTodayRejected"
    private val APP_LIST_TOMORROW_REJECTED = "listTomorrowRejected"
    private val APP_USER_INFO: String = "userInfo"
    private lateinit var date: String
    private lateinit var status: String
    private var save: Boolean = true

    var callbackListMeasurements: CallbackListMeasurements? = null
    var userCallback: UserInfoCallback? = null
    var updateOneMeasurement: CallbackUpdateOneMeasurement? = null
    var transferMeasurement: TransferMeasurementCallback? = null
    var responsible: ResponsibleCallback? = null
    var rejectCallback: RejectCallback? = null
    var closeCallback: CloseCallback? = null
    private lateinit var listMeasurements: List<Measurement>
    private lateinit var mSettings: SharedPreferences



    private val BASE_URL = "http://188.225.46.31/api/"
    private val api: MeasurementsAPI by lazy { init(MyApp.instance) }
    private fun init(context: Context): MeasurementsAPI {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val okHttpClient = OkHttpClient.Builder()
        val interceptor = Interceptor { chain ->
            val request = chain?.request()?.newBuilder()?.addHeader("Authorization", "Token " + sp.getString("token", ""))?.build()
            chain?.proceed(request)
        }

        okHttpClient.networkInterceptors().add(interceptor)
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(GsonBuilder().create())).client(okHttpClient.build()).build()
        val api = retrofit.create(MeasurementsAPI::class.java)
        return api

    }

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

    //------------------------------запросы------------------------------------------
    override fun getTodayCurrentMeasurements() {
        date = getTodayDate()
        status = "current"
        save = true

        val call = api.getCurrent(date)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    saveMeasurementsList(listMeasurements, APP_LIST_TODAY_CURRENT)
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed)
                }
            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                listMeasurements = loadSharedPreferencesList(APP_LIST_TODAY_CURRENT)
                callbackListMeasurements?.resultList(listMeasurements, 1, date, 0, 0, 0)
            }

        })
    }

    override fun getTomorrowCurrentMeasurements() {
        save = true
        date = getTomorrowDate()
        status = "current"
        val call = api.getCurrentMeasurement(date)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    saveMeasurementsList(listMeasurements, APP_LIST_TOMORROW_CURRENT)
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed)

                }
            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                listMeasurements = loadSharedPreferencesList(APP_LIST_TOMORROW_CURRENT)
                callbackListMeasurements?.resultList(listMeasurements, 1, date, 0, 0, 0)
            }
        })
    }

    override fun getDateCurrentMeasurements(date: String) {
        save = false
        status = "current"
        this.date = date
        val call = api.getCurrentMeasurement(date)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed)

                }
            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                callbackListMeasurements?.resultList(emptyList<Measurement>(), 1, date, 0, 0, 0)
            }
        })
    }

    override fun getTodayRejectMeasurements() {
        save = true
        date = getTodayDate()
        status = "rejected"

        val call = api.getRejectedMeasurement(date)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    saveMeasurementsList(listMeasurements, APP_LIST_TODAY_REJECTED)
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed)

                }
            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                listMeasurements = loadSharedPreferencesList(APP_LIST_TODAY_REJECTED)
                callbackListMeasurements?.resultList(listMeasurements, 1, date, 0, 0, 0)
            }
        })

    }

    override fun getTomorrowRejectMeasurements() {
        save = true
        date = getTomorrowDate()
        status = "rejected"
        val call = api.getRejectedMeasurement(date)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    saveMeasurementsList(listMeasurements, APP_LIST_TOMORROW_REJECTED)
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed)

                }
            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                listMeasurements = loadSharedPreferencesList(APP_LIST_TOMORROW_REJECTED)
                callbackListMeasurements?.resultList(listMeasurements, 1, date, 0, 0, 0)
            }
        })
    }

    override fun getDateRejectMeasurements(date: String) {
        save = false
        status = "rejected"
        this.date = date

        val call = api.getRejectedMeasurement(date)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed)
                }

            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                callbackListMeasurements?.resultList(emptyList<Measurement>(), 1, date, 0, 0, 0)
            }
        })
    }

    override fun getTodayClosedMeasurements() {
        save = true

        date = getTodayDate()
        status = "closed"

        val call = api.getClosedMeasurement(date)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    saveMeasurementsList(listMeasurements, APP_LIST_TODAY_CLOSED)
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed)

                }
            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                listMeasurements = loadSharedPreferencesList(APP_LIST_TODAY_CLOSED)
                callbackListMeasurements?.resultList(listMeasurements, 1, date, 0, 0, 0)
            }
        })
    }

    override fun getTomorrowClosedMeasurements() {
        save = true
        date = getTomorrowDate()
        status = "closed"
        val call = api.getClosedMeasurement(date)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    saveMeasurementsList(listMeasurements, APP_LIST_TOMORROW_CLOSED)
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed)

                }
            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                listMeasurements = loadSharedPreferencesList(APP_LIST_TOMORROW_CLOSED)
                callbackListMeasurements?.resultList(listMeasurements, 1, date, 0, 0, 0)
            }
        })
    }

    override fun getDateClosedMeasurements(date: String) {
        save = false
        status = "closed"
        this.date = date
        val call = api.getClosedMeasurement(date)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed)
                }
            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                callbackListMeasurements?.resultList(emptyList<Measurement>(), 1, date, 0, 0, 0)
            }
        })
    }

    fun getOneMeasurement(id: String) {
        val call = api.getOneMeasurement(id)
        call.enqueue(object : retrofit2.Callback<Measurement> {
            override fun onResponse(call: Call<Measurement>?, response: Response<Measurement>?) {
                var measurement: Measurement? = null
                response?.body().let {
                    if (response?.code() == 200) {
                        measurement = response.body()
                    }
                    updateOneMeasurement?.resultUpdate(measurement)
                }
            }

            override fun onFailure(call: Call<Measurement>?, t: Throwable?) {
                updateOneMeasurement?.resultUpdate( null)
            }

        })
    }

    fun doTransferMeasurement(transfer: Transfer, id: String) {
        val call = api.transferMeasurement(transfer, id)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                response?.let {
                    transferMeasurement?.resultTransfer(response.code())
                }

            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                transferMeasurement?.resultTransfer(500)
            }
        })
    }

    fun becomeResponsible(id: Int) {
        val call = api.becomeResponsible(id)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                response?.let {
                    responsible?.resultResponsible(response?.code())
                }
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                responsible?.resultResponsible(500)

            }
        })
    }

    fun updateListInFragment() {
        when (status) {
            "current" -> {
                val call = api.getCurrentMeasurement(date)
                call.enqueue(object : retrofit2.Callback<MyResult> {
                    override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                        response?.body()?.let {
                            listMeasurements = response.body().results!!
                            if (save) {
                                saveMeasurementsList(listMeasurements, APP_LIST_TODAY_CURRENT)
                            }
                            callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed)
                        }
                    }

                    override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                        if (save) {
                            listMeasurements = loadSharedPreferencesList(APP_LIST_TODAY_CURRENT)
                            callbackListMeasurements?.resultList(listMeasurements, 1, date, 0, 0, 0)
                        } else {
                            callbackListMeasurements?.resultList(listMeasurements, 1, date, 0, 0, 0)
                        }
                    }
                })
            }
            "rejected" -> {
                val call = api.getRejectedMeasurement(date)
                call.enqueue(object : retrofit2.Callback<MyResult> {
                    override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                        response?.body()?.let {
                            listMeasurements = response.body().results!!
                            if (save) {
                                saveMeasurementsList(listMeasurements, APP_LIST_TODAY_REJECTED)
                            }
                            callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed)
                        }
                    }

                    override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                        if (save) {
                            listMeasurements = loadSharedPreferencesList(APP_LIST_TODAY_REJECTED)
                            callbackListMeasurements?.resultList(listMeasurements, 1, date, 0, 0, 0)
                        } else {
                            callbackListMeasurements?.resultList(listMeasurements, 1, date, 0, 0, 0)
                        }
                    }
                })
            }
            "closed" -> {
                val call = api.getClosedMeasurement(date)
                call.enqueue(object : retrofit2.Callback<MyResult> {
                    override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                        response?.body()?.let {
                            listMeasurements = response.body().results!!
                            if (save) {
                                saveMeasurementsList(listMeasurements, APP_LIST_TODAY_CLOSED)
                            }
                            callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed)
                        }
                    }

                    override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                        if (save) {
                            listMeasurements = loadSharedPreferencesList(APP_LIST_TODAY_CLOSED)
                            callbackListMeasurements?.resultList(listMeasurements, 1, date, 0, 0, 0)
                        } else {
                            callbackListMeasurements?.resultList(listMeasurements, 1, date, 0, 0, 0)
                        }
                    }
                })
            }
        }
    }

    fun rejectMeasurement(reject: Reject, id: String) {

        val call = api.rejectMeasurement(reject, id)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                response?.let {
                    rejectCallback?.resultReject(response?.code())
                }
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                rejectCallback?.resultReject(500)
            }
        })
    }

    fun closeMeasurement(close: Close, id: String) {

        val call = api.closeMeasurement(close, id)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                response?.let {
                    closeCallback?.resultClose(response?.code())
                }
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                closeCallback?.resultClose(500)
            }
        })
    }
//----------------------------------внутренние функции класса------------------------------------------

    private fun saveMeasurementsList(list: List<Measurement>, name: String) {
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApp.instance)
        val prefsEditor = mPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(list)
        prefsEditor.putString(name, json)
        prefsEditor.commit()
    }

    private fun loadSharedPreferencesList(name: String): List<Measurement> {
        var callLog: List<Measurement>
        val mSettings = PreferenceManager.getDefaultSharedPreferences(MyApp.instance)
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

    fun getInfoUser() {
        mSettings = PreferenceManager.getDefaultSharedPreferences(MyApp.instance)
        val call = api.userInfo()

        call.enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: Call<User>?, response: Response<User>?) {
                response?.let {
                    val userInfo: User
                    if (response.code() == 200) {
                        userInfo = response.body()
                        saveUserInfo(userInfo)
                    } else {
                        userInfo = loadSharedPreferencesUser()
                    }
                    userCallback?.result(userInfo)
                }

            }

            override fun onFailure(call: Call<User>?, t: Throwable?) {
                val userInfo = loadSharedPreferencesUser()
                userCallback?.result(userInfo)
            }
        })
    }

    private fun saveUserInfo(user: User) {
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApp.instance)
        val prefsEditor = mPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(user)
        prefsEditor.putString(APP_USER_INFO, json)
        prefsEditor.commit()
    }

    private fun loadSharedPreferencesUser(): User {
        var user: User
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApp.instance)
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


//---------------------------------------callbacks-------------------------------------------------------

    interface CallbackListMeasurements {
        fun resultList(listMeasurements: List<Measurement>, result: Int, date: String, allMeasurements: Int?, myMeasurements: Int?, notDistributed: Int?)
    }

    fun registerCallBack(callbackListMeasurements: CallbackListMeasurements?) {
        NetworkController.callbackListMeasurements = callbackListMeasurements
    }

    interface UserInfoCallback {
        fun result(user: User)
    }

    fun registerUserInfoCallBack(callback: UserInfoCallback?) {
        NetworkController.userCallback = callback
    }

    interface CallbackUpdateOneMeasurement {
        fun resultUpdate(measurement: Measurement?)
    }

    fun registerUpdateOneMeasurementCallback(callback: CallbackUpdateOneMeasurement?) {
        NetworkController.updateOneMeasurement = callback
    }

    interface TransferMeasurementCallback {
        fun resultTransfer(code: Int)
    }

    fun registerTransferMeasurementCallback(callback: TransferMeasurementCallback?) {
        NetworkController.transferMeasurement = callback
    }

    interface ResponsibleCallback {
        fun resultResponsible(code: Int)
    }

    fun registerResponsibleCallback(callback: ResponsibleCallback?) {
        NetworkController.responsible = callback
    }

    interface RejectCallback {
        fun resultReject(code: Int)
    }

    fun registerRejectCallback(callback: RejectCallback?) {
        NetworkController.rejectCallback = callback
    }

    interface CloseCallback {
        fun resultClose(code: Int)
    }

    fun registerCloseCallback(callback: CloseCallback?) {
        NetworkController.closeCallback = callback
    }
}
