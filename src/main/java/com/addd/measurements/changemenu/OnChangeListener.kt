package com.addd.measurements.changemenu

import com.addd.measurements.modelAPI.Measurement

/**
 * Created by addd on 14.12.2017.
 */
interface OnChangeListener {
    fun onChange(date : String, list : List<Measurement>)
}