package com.addd.measurements.network

import com.addd.measurements.modelAPI.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


/**
 * Created by addd on 02.12.2017.
 */
interface MeasurementsAPI {
      @GET("user_info")
    fun userInfo(): Call<User>

    @GET("measurements/{id}")
    fun getOneMeasurement(@Path("id") id: String): Call<Measurement>

    @GET("measurements/current")
    fun getCurrentMeasurement(@Query("date") date: String,@Query("page") page: Int): Call<MyResultMeasurements>

    @GET("measurements/rejected")
    fun getRejectedMeasurement(@Query("date") date: String,@Query("page") page: Int): Call<MyResultMeasurements>

    @GET("measurements/closed")
    fun getClosedMeasurement(@Query("date") date: String,@Query("page") page: Int): Call<MyResultMeasurements>

    @POST("measurements/{id}/transfer/")
    fun transferMeasurement(@Body transfer: Transfer, @Path("id") id: String): Call<Void>

    @POST("measurements/{id}/take/")
    fun becomeResponsible(@Path("id") id: Int?): Call<Void>

    @POST("measurements/{id}/reject/")
    fun rejectMeasurement(@Body reject: Reject, @Path("id") id: String): Call<Void>

    @POST("measurements/{id}/close/")
    fun closeMeasurement(@Body close: Close, @Path("id") id: String): Call<Void>

    @POST("deals/{id}/discussion/")
    fun addProblem(@Body problem : ProblemRequest, @Path("id") id: String): Call<Void>

    @GET("discussions")
    fun getProblems(@Query("page") page: Int): Call<MyResultProblem>

    @POST("discussions/{id}/comment/")
    fun addComment(@Body text : CommentRequest, @Path("id") id: String): Call<Void>

    @GET("discussions/{id}")
    fun getProblems(@Path("id") id: String): Call<MyProblem>

    @Multipart
    @POST("measurements/{id}/image_upload/")
    fun addPicture(@Path("id") id: String, @Part file : MultipartBody.Part ) : Call<Void>

//    @GET("measurements/current")
//    fun getCurrentMeasurement(@Query("date") date: String,@Query("page") page: Int): Call<MyResultMeasurements>
//
//    @GET("measurements/rejected")
//    fun getRejectedMeasurement(@Query("date") date: String,@Query("page") page: Int): Call<MyResultMeasurements>
//
//    @GET("measurements/closed")
//    fun getClosedMeasurement(@Query("date") date: String,@Query("page") page: Int): Call<MyResultMeasurements>
}