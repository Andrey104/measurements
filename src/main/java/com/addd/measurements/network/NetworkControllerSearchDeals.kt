package com.addd.measurements.network

import android.content.Context
import android.preference.PreferenceManager
import com.addd.measurements.MyApp
import com.addd.measurements.modelAPI.Deal
import com.addd.measurements.modelAPI.MyResultDeals
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
object NetworkControllerSearchDeals {
    private lateinit var search : String
    private lateinit var listDeals: List<Deal>
    private var callbackListDeals: CallbackListDeals?= null
    private var callbackPaginationListDeals: PaginationCallback?= null

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
        return retrofit.create(MeasurementsAPI::class.java)

    }

    fun search(search : String) {
        this.search = search
        val call = api.searchDeal(search,1)
        call.enqueue(object : retrofit2.Callback<MyResultDeals> {
            override fun onResponse(call: Call<MyResultDeals>?, response: Response<MyResultDeals>?) {
                response?.body()?.let {
                    listDeals = it.results!!
                    callbackListDeals?.resultList(listDeals, 0,  it?.count ?: 1)
                }
            }

            override fun onFailure(call: Call<MyResultDeals>?, t: Throwable?) {
                callbackListDeals?.resultList(emptyList(), 1,  0)
            }

        })
    }

    fun pagination(page: Int) {
        val call = api.searchDeal(search,page)
        call.enqueue(object : retrofit2.Callback<MyResultDeals> {
            override fun onResponse(call: Call<MyResultDeals>?, response: Response<MyResultDeals>?) {
                response?.body()?.let {
                    listDeals = it.results!!
                    callbackPaginationListDeals?.resultPagination(listDeals, 0)
                }
            }

            override fun onFailure(call: Call<MyResultDeals>?, t: Throwable?) {
                callbackPaginationListDeals?.resultPagination(emptyList(), 1)
            }

        })
    }

    interface CallbackListDeals {
        fun resultList(listDeals: List<Deal>, result: Int, count: Int)
    }

    fun registerCallBack(callbackListDeals: CallbackListDeals?) {
        NetworkControllerSearchDeals.callbackListDeals = callbackListDeals
    }

    interface PaginationCallback {
        fun resultPagination(listDeals: List<Deal>, result: Int)
    }

    fun registerPaginationCallback(callback: PaginationCallback?) {
        NetworkControllerSearchDeals.callbackPaginationListDeals = callback
    }
}