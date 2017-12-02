package com.addd.measurements

import retrofit2.Call
import retrofit2.http.*

/**
 * Created by addd on 02.12.2017.
 */
interface MeasurementsAPI {
    @FormUrlEncoded
    @POST("login/")
    fun performPostCall(@Field("username") login: String,@Field("password") password : String): Call<Any>
}