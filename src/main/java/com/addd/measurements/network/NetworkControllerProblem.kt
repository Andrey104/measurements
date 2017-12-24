package com.addd.measurements.network

import android.content.Context
import android.preference.PreferenceManager
import com.addd.measurements.MyApp
import com.addd.measurements.modelAPI.MyProblem
import com.addd.measurements.modelAPI.MyResultProblem
import com.addd.measurements.modelAPI.ProblemRequest
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by addd on 24.12.2017.
 */
object NetworkControllerProblem {
    var addProblemCallback : AddProblemCallback? = null
    var problemListCallback : ProblemListCallback? = null
    var problemPaginationResultCallback : ProblemPaginationList? = null
    var oneProblemCallback : OneProblem? = null



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

    fun addProblem(problem: ProblemRequest, id: String) {
        val call = api.addProblem(problem, id)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                if (response?.code() == 200) {
                  addProblemCallback?.resultAddProblem(true)
                }
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                addProblemCallback?.resultAddProblem(false)
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

    fun getOneProblem(id: String) {
        val call = api.getProblems(id)
        call.enqueue(object : retrofit2.Callback<MyProblem> {
            override fun onResponse(call: Call<MyProblem>?, response: Response<MyProblem>?) {
                if (response?.code() == 200) {
                    oneProblemCallback?.resultGetOneProblem(response.body())

                }
            }

            override fun onFailure(call: Call<MyProblem>?, t: Throwable?) {
                oneProblemCallback?.resultGetOneProblem(null)
            }
        })
    }



    interface AddProblemCallback {
        fun resultAddProblem(result: Boolean)
    }

    fun registerAddProblemCallback(callback: AddProblemCallback?) {
        addProblemCallback = callback
    }

    interface ProblemListCallback {
        fun resultProblemList(listMeasurements: List<MyProblem>, result: Boolean, count: Int)
    }

    fun registerProblemListCallback(callback: ProblemListCallback?) {
        problemListCallback = callback
    }

    interface ProblemPaginationList {
        fun problemPaginationResult(list: List<MyProblem>, result: Boolean)
    }

    fun registerProblemPagination(callback: ProblemPaginationList) {
        problemPaginationResultCallback = callback

    }

    interface OneProblem {
        fun resultGetOneProblem(problem: MyProblem?)
    }

    fun registerGetOneProblemCallback(callback: OneProblem?) {
        oneProblemCallback = callback
    }
}