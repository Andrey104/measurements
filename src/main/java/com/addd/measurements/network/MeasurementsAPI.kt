package com.addd.measurements.network

import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.modelAPI.Reject
import com.addd.measurements.modelAPI.Transfer
import com.addd.measurements.modelAPI.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


/**
 * Created by addd on 02.12.2017.
 */
interface MeasurementsAPI {
      @GET("user_info")
    fun userInfo(@Header("Authorization") authorization: String): Call<User>

    @GET("measurements/{id}")
    fun getOneMeasurement(@Header("Authorization") authorization: String, @Path("id") id: String): Call<Measurement>

    @GET("measurements/current")
    fun getCurrentMeasurement(@Header("Authorization") authorization: String, @Query("date") date: String): Call<List<Measurement>>

    @GET("measurements/rejected")
    fun getRejectedMeasurement(@Header("Authorization") authorization: String, @Query("date") date: String): Call<List<Measurement>>

    @GET("measurements/closed")
    fun getClosedMeasurement(@Header("Authorization") authorization: String, @Query("date") date: String): Call<List<Measurement>>

    @POST("measurements/{id}/transfer/")
    fun transferMeasurement(@Header("Authorization") authorization: String, @Body transfer: Transfer, @Path("id") id: String): Call<Void>

    @POST("measurements/{id}/take/")
    fun becomeResponsible(@Header("Authorization") authorization: String, @Path("id") id: Int?): Call<Void>

    @POST("measurements/{id}/reject/")
    fun rejectMeasurement(@Header("Authorization") authorization: String, @Body reject: Reject, @Path("id") id: String): Call<Void>



    companion object Factory {

        fun create(): MeasurementsAPI {
            val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://188.225.46.31/api/")
                    .build()

            return retrofit.create(MeasurementsAPI::class.java)
        }
    }
}