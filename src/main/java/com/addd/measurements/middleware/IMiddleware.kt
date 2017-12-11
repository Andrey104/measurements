package com.addd.measurements.middleware

import android.content.Context
import com.addd.measurements.modelAPI.Measurement

/**
 * Created by addd on 11.12.2017.
 */
interface IMiddleware {
    fun getTodayNormalMeasurements(context: Context)
    fun getTodayEndMeasurements()
    fun getTodayRejectMeasurements()
    fun getTomorrowNormalMeasurements()
    fun getTomorrowEndMeasurements()
    fun getTomorrowRejectMeasurements()
    fun getDateNormalMeasurements()
    fun getDateEndMeasurements()
    fun getDateRejectMeasurements()
}