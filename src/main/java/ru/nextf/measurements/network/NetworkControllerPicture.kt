package ru.nextf.measurements.network

import android.content.Context
import android.preference.PreferenceManager
import ru.nextf.measurements.modelAPI.Measurement
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
    var callbackPictureAdd: PictureCallback? = null
    var updatePicturesOneMeasurement: UpdatePicturesCallback? = null
    private val BASE_URL = "http://188.225.46.31/api/"
    private val api: MeasurementsAPI by lazy { init(ru.nextf.measurements.MyApp.instance) }
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
        return retrofit.create(MeasurementsAPI::class.java)
    }

    fun addPictureFile(id: String, file: File?) {
        if (file != null) {
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val body = MultipartBody.Part.createFormData("url", file.name, requestFile)
            val call = api.addPicture(id, body)
            call.enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                    if (response?.code() == 413) {
                        //слишком большой
                    }
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
                    updatePicturesOneMeasurement?.resultUpdate(measurement)
                }
            }

            override fun onFailure(call: Call<Measurement>?, t: Throwable?) {
                updatePicturesOneMeasurement?.resultUpdate(null)
            }

        })
    }

    interface PictureCallback {
        fun resultPictureAdd(result: Boolean)
    }

    fun registerPictureCallback(callback: PictureCallback?) {
        callbackPictureAdd = callback
    }

    interface UpdatePicturesCallback {
        fun resultUpdate(measurement: Measurement?)
    }

    fun registerUpdateCallback(picturesCallback: UpdatePicturesCallback?) {
        updatePicturesOneMeasurement = picturesCallback
    }
}
