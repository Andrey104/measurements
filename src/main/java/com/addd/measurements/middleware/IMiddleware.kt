package com.addd.measurements.middleware

import android.content.Context
import com.addd.measurements.modelAPI.Measurement

/**
 * Created by addd on 11.12.2017.
 */
interface IMiddleware {
    fun getTodayCurrentMeasurements(context: Context)
    fun getTodayClosedMeasurements(context: Context)
    fun getTodayRejectMeasurements(context: Context)

    fun getTomorrowCurrentMeasurements(context: Context)
    fun getTomorrowClosedMeasurements(context: Context)
    fun getTomorrowRejectMeasurements(context: Context)

    fun getDateCurrentMeasurements(context: Context, date : String)
    fun getDateClosedMeasurements(context: Context, date : String)
    fun getDateRejectMeasurements(context: Context,date : String)
}