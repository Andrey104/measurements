package com.addd.measurements.network

import android.app.PendingIntent.getActivity
import android.content.Context
import android.net.Uri
import android.preference.PreferenceManager
import com.addd.measurements.MyApp
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URLConnection.guessContentTypeFromName
import android.support.annotation.NonNull
import com.addd.measurements.modelAPI.CommentRequest
import okhttp3.*
import retrofit2.Call
import retrofit2.Response
import java.net.URLConnection


/**
 * Created by addd on 26.12.2017.
 */
object NetworkControllerPicture {

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

    fun addPicture(uri: Uri?, id: String) {
       val body = MultipartBody.Part.createFormData("file", "file", RequestBody.create(MediaType.parse("image/*"), File(uri?.path)))

            val call = api.addPicture(body, id)
            call.enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                    if (response?.code() == 200) {
                    }
                }

                override fun onFailure(call: Call<Void>?, t: Throwable?) {
                }
            })

    }


    fun prepareFilePart(context: Context, partName: String, file: File): MultipartBody.Part {

        val mimeType = URLConnection.guessContentTypeFromName(file.name)
        val requestFile = RequestBody.create(MediaType.parse(mimeType), file)

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}