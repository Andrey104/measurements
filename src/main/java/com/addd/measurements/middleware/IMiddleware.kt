package com.addd.measurements.middleware

import android.content.Context
import com.addd.measurements.modelAPI.Measurement

/**
 * Created by addd on 11.12.2017.
 */
interface IMiddleware {
    fun getTodayCurrentMeasurements()
    fun getTodayClosedMeasurements()
    fun getTodayRejectMeasurements()

    fun getTomorrowCurrentMeasurements()
    fun getTomorrowClosedMeasurements()
    fun getTomorrowRejectMeasurements()

    fun getDateCurrentMeasurements(date : String)
    fun getDateClosedMeasurements(date : String)
    fun getDateRejectMeasurements(date : String)
}