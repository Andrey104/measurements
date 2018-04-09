package ru.nextf.measurements.network

import ru.nextf.measurements.modelAPI.Authorization
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by addd on 16.12.2017.
 */
interface AuthorizationAPI {
    @FormUrlEncoded
    @POST("login/")
    fun authorization(@Field("username") login: String, @Field("password") password: String): Call<Authorization>

    companion object Factory {

        fun create(): AuthorizationAPI {
            val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://188.225.46.31/api/")
                    .build()

            return retrofit.create(AuthorizationAPI::class.java)
        }
    }
}