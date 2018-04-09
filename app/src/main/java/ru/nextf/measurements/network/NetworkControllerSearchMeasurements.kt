package ru.nextf.measurements.network

import android.content.Context
import android.preference.PreferenceManager
import ru.nextf.measurements.modelAPI.Measurement
import ru.nextf.measurements.modelAPI.MySearchResultMeasurement
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by addd on 13.01.2018.
 */
object NetworkControllerSearchMeasurements {
    private lateinit var listMeasurements: List<Measurement>
    private lateinit var search: String
    private var date: String = "0"
    var callbackListMeasurements: CallbackListMeasurements? = null
    var callbackPaginationListMeasurements: PaginationCallback? = null

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

    fun search(search: String) {
        this.search = search
        val call = api.searchMeasurement(search, 1)
        call.enqueue(object : retrofit2.Callback<MySearchResultMeasurement> {
            override fun onResponse(call: Call<MySearchResultMeasurement>?, response: Response<MySearchResultMeasurement>?) {
                if (response?.code() == 200) {
                    response?.body()?.let {
                        listMeasurements = it.results ?: emptyList()
                        callbackListMeasurements?.resultList(listMeasurements, 0, date, it.count, 0, 0, it.count ?: 0)
                    }
                } else {
                    callbackListMeasurements?.resultList(emptyList(), 0, date, 0, 0, 0, 0)
                }
            }

            override fun onFailure(call: Call<MySearchResultMeasurement>?, t: Throwable?) {
                callbackListMeasurements?.resultList(emptyList(), 1, date, 0, 0, 0, 0)
            }

        })
    }

    fun pagination(page: Int, search: String) {
        val call = api.searchMeasurement(search, page)
        call.enqueue(object : retrofit2.Callback<MySearchResultMeasurement> {
            override fun onResponse(call: Call<MySearchResultMeasurement>?, response: Response<MySearchResultMeasurement>?) {
                response?.body()?.let {
                    listMeasurements = it.results ?: emptyList()
                    callbackPaginationListMeasurements?.resultPaginationClose(listMeasurements, 0)
                }
            }

            override fun onFailure(call: Call<MySearchResultMeasurement>?, t: Throwable?) {
                callbackPaginationListMeasurements?.resultPaginationClose(emptyList(), 1)
            }

        })
    }

    interface CallbackListMeasurements {
        fun resultList(listMeasurements: List<Measurement>, result: Int, date: String, allMeasurements: Int?, myMeasurements: Int?, notDistributed: Int?, count: Int)
    }

    fun registerCallBack(callbackListMeasurements: CallbackListMeasurements?) {
        NetworkControllerSearchMeasurements.callbackListMeasurements = callbackListMeasurements
    }

    interface PaginationCallback {
        fun resultPaginationClose(listMeasurements: List<Measurement>, result: Int)
    }

    fun registerPaginationCallback(callback: PaginationCallback?) {
        NetworkControllerSearchMeasurements.callbackPaginationListMeasurements = callback
    }
}