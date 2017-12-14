package com.addd.measurements.changemenu

import com.addd.measurements.modelAPI.Measurement

/**
 * Created by addd on 14.12.2017.
 */
class ChangeManager {
    private var listener: OnChangeListener? = null

    private object Holder { val INSTANCE = ChangeManager() }
    companion object {
        val instance: ChangeManager by lazy { Holder.INSTANCE }
    }

    fun setListener(listener: OnChangeListener) {
        this.listener = listener
    }

    fun notifyOnChange(date: String, list: List<Measurement>) {
        if (listener != null) {
            listener!!.onChange(date, list)
        }
    }
}