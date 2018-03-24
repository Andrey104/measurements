package ru.nextf.measurements.network

import android.content.Context
import android.preference.PreferenceManager
import ru.nextf.measurements.modelAPI.Comment
import ru.nextf.measurements.modelAPI.CommentRequest
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
object NetworkControllerComment {
    var addCommentCallback: AddCommentCallback? = null
    private val BASE_URL = "http://natcom-crm.nextf.ru/api/"
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

    fun addComment(text: CommentRequest, id: String) {
        val call = api.addComment(text, id)
        call.enqueue(object : retrofit2.Callback<Comment> {
            override fun onResponse(call: Call<Comment>?, response: Response<Comment>?) {
                if (response?.code() == 200) {
                    addCommentCallback?.addCommentResult(true, response.body())
                } else {
                    addCommentCallback?.addCommentResult(false, null)
                }
            }

            override fun onFailure(call: Call<Comment>?, t: Throwable?) {
                addCommentCallback?.addCommentResult(false, null)
            }
        })
    }

    fun addCommentDeal(text: CommentRequest, id: String) {
        val call = api.addCommentDeal(text, id)
        call.enqueue(object : retrofit2.Callback<Comment> {
            override fun onResponse(call: Call<Comment>?, response: Response<Comment>?) {
                if (response?.code() == 200) {
                    addCommentCallback?.addCommentResult(true, response.body())
                } else {
                    addCommentCallback?.addCommentResult(false, null)
                }
            }

            override fun onFailure(call: Call<Comment>?, t: Throwable?) {
                addCommentCallback?.addCommentResult(false, null)
            }
        })
    }

    interface AddCommentCallback {
        fun addCommentResult(result: Boolean, comment: Comment?)
    }

    fun registerProblemPagination(callback: AddCommentCallback?) {
        addCommentCallback = callback

    }
}