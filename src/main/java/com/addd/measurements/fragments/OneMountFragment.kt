package com.addd.measurements.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.addd.measurements.R
import com.addd.measurements.adapters.StageAdapter
import com.addd.measurements.toast

/**
 * Created by addd on 11.01.2018.
 */
class OneMountFragment : Fragment(), StageAdapter.CustomAdapterCallback{
    private lateinit var mView : View
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.mount_fragment, container, false) ?: View(context)
        mView = view

        return view
    }



    override fun onItemClick(pos: Int) {
        toast(pos)
    }


}