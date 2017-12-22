package com.addd.measurements.network

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.addd.measurements.*
import com.addd.measurements.modelAPI.*
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.ArrayList

/**
 * Created by addd on 11.12.2017.
 */
object NetworkController {
    private lateinit var date: String
    private lateinit var status: String

    var callbackListMeasurements: CallbackListMeasurements? = null
    var userCallback: UserInfoCallback? = null
    var updateOneMeasurement: CallbackUpdateOneMeasurement? = null
    var transferMeasurement: TransferMeasurementCallback? = null
    var responsible: ResponsibleCallback? = null
    var rejectCallback: RejectCallback? = null
    var closeCallback: CloseCallback? = null
    var problemCallback: ProblemCallback? = null
    var callbackPaginationListMeasurements: PaginationCallback? = null
    var problemListCallback: ProblemListCallback? = null
    var problemPaginationResultCallback: ProblemPaginationList? = null
    private lateinit var listMeasurements: List<Measurement>
    private lateinit var mSettings: SharedPreferences


    private val BASE_URL = "http://188.225.46.31/api/"
    private val api: MeasurementsAPI by lazy { init(MyApp.instance) }
    private fun init(context: Context): MeasurementsAPI {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val okHttpClient = OkHttpClient.Builder()
        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder().addHeader("Authorization", "Token " + sp.getString("token", ""))?.build()
            chain.proceed(request)
        }

        okHttpClient.networkInterceptors().add(interceptor)
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(GsonBuilder().create())).client(okHttpClient.build()).build()
        val api = retrofit.create(MeasurementsAPI::class.java)
        return api

    }

    //------------------------------запросы------------------------------------------
    fun getCurrentMeasurements(date: String, nameSave: String?) {
        this.date = date
        status = "current"
        val call = api.getCurrentMeasurement(date, 1)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    if (nameSave != null) {
                        saveMeasurementsList(listMeasurements, nameSave)
                    }
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed, response?.body()?.count ?: 0)
                }
            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                listMeasurements = if (nameSave != null) {
                    loadSharedPreferencesList(nameSave)
                } else {
                    emptyList()
                }
                callbackListMeasurements?.resultList(listMeasurements, 1, NetworkController.date, 0, 0, 0, 0)
            }

        })
    }

    fun getRejectMeasurements(date: String, nameSave: String?) {
        this.date = date
        status = "rejected"
        val call = api.getRejectedMeasurement(date, 1)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    if (nameSave != null) {
                        saveMeasurementsList(listMeasurements, nameSave)
                    }
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed, response?.body()?.count ?: 0)

                }
            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                listMeasurements = if (nameSave != null) {
                    loadSharedPreferencesList(nameSave)
                } else {
                    emptyList()
                }
                callbackListMeasurements?.resultList(listMeasurements, 1, date, 0, 0, 0, 0)
            }
        })
    }

    fun getCloseMeasurements(date: String, nameSave: String?) {
        status = "closed"
        this.date = date
        val call = api.getClosedMeasurement(date, 1)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    if (nameSave != null) {
                        saveMeasurementsList(listMeasurements, nameSave)
                    }
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, response.body().count, response.body().myMeasurements, response.body().notDistributed, response?.body()?.count ?: 0)

                }
            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                listMeasurements = if (nameSave != null) {
                    loadSharedPreferencesList(nameSave)
                } else {
                    emptyList()
                }
                callbackListMeasurements?.resultList(listMeasurements, 1, date, 0, 0, 0, 0)
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
                updateOneMeasurement?.resultUpdate(null)
            }

        })
    }

    fun doTransferMeasurement(transfer: Transfer, id: String) {
        val call = api.transferMeasurement(transfer, id)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                response?.let {
                    if (response.code() == 200) {
                        transferMeasurement?.resultTransfer(true)
                    }
                }

            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                transferMeasurement?.resultTransfer(false)
            }
        })
    }

    fun becomeResponsible(id: Int) {
        val call = api.becomeResponsible(id)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                response?.let {
                    if (response.code() == 200) {
                        responsible?.resultResponsible(true)
                    }
                }
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                responsible?.resultResponsible(false)

            }
        })
    }

    fun updateListInFragment() {
        when (status) {
            "current" -> {
                when (date) {
                    getTodayDate() -> getCurrentMeasurements(date, APP_LIST_TODAY_CURRENT)
                    getTomorrowDate() -> getCurrentMeasurements(date, APP_LIST_TOMORROW_CURRENT)
                    else -> getCurrentMeasurements(date, null)
                }

            }
            "rejected" -> {
                when (date) {
                    getTodayDate() -> getRejectMeasurements(date, APP_LIST_TODAY_REJECTED)
                    getTomorrowDate() -> getRejectMeasurements(date, APP_LIST_TOMORROW_REJECTED)
                    else -> getRejectMeasurements(date, null)
                }
            }
            "closed" -> {
                when (date) {
                    getTodayDate() -> getCloseMeasurements(date, APP_LIST_TODAY_CLOSED)
                    getTomorrowDate() -> getCloseMeasurements(date, APP_LIST_TOMORROW_CLOSED)
                    else -> getCloseMeasurements(date, null)
                }
            }
        }
    }

    fun pagination(page: Int) {
        when (status) {
            "current" -> {
                var nameList: String? = null
                if (date == getTodayDate()) {
                    nameList = APP_LIST_TODAY_CURRENT
                }
                if (date == getTomorrowDate()) {
                    nameList = APP_LIST_TOMORROW_CURRENT
                }
                paginationCurrentRequest(page, nameList)
            }

            "rejected" -> {
                var nameList: String? = null
                if (date == getTodayDate()) {
                    nameList = APP_LIST_TODAY_REJECTED
                }
                if (date == getTomorrowDate()) {
                    nameList = APP_LIST_TOMORROW_REJECTED
                }
                paginationRejectRequest(page, nameList)
            }
            "closed" -> {
                var nameList: String? = null
                if (date == getTodayDate()) {
                    nameList = APP_LIST_TODAY_CLOSED
                }
                if (date == getTomorrowDate()) {
                    nameList = APP_LIST_TOMORROW_CLOSED
                }
                paginationCloseRequest(page, nameList)
            }
        }

    }

    private fun paginationCurrentRequest(page: Int, name: String?) {
        val call = api.getCurrentMeasurement(date, page)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    if (name != null) {
                        val buferList = loadSharedPreferencesList(name) as ArrayList<Measurement>
                        buferList.addAll(listMeasurements)
                        saveMeasurementsList(buferList, name)
                    }
                    callbackPaginationListMeasurements?.resultPaginationClose(listMeasurements, 0)
                }
            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                callbackPaginationListMeasurements?.resultPaginationClose(emptyList(), 1)
            }

        })
    }

    private fun paginationRejectRequest(page: Int, name: String?) {
        val call = api.getRejectedMeasurement(date, page)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    if (name != null) {
                        val buferList = loadSharedPreferencesList(name) as ArrayList<Measurement>
                        buferList.addAll(listMeasurements)
                        saveMeasurementsList(buferList, name)
                    }
                    callbackPaginationListMeasurements?.resultPaginationClose(listMeasurements, 0)
                }
            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                callbackPaginationListMeasurements?.resultPaginationClose(emptyList(), 1)
            }

        })
    }

    private fun paginationCloseRequest(page: Int, name: String?) {
        val call = api.getClosedMeasurement(date, page)
        call.enqueue(object : retrofit2.Callback<MyResult> {
            override fun onResponse(call: Call<MyResult>?, response: Response<MyResult>?) {
                response?.body()?.let {
                    listMeasurements = response.body().results!!
                    if (name != null) {
                        val buferList = loadSharedPreferencesList(name) as ArrayList<Measurement>
                        buferList.addAll(listMeasurements)
                        saveMeasurementsList(buferList, name)
                    }
                    callbackPaginationListMeasurements?.resultPaginationClose(listMeasurements, 0)
                }
            }

            override fun onFailure(call: Call<MyResult>?, t: Throwable?) {
                callbackPaginationListMeasurements?.resultPaginationClose(emptyList(), 1)
            }

        })
    }

    fun rejectMeasurement(reject: Reject, id: String) {

        val call = api.rejectMeasurement(reject, id)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                response?.let {
                    if (response.code() == 200) {
                        rejectCallback?.resultReject(true)
                    }
                }
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                rejectCallback?.resultReject(false)
            }
        })
    }

    fun closeMeasurement(close: Close, id: String) {

        val call = api.closeMeasurement(close, id)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                response?.let {
                    if (response.code() == 200) {
                        closeCallback?.resultClose(true)
                    }
                }
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                closeCallback?.resultClose(false)
            }
        })
    }

    fun addProblem(problem: Problem, id: String) {
        val call = api.addProblem(problem, id)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                if (response?.code() == 200) {
                    problemCallback?.resultClose(true)
                }
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                problemCallback?.resultClose(false)
            }
        })
    }

    fun getProblems(page: Int) {
        val call = api.getProblems(page)
        call.enqueue(object : retrofit2.Callback<MyResultProblem> {
            override fun onResponse(call: Call<MyResultProblem>?, response: Response<MyResultProblem>?) {
                if (response?.code() == 200) {

                    problemListCallback?.resultProblemList(response.body()?.results ?: emptyList(), true,response.body().count ?: 1)

                }
            }

            override fun onFailure(call: Call<MyResultProblem>?, t: Throwable?) {
                problemListCallback?.resultProblemList(emptyList(), false,1)
            }
        })
    }

    fun paginationProblem(page: Int) {
        val call = api.getProblems(page)
        call.enqueue(object : retrofit2.Callback<MyResultProblem> {
            override fun onResponse(call: Call<MyResultProblem>?, response: Response<MyResultProblem>?) {
                if (response?.code() == 200) {

                    problemPaginationResultCallback?.problemPaginationResult(response.body()?.results ?: emptyList(), true)

                }
            }

            override fun onFailure(call: Call<MyResultProblem>?, t: Throwable?) {
                problemPaginationResultCallback?.problemPaginationResult(emptyList(), false)
            }
        })
    }
//----------------------------------внутренние функции класса------------------------------------------

    private fun saveMeasurementsList(list: List<Measurement>, name: String) {
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApp.instance)
        val prefsEditor = mPrefs.edit()
        val json = gson.toJson(list)
        prefsEditor.putString(name, json)
        prefsEditor.commit()
    }

    private fun loadSharedPreferencesList(name: String): List<Measurement> {
        var callLog: List<Measurement>
        val mSettings = PreferenceManager.getDefaultSharedPreferences(MyApp.instance)
        val json = mSettings.getString(name, "")
        if (json.isNullOrEmpty()) {
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
        val json = gson.toJson(user)
        prefsEditor.putString(APP_USER_INFO, json)
        prefsEditor.commit()
    }

    private fun loadSharedPreferencesUser(): User {
        var user: User
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(MyApp.instance)
        val json = mPrefs.getString(APP_USER_INFO, "")
        user = if (json.isNullOrEmpty()) {
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
        fun resultList(listMeasurements: List<Measurement>, result: Int, date: String, allMeasurements: Int?, myMeasurements: Int?, notDistributed: Int?, count: Int)
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
        fun resultTransfer(result: Boolean)
    }

    fun registerTransferMeasurementCallback(callback: TransferMeasurementCallback?) {
        NetworkController.transferMeasurement = callback
    }

    interface ResponsibleCallback {
        fun resultResponsible(result: Boolean)
    }

    fun registerResponsibleCallback(callback: ResponsibleCallback?) {
        NetworkController.responsible = callback
    }

    interface RejectCallback {
        fun resultReject(result: Boolean)
    }

    fun registerRejectCallback(callback: RejectCallback?) {
        NetworkController.rejectCallback = callback
    }

    interface CloseCallback {
        fun resultClose(result: Boolean)
    }

    fun registerCloseCallback(callback: CloseCallback?) {
        NetworkController.closeCallback = callback
    }

    interface ProblemCallback {
        fun resultClose(result: Boolean)
    }

    fun registerProblemCallback(callback: ProblemCallback?) {
        NetworkController.problemCallback = callback
    }

    interface PaginationCallback {
        fun resultPaginationClose(listMeasurements: List<Measurement>, result: Int)
    }

    fun registerPaginationCallback(callback: PaginationCallback?) {
        NetworkController.callbackPaginationListMeasurements = callback
    }

    interface ProblemListCallback {
        fun resultProblemList(listMeasurements: List<MyProblem>,result: Boolean, count: Int)
    }

    fun registerProblemListCallback(callback: ProblemListCallback?) {
        NetworkController.problemListCallback = callback
    }

    interface ProblemPaginationList {
        fun problemPaginationResult(list: List<MyProblem>, result: Boolean)
    }

    fun registerProblemPagination(callback: ProblemPaginationList) {
        NetworkController.problemPaginationResultCallback = callback

    }
}
