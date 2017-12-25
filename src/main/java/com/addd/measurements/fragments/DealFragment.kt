package com.addd.measurements.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.addd.measurements.*
import com.addd.measurements.adapters.DealAdapter
import com.addd.measurements.modelAPI.Deal


/**
 * Created by addd on 03.12.2017.
 */
class DealFragment : Fragment() {
    private lateinit var adapter: DealAdapter
    lateinit var problems: List<Deal>
    var emptyList: ArrayList<Deal> = ArrayList(emptyList())
    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1
    private var TOTAL_PAGES = 4
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val bundle = this.arguments
        bundle?.let {
            when (bundle.getInt(CHECK)) {
                STATUS_CURRENT -> Toast.makeText(context,"Текущие",Toast.LENGTH_SHORT).show()
                STATUS_REJECT -> Toast.makeText(context,"Отклоненные",Toast.LENGTH_SHORT).show()
                STATUS_CLOSE -> Toast.makeText(context,"ЗАкрытые",Toast.LENGTH_SHORT).show()
            }
        }
        return inflater?.inflate(R.layout.myobjects_fragment, container, false) ?: View(context)
    }

}