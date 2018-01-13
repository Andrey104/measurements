package com.addd.measurements.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.addd.measurements.R

/**
 * Created by addd on 13.01.2018.
 */
class EmptyFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        return inflater?.inflate(R.layout.empty_fragment, container, false) ?: View(context)
    }
}