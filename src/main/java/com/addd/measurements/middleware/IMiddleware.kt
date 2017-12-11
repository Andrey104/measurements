package com.addd.measurements.middleware

import com.addd.measurements.modelAPI.Measurement

/**
 * Created by addd on 11.12.2017.
 */
interface IMiddleware {
    fun getTodayNormalMeasurements(): List<Measurement>
    fun getTodayEndMeasurements(): List<Measurement>
    fun getTodayRejectMeasurements(): List<Measurement>
    fun getTomorrowNormalMeasurements(): List<Measurement>
    fun getTomorrowEndMeasurements(): List<Measurement>
    fun getTomorrowRejectMeasurements(): List<Measurement>
    fun getDateNormalMeasurements(): List<Measurement>
    fun getDateEndMeasurements(): List<Measurement>
    fun getDateRejectMeasurements(): List<Measurement>
}