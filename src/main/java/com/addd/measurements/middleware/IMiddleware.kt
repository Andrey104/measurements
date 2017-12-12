package com.addd.measurements.middleware

import android.content.Context
import com.addd.measurements.modelAPI.Measurement

/**
 * Created by addd on 11.12.2017.
 */
interface IMiddleware {
    fun getTodayNormalMeasurements(context: Context)
    fun getTodayEndMeasurements(context: Context)
    fun getTodayRejectMeasurements(context: Context)
    fun getTomorrowNormalMeasurements(context: Context)
    fun getTomorrowEndMeasurements(context: Context)
    fun getTomorrowRejectMeasurements(context: Context)
    fun getDateNormalMeasurements(context: Context)
    fun getDateEndMeasurements(context: Context)
    fun getDateRejectMeasurements(context: Context)
}