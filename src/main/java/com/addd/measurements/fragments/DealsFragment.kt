package com.addd.measurements.fragments


import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.addd.measurements.*
import com.addd.measurements.adapters.DealAdapter
import com.addd.measurements.modelAPI.Deal


/**
 * Created by addd on 03.12.2017.
 */
class DealsFragment : Fragment() {
    private lateinit var adapter: DealAdapter
    private var status = 0
    lateinit var problems: List<Deal>
    var emptyList: ArrayList<Deal> = ArrayList(emptyList())
    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1
    private var TOTAL_PAGES = 4
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater?.inflate(R.layout.my_deals_fragment, container, false) ?: View(context)
        val bundle = this.arguments
        val toolbar = (activity as AppCompatActivity).supportActionBar
        bundle?.let {
            status = when (bundle.getInt(CHECK)) {
                STATUS_CURRENT -> STATUS_CURRENT
                STATUS_REJECT -> STATUS_REJECT
                STATUS_CLOSE -> STATUS_CLOSE
                else -> STATUS_CURRENT
            }
        }
        toolbar?.title = when(status) {
            STATUS_CURRENT -> getString(R.string.current)
            STATUS_REJECT -> getString(R.string.rejected)
            STATUS_CLOSE -> getString(R.string.closed)
            else -> getString(R.string.my_objects)
        }

        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottomNavigationDeals)
//        onClickMenu(bottomNavigationView)
        return view
    }

//    private fun onClickMenu(bottomNavigationView: BottomNavigationView) {
//        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//            val bundle = this.arguments
//            when (item.itemId) {
//                R.id.allDeals -> {
//                    adapter = DealAdapter(emptyList, this)
//                    recyclerList.adapter = adapter
//                    progressBarMain.visibility = View.VISIBLE
//                    currentPage = 1
//                    isLastPage = false
//                    when (bundle.get(CHECK)) {
//                        STATUS_CURRENT -> NetworkController.getCurrentMeasurements(date, APP_LIST_TODAY_CURRENT)
//                        STATUS_REJECT -> NetworkController.getRejectMeasurements(date, APP_LIST_TODAY_REJECTED)
//                        STATUS_CLOSE -> NetworkController.getCloseMeasurements(date, APP_LIST_TODAY_CLOSED)
//                    }
//                }
//                R.id.calendarDeals -> {
//                    date = getTomorrowDate()
//                    adapter = DataAdapter(emptyList, this)
//                    recyclerList.adapter = adapter
//                    progressBarMain.visibility = View.VISIBLE
//                    currentPage = 1
//                    isLastPage = false
//                    when (bundle.get(CHECK)) {
//                        STATUS_CURRENT -> NetworkController.getCurrentMeasurements(date, APP_LIST_TOMORROW_CURRENT)
//                        STATUS_REJECT -> NetworkController.getRejectMeasurements(date, APP_LIST_TOMORROW_REJECTED)
//                        STATUS_CLOSE -> NetworkController.getCloseMeasurements(date, APP_LIST_TOMORROW_CLOSED)
//                    }
//                }
//                R.id.date -> {
//                    datePick()
//
//                }
//            }
//            true
//        }
//    }

}