package ru.nextf.measurements.network

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.nextf.measurements.modelAPI.MeasurementNote

/**
 * Created by left0ver on 10.04.18.
 */
object NetworkNote {
    var noteEdit: NoteEditCallback? = null
    var updatePicturesOneMeasurement: NetworkControllerPicture.UpdatePicturesCallback? = null
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

    fun editNote(id: String, text: String) {
        val call = api.editNote(id, MeasurementNote(text))
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                if (response?.code() == 200) {
                    noteEdit?.resultNoteEdit(true)
                } else {
                    noteEdit?.resultNoteEdit(false)
                }
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                noteEdit?.resultNoteEdit(false)
            }
        })
    }


    interface NoteEditCallback {
        fun resultNoteEdit(result: Boolean)
    }

    fun registerNoteEditCallback(voiceCall: NoteEditCallback?) {
        this.noteEdit = voiceCall
    }
}