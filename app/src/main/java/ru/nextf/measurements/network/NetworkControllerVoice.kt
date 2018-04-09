package ru.nextf.measurements.network

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.GsonBuilder
import okhttp3.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.nextf.measurements.modelAPI.Comment
import java.io.File

/**
 * Created by left0ver on 09.04.18.
 */
object NetworkControllerVoice {
    var callbackVoiceAdd: VoiceCallback? = null
    var updatePicturesOneMeasurement: NetworkControllerPicture.UpdatePicturesCallback? = null
    private val BASE_URL = "http://natcom-crm.nextf.ru/api/"
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

    fun addVoiceFileMeasurement(id: String, file: File?) {
        if (file != null) {
            println(file.absolutePath)
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val call = api.addVoiceMeasurement(id, body, 2)
            call.enqueue(object : retrofit2.Callback<Comment> {
                override fun onResponse(call: Call<Comment>?, response: Response<Comment>?) {
                    if (response?.code() == 200) {
                        callbackVoiceAdd?.resultVoiceAdd(true, response.body())
                    } else {
                        callbackVoiceAdd?.resultVoiceAdd(false, null)
                    }
                }

                override fun onFailure(call: Call<Comment>?, t: Throwable?) {
                    callbackVoiceAdd?.resultVoiceAdd(false, null)
                }
            })
        }
    }

    fun addVoiceFileMount(id: String, file: File?) {
        if (file != null) {
            println(file.absolutePath)
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val call = api.addVoiceMount(id, body, 2)
            call.enqueue(object : retrofit2.Callback<Comment> {
                override fun onResponse(call: Call<Comment>?, response: Response<Comment>?) {
                    if (response?.code() == 200) {
                        callbackVoiceAdd?.resultVoiceAdd(true, response.body())
                    } else {
                        callbackVoiceAdd?.resultVoiceAdd(false, null)
                    }
                }

                override fun onFailure(call: Call<Comment>?, t: Throwable?) {
                    callbackVoiceAdd?.resultVoiceAdd(false, null)
                }
            })
        }
    }

    fun addVoiceFileDeal(id: String, file: File?) {
        if (file != null) {
            println(file.absolutePath)
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val call = api.addVoiceDeal(id, body, 2)
            call.enqueue(object : retrofit2.Callback<Comment> {
                override fun onResponse(call: Call<Comment>?, response: Response<Comment>?) {
                    if (response?.code() == 200) {
                        callbackVoiceAdd?.resultVoiceAdd(true, response.body())
                    } else {
                        callbackVoiceAdd?.resultVoiceAdd(false, null)
                    }
                }

                override fun onFailure(call: Call<Comment>?, t: Throwable?) {
                    callbackVoiceAdd?.resultVoiceAdd(false, null)
                }
            })
        }
    }

    interface VoiceCallback {
        fun resultVoiceAdd(result: Boolean, comment: Comment?)
    }

    fun registerVoiceCallback(voiceCall: VoiceCallback?) {
        this.callbackVoiceAdd = voiceCall
    }
}