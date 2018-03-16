package ru.nextf.measurements.network

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import ru.nextf.measurements.modelAPI.*
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.nextf.measurements.*
import kotlin.collections.ArrayList

/**
 * Created by addd on 11.12.2017.
 */
object NetworkController {
    private lateinit var date: String
    lateinit var status: String
    private lateinit var search: String

    var callbackListMeasurements: CallbackListMeasurements? = null
    var userCallback: UserInfoCallback? = null
    var updateOneMeasurement: CallbackUpdateOneMeasurement? = null
    var transferMeasurement: TransferMeasurementCallback? = null
    var responsible: ResponsibleCallback? = null
    var rejectCallback: RejectCallback? = null
    var closeCallback: CloseCallback? = null
    var fragmentOneCallback: CallbackUpdateOneMeasurementFragment? = null
    var callbackMeasurementsDealCallback: MeasurementsDealCallback? = null
    var callbackPaginationListMeasurements: PaginationCallback? = null
    private lateinit var listMeasurements: List<Measurement>
    private lateinit var mSettings: SharedPreferences


    private val BASE_URL = "http://188.225.46.31/api/"
    private val api: MeasurementsAPI by lazy { init(ru.nextf.measurements.MyApp.instance) }
    private fun init(context: Context): MeasurementsAPI {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val okHttpClient = OkHttpClient.Builder()
        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder().addHeader("Authorization", "Token " + sp.getString("token", ""))?.build()
            chain.proceed(request)
        }

        okHttpClient.networkInterceptors().add(interceptor)
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(GsonBuilder().create())).client(okHttpClient.build()).build()
        return retrofit.create(MeasurementsAPI::class.java)

    }

    //------------------------------запросы------------------------------------------
    fun getMeasurementsDeals(id: String) {
        val call = api.getMeasurementsDeal(id)
        call.enqueue(object : retrofit2.Callback<List<Measurement>> {
            override fun onResponse(call: Call<List<Measurement>>?, response: Response<List<Measurement>>?) {
                response?.body()?.let {
                    if (response.isSuccessful) {
                        callbackMeasurementsDealCallback?.resultMeasurementsDeal(it, true)
                    }
                }
                if (!(response?.isSuccessful ?: true)) {
                    callbackMeasurementsDealCallback?.resultMeasurementsDeal(emptyList(), false)
                }

            }

            override fun onFailure(call: Call<List<Measurement>>?, t: Throwable?) {
                callbackMeasurementsDealCallback?.resultMeasurementsDeal(emptyList(), false)
            }

        })
    }

    fun getCurrentMeasurements(date: String, nameSave: String?) {
        this.date = date
        status = "current"
        val call = api.getCurrentMeasurement(date, 1)
        call.enqueue(object : retrofit2.Callback<MyResultMeasurements> {
            override fun onResponse(call: Call<MyResultMeasurements>?, response: Response<MyResultMeasurements>?) {
                response?.body()?.let {
                    listMeasurements = it.results ?: emptyList()
                    if (nameSave != null) {
                        saveMeasurementsList(listMeasurements, nameSave)
                    }
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, it.count, it.myMeasurements, it.notDistributed, it.count
                            ?: 0)
                }
            }

            override fun onFailure(call: Call<MyResultMeasurements>?, t: Throwable?) {
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
        call.enqueue(object : retrofit2.Callback<MyResultMeasurements> {
            override fun onResponse(call: Call<MyResultMeasurements>?, response: Response<MyResultMeasurements>?) {
                response?.body()?.let {
                    listMeasurements = it.results ?: emptyList()
                    if (nameSave != null) {
                        saveMeasurementsList(listMeasurements, nameSave)
                    }
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, it.count, it.myMeasurements, it.notDistributed, it.count
                            ?: 0)

                }
            }

            override fun onFailure(call: Call<MyResultMeasurements>?, t: Throwable?) {
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
        call.enqueue(object : retrofit2.Callback<MyResultMeasurements> {
            override fun onResponse(call: Call<MyResultMeasurements>?, response: Response<MyResultMeasurements>?) {
                response?.body()?.let {
                    listMeasurements = it.results ?: emptyList()
                    if (nameSave != null) {
                        saveMeasurementsList(listMeasurements, nameSave)
                    }
                    callbackListMeasurements?.resultList(listMeasurements, 0, date, it.count, it.myMeasurements, it.notDistributed, it.count
                            ?: 0)

                }
            }

            override fun onFailure(call: Call<MyResultMeasurements>?, t: Throwable?) {
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
                        updateOneMeasurement?.resultUpdate(measurement)
                    } else {
                        updateOneMeasurement?.resultUpdate(null)
                    }
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
                    } else {
                        transferMeasurement?.resultTransfer(false)
                    }
                }

            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                transferMeasurement?.resultTransfer(false)
            }
        })
    }

    fun getOneMeasurementFragment(id: String) {
        val call = api.getOneMeasurement(id)
        call.enqueue(object : retrofit2.Callback<Measurement> {
            override fun onResponse(call: Call<Measurement>?, response: Response<Measurement>?) {
                var measurement: Measurement? = null
                response?.body().let {
                    if (response?.code() == 200) {
                        measurement = response.body()
                        fragmentOneCallback?.resultUpdate(measurement)
                    } else {
                        fragmentOneCallback?.resultUpdate(null)
                    }
                }
            }

            override fun onFailure(call: Call<Measurement>?, t: Throwable?) {
                fragmentOneCallback?.resultUpdate(null)
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
        call.enqueue(object : retrofit2.Callback<MyResultMeasurements> {
            override fun onResponse(call: Call<MyResultMeasurements>?, response: Response<MyResultMeasurements>?) {
                response?.body()?.let {
                    listMeasurements = it.results ?: emptyList()
                    if (name != null) {
                        val buferList = loadSharedPreferencesList(name) as ArrayList<Measurement>
                        buferList.addAll(listMeasurements)
                        saveMeasurementsList(buferList, name)
                    }
                    callbackPaginationListMeasurements?.resultPaginationClose(listMeasurements, 0)
                }
            }

            override fun onFailure(call: Call<MyResultMeasurements>?, t: Throwable?) {
                callbackPaginationListMeasurements?.resultPaginationClose(emptyList(), 1)
            }

        })
    }

    private fun paginationRejectRequest(page: Int, name: String?) {
        val call = api.getRejectedMeasurement(date, page)
        call.enqueue(object : retrofit2.Callback<MyResultMeasurements> {
            override fun onResponse(call: Call<MyResultMeasurements>?, response: Response<MyResultMeasurements>?) {
                response?.body()?.let {
                    listMeasurements = it.results ?: emptyList()
                    if (name != null) {
                        val buferList = loadSharedPreferencesList(name) as ArrayList<Measurement>
                        buferList.addAll(listMeasurements)
                        saveMeasurementsList(buferList, name)
                    }
                    callbackPaginationListMeasurements?.resultPaginationClose(listMeasurements, 0)
                }
            }

            override fun onFailure(call: Call<MyResultMeasurements>?, t: Throwable?) {
                callbackPaginationListMeasurements?.resultPaginationClose(emptyList(), 1)
            }

        })
    }

    private fun paginationCloseRequest(page: Int, name: String?) {
        val call = api.getClosedMeasurement(date, page)
        call.enqueue(object : retrofit2.Callback<MyResultMeasurements> {
            override fun onResponse(call: Call<MyResultMeasurements>?, response: Response<MyResultMeasurements>?) {
                response?.body()?.let {
                    listMeasurements = it.results ?: emptyList()
                    if (name != null) {
                        val buferList = loadSharedPreferencesList(name) as ArrayList<Measurement>
                        buferList.addAll(listMeasurements)
                        saveMeasurementsList(buferList, name)
                    }
                    callbackPaginationListMeasurements?.resultPaginationClose(listMeasurements, 0)
                }
            }

            override fun onFailure(call: Call<MyResultMeasurements>?, t: Throwable?) {
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
                    } else {
                        closeCallback?.resultClose(false)
                    }
                }
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                closeCallback?.resultClose(false)
            }
        })
    }


//----------------------------------внутренние функции класса------------------------------------------

    private fun saveMeasurementsList(list: List<Measurement>, name: String) {
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(ru.nextf.measurements.MyApp.instance)
        val prefsEditor = mPrefs.edit()
        val json = gson.toJson(list)
        prefsEditor.putString(name, json)
        prefsEditor.apply()
    }

    private fun loadSharedPreferencesList(name: String): List<Measurement> {
        var callLog: List<Measurement>
        val mSettings = PreferenceManager.getDefaultSharedPreferences(ru.nextf.measurements.MyApp.instance)
        val json = mSettings.getString(name, "")
        if (json.isNullOrEmpty()) {
            callLog = ArrayList()
        } else {
            val type = object : TypeToken<List<Measurement>>() {
            }.type
            callLog = gson.fromJson(json, type)
        }
        return callLog
    }

    fun getInfoUser() {
        mSettings = PreferenceManager.getDefaultSharedPreferences(ru.nextf.measurements.MyApp.instance)
        val call = api.userInfo()

        call.enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: Call<User>?, response: Response<User>?) {
                response?.let {
                    val userInfo: User
                    if (response.code() == 200) {
                        userInfo = it.body() ?: User()
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
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(ru.nextf.measurements.MyApp.instance)
        val prefsEditor = mPrefs.edit()
        val json = gson.toJson(user)
        prefsEditor.putString(APP_USER_INFO, json)
        prefsEditor.apply()
    }

    private fun loadSharedPreferencesUser(): User {
        var user: User
        val mPrefs = PreferenceManager.getDefaultSharedPreferences(ru.nextf.measurements.MyApp.instance)
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

    interface CallbackUpdateOneMeasurementFragment {
        fun resultUpdate(measurement: Measurement?)
    }

    fun registerOneMeasurementCallbackFragment(callback: CallbackUpdateOneMeasurementFragment?) {
        NetworkController.fragmentOneCallback = callback
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

    interface PaginationCallback {
        fun resultPaginationClose(listMeasurements: List<Measurement>, result: Int)
    }

    fun registerPaginationCallback(callback: PaginationCallback?) {
        NetworkController.callbackPaginationListMeasurements = callback
    }

    interface MeasurementsDealCallback {
        fun resultMeasurementsDeal(listMeasurements: List<Measurement>, result: Boolean)
    }

    fun registerMeasurementsDealCallback(callback: MeasurementsDealCallback?) {
        NetworkController.callbackMeasurementsDealCallback = callback
    }
}
