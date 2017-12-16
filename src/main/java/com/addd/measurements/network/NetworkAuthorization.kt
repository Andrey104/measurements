package com.addd.measurements.network

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.addd.measurements.modelAPI.Authorization
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by addd on 16.12.2017.
 */
object NetworkAuthorization {
    private val serviceAPI = AuthorizationAPI.Factory.create()
    private val APP_TOKEN = "token"
    var callback: MyCallback? = null

    fun authorization(login: String, password: String, context: Context) {
        val call = serviceAPI.authorization(login, password)

        call.enqueue(object : Callback<Authorization> {
            override fun onResponse(call: Call<Authorization>?, response: Response<Authorization>?) {
                response?.let {
                    if (response.code() != 200) {
                        callback!!.resultAuth(400)
                    } else if (response.code() == 200) {
                        val mSettings: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                        val editor: SharedPreferences.Editor = mSettings.edit()
                        editor.putString(APP_TOKEN, response.body().token)
                        editor.apply()
                        callback!!.resultAuth(200)
                    }
                }
            }

            override fun onFailure(call: Call<Authorization>?, t: Throwable?) {
                callback!!.resultAuth(500)
            }
        })
    }

    interface MyCallback {
        fun resultAuth(result: Int)
    }

    fun registerCallback(callback: MyCallback?) {
        this.callback = callback
    }
}
