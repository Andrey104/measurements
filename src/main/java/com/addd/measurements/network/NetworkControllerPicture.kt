package com.addd.measurements.network

import android.content.Context
import android.net.Uri
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.addd.measurements.MyApp
import com.addd.measurements.modelAPI.Measurement
import com.google.gson.GsonBuilder
import okhttp3.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


/**
 * Created by addd on 26.12.2017.
 */
object NetworkControllerPicture {
    var callbackPictureAdd : PictureCallback? = null
    private val BASE_URL = "http://188.225.46.31/api/"
    private val api: MeasurementsAPI by lazy { init(MyApp.instance) }
    private fun init(context: Context): MeasurementsAPI {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val okHttpClient = OkHttpClient.Builder()
        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Token " + sp.getString("token", ""))
                    .build()
            chain.proceed(request)
        }

        okHttpClient.networkInterceptors().add(interceptor)
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(GsonBuilder().create())).client(okHttpClient.build()).build()
        val api = retrofit.create(MeasurementsAPI::class.java)
        return api
    }

    fun addPicture(id: String, fileUri: Uri?) {
        val path = fileUri?.path
        val newPath = path?.slice(5 until path.length)
        val file = File(newPath)
        Toast.makeText(MyApp.instance, file.name, Toast.LENGTH_LONG).show()

        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("url", file.name, requestFile)
        val call = api.addPicture(id, body)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                if (response?.code() == 200) {
                    callbackPictureAdd?.resultPictureAdd(true)
                } else {
                    callbackPictureAdd?.resultPictureAdd(false)
                }
            }
            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                callbackPictureAdd?.resultPictureAdd(false)
            }
        })

    }

    interface PictureCallback {
        fun resultPictureAdd(result: Boolean)
    }

    fun registerPictureCallback(callback:PictureCallback?) {
        callbackPictureAdd = callback
    }
}
