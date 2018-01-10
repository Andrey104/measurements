package com.addd.measurements.fragments


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.addd.measurements.*
import com.addd.measurements.activity.OneDealActivity
import com.addd.measurements.adapters.DealAdapter
import com.addd.measurements.modelAPI.Deal
import com.addd.measurements.network.NetworkControllerDeals
import java.util.*


/**
 * Created by addd on 03.12.2017.
 */
class DealsFragment : Fragment(),
        NetworkControllerDeals.PaginationCallback, NetworkControllerDeals.CallbackListDeals,
        DealAdapter.CustomAdapterCallback {

    private lateinit var adapter: DealAdapter
    private lateinit var recyclerList: RecyclerView
    private lateinit var progressBarMainDeal: ProgressBar
    private lateinit var date: String
    private lateinit var bundle: Bundle
    private var status = 0
    lateinit var deals: List<Deal>
    var emptyList: ArrayList<Deal> = ArrayList(emptyList())
    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1
    private var TOTAL_PAGES = 4


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (activity as AppCompatActivity).supportActionBar?.show()
        NetworkControllerDeals.registerCallBack(this)
        NetworkControllerDeals.registerPaginationCallback(this)
        val view = inflater?.inflate(R.layout.my_deals_fragment, container, false) ?: View(context)
        progressBarMainDeal = view.findViewById(R.id.progressBarMainDeal)
        recyclerList = view.findViewById(R.id.recyclerListDeals)
        bundle = this.arguments
        val toolbar = (activity as AppCompatActivity).supportActionBar
        bundle?.let {
            when (bundle.getInt(CHECK)) {
                STATUS_CURRENT -> {
                    status = STATUS_CURRENT
                    toolbar?.title = getString(R.string.current)
                    NetworkControllerDeals.getAllCurrentDeals()
                }
                STATUS_REJECT -> {
                    status = STATUS_REJECT
                    toolbar?.title = getString(R.string.rejected)
                    NetworkControllerDeals.getAllRejectedDeals()
                }
                STATUS_CLOSE -> {
                    status = STATUS_CLOSE
                    toolbar?.title = getString(R.string.closed)
                    NetworkControllerDeals.getAllClosedDeals()
                }
                else -> {
                    status = STATUS_CURRENT
                    toolbar?.title = getString(R.string.current)
                    NetworkControllerDeals.getAllCurrentDeals()
                }
            }
        }

        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottomNavigationDeals)
        onClickMenu(bottomNavigationView)
        return view
    }

    override fun onItemClick(pos: Int) {
        val intent = Intent(context, OneDealActivity::class.java)
        intent.putExtra(DEAL_ID, deals[pos].id.toString())
        startActivityForResult(intent, 0)
    }


    private fun onClickMenu(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.allDeals -> {
                    adapter = DealAdapter(emptyList, this)
                    recyclerList.adapter = adapter
                    progressBarMainDeal.visibility = View.VISIBLE
                    currentPage = 1
                    isLastPage = false
                    when (bundle.get(CHECK)) {
                        STATUS_CURRENT -> NetworkControllerDeals.getAllCurrentDeals()
                        STATUS_REJECT -> NetworkControllerDeals.getAllRejectedDeals()
                        STATUS_CLOSE -> NetworkControllerDeals.getAllClosedDeals()
                    }
                }
                R.id.calendarDeals -> {
                    datePick()
                }
            }
            true
        }
    }

    private fun datePick() {
        val myCallBack = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            currentPage = 1
            isLastPage = false
            date = String.format("$year-%02d-%02d", monthOfYear + 1, dayOfMonth)
            adapter = DealAdapter(emptyList, this)
            recyclerList.adapter = adapter
            progressBarMainDeal.visibility = View.VISIBLE
            when (status) {
                STATUS_CURRENT -> NetworkControllerDeals.getCurrentDeals(date)
                STATUS_REJECT -> NetworkControllerDeals.getRejectedDeals(date)
                STATUS_CLOSE -> NetworkControllerDeals.getClosedDeals(date)
            }
        }
        val calendar = Calendar.getInstance()
        val datePikerDialog = DatePickerDialog(context, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePikerDialog.show()
    }

    override fun resultList(listDeals: List<Deal>, result: Int, date: String, count: Int) {
        TOTAL_PAGES = if (count % 20 == 0) {
            count / 20
        } else {
            (count / 20) + 1
        }

        deals = listDeals
        if ((activity as AppCompatActivity).supportActionBar != null) {
            val toolbar = (activity as AppCompatActivity).supportActionBar
            if (deals.isEmpty()) {
                if (result == 0) {
                    if (deals.isEmpty()) toast("По данному запросу ничего не найдено")
//                Toast.makeText(context, "Данные загружены из сети", Toast.LENGTH_SHORT).show()
                } else {
                    if (this.arguments.getInt(CHECK) == STATUS_CURRENT) toolbar?.title = getString(R.string.without_internet)
                    toast(R.string.no_internet)
                }
            }
        }
        adapter = if (deals.isEmpty()) {
            DealAdapter(emptyList, this)
        } else {
            DealAdapter(deals as ArrayList, this)
        }

        recyclerList.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerList.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerList.context, layoutManager.orientation)
        recyclerList.addItemDecoration(dividerItemDecoration)
        recyclerList.addOnScrollListener(object : PaginationScrollListener(recyclerList.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1

                loadNextPage()
            }

            override fun getTotalPageCount(): Int {
                return TOTAL_PAGES
            }

        })
        addFooter()

        progressBarMainDeal.visibility = View.GONE
    }


    private fun addFooter() {
        if (currentPage < TOTAL_PAGES) {
            adapter.addLoadingFooter()
        } else {
            isLastPage = true
        }
    }

    private fun loadNextPage() {
        NetworkControllerDeals.pagination(currentPage)
    }

    override fun resultPagination(listDeals: List<Deal>, result: Int) {
        if (!adapter.isEmpty()) {
            adapter.removeLoadingFooter()
            isLoading = false

            adapter.addAll(listDeals)

            if (currentPage != TOTAL_PAGES)
                adapter.addLoadingFooter()
            else
                isLastPage = true
        }
        isLoading = false
    }

}