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

    @GET("deals/{id}")
    fun getOneDeal(@Path("id") id: String): Call<Deal>

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



    @POST("measurements/{id}/comment/")
    fun addComment(@Body text : CommentRequest, @Path("id") id: String): Call<Comment>


    @Multipart
    @POST("measurements/{id}/image_upload/")
    fun addPicture(@Path("id") id: String, @Part file : MultipartBody.Part ) : Call<Void>

    @GET("deals/current")
    fun getCurrentDeals(@Query("date") date: String,@Query("page") page: Int): Call<MyResultDeals>

    @GET("deals/rejected")
    fun getRejectedDeals(@Query("date") date: String,@Query("page") page: Int): Call<MyResultDeals>

    @GET("deals/closed")
    fun getClosedDeals(@Query("date") date: String,@Query("page") page: Int): Call<MyResultDeals>

    @GET("deals/current")
    fun getCurrentDeals(@Query("page") page: Int): Call<MyResultDeals>

    @GET("deals/rejected")
    fun getRejectedDeals(@Query("page") page: Int): Call<MyResultDeals>

    @GET("deals/closed")
    fun getClosedDeals(@Query("page") page: Int): Call<MyResultDeals>

    @GET("deals/{id}/measurements")
    fun getMeasurementsDeal(@Path("id") id: String): Call<List<Measurement>>


    @GET("deals/{id}/mounts")
    fun getMountsDeal(@Path("id") id: String): Call<List<Mount>>

    @POST("deals/{id}/discount/")
    fun addRecalculation(@Body recalculation: RecalculationRequest, @Path("id") id: String): Call<Void>

    @GET("measurements/search")
    fun searchMeasurement(@Query("text") text: String,@Query("page") page: Int): Call<MySearchResultMeasurement>

    @GET("deals/search")
    fun searchDeal(@Query("text") text: String,@Query("page") page: Int): Call<MyResultDeals>

    @GET("measurements/current")
    fun getCurrentMeasurement(@Query("date") date: String,@Query("page") page: Int, @Query("owner") owner: String): Call<MyResultMeasurements>

}
