package ru.nextf.measurements.network

import android.content.Context
import android.preference.PreferenceManager
import ru.nextf.measurements.modelAPI.Measurement
import ru.nextf.measurements.modelAPI.MyResultMeasurements
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by addd on 02.02.2018.
 */
object NetworkControllerFree {
    private lateinit var date: String
    private lateinit var listMeasurements: List<Measurement>
    private var callbackListFree: CallbackListFree? = null
    private var paginationCallbackFree: CallbackPaginationFree? = null


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

    fun getCurrentMeasurements(date: String, page: Int, owner: String) {
        this.date = date
        val call = api.getCurrentMeasurement(date, page, owner)
        call.enqueue(object : retrofit2.Callback<MyResultMeasurements> {
            override fun onResponse(call: Call<MyResultMeasurements>?, response: Response<MyResultMeasurements>?) {
                response?.body()?.let {
                    if (page == 1) {
                        listMeasurements = it.results ?: emptyList()
                        callbackListFree?.resultListFree(listMeasurements, 0, date, it.count, it.myMeasurements, it.notDistributed, it.count
                                ?: 0)
                    } else {
                        listMeasurements = it.results ?: emptyList()
                        paginationCallbackFree?.paginationFree(listMeasurements, 0)
                    }
                }
            }

            override fun onFailure(call: Call<MyResultMeasurements>?, t: Throwable?) {
                if (page == 1) {
                    callbackListFree?.resultListFree(emptyList(), 1, date, 0, 0, 0, 0)
                } else {
                    paginationCallbackFree?.paginationFree(emptyList(), 1)
                }
            }

        })
    }


    interface CallbackListFree {
        fun resultListFree(listMeasurements: List<Measurement>, result: Int, date: String, allMeasurements: Int?, myMeasurements: Int?, notDistributed: Int?, count: Int)
    }

    fun registerCallbackFree(callbackListMeasurements: CallbackListFree?) {
        NetworkControllerFree.callbackListFree = callbackListMeasurements
    }

    interface CallbackPaginationFree {
        fun paginationFree(listMeasurements: List<Measurement>, result: Int)
    }

    fun registerPaginationFree(callback: CallbackPaginationFree?) {
        NetworkControllerFree.paginationCallbackFree = callback
    }
}