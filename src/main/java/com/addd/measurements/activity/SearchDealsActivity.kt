package com.addd.measurements.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.addd.measurements.*
import com.addd.measurements.adapters.DealAdapter
import com.addd.measurements.modelAPI.Deal
import com.addd.measurements.network.NetworkControllerSearchDeals
import kotlinx.android.synthetic.main.activity_search_deals.*
import java.util.*

class SearchDealsActivity : AppCompatActivity(),NetworkControllerSearchDeals.PaginationCallback, NetworkControllerSearchDeals.CallbackListDeals, DealAdapter.CustomAdapterCallback {
    private lateinit var adapter: DealAdapter
    private lateinit var recyclerList: RecyclerView
    private lateinit var search: String
    lateinit var deals: List<Deal>
    var emptyList: ArrayList<Deal> = ArrayList(emptyList())
    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1
    private var TOTAL_PAGES = 4

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_deals)
        toolbarAst.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbarAst.setNavigationOnClickListener {
            finish()
        }
        NetworkControllerSearchDeals.registerCallBack(this)
        NetworkControllerSearchDeals.registerPaginationCallback(this)
        recyclerList = recyclerListDeals
        search = if (intent.hasExtra(IS_IT_SEARCH)) {
            intent.getStringExtra(IS_IT_SEARCH)
        } else {
            "0"
        }
        NetworkControllerSearchDeals.search(search)
    }

    override fun onItemClick(pos: Int) {
        val intent = Intent(applicationContext, OneDealActivity::class.java)
        intent.putExtra(DEAL_ID, deals[pos].id.toString())
        startActivityForResult(intent, 0)
    }

    override fun resultList(listDeals: List<Deal>, result: Int, count: Int) {
        TOTAL_PAGES = if (count % 20 == 0) {
            count / 20
        } else {
            (count / 20) + 1
        }

        deals = listDeals
        if (listDeals.isEmpty()) {
            if (result == 1) {
                toast(R.string.check_internet)
            } else {
                toast(R.string.nothing_show)
            }
        } else {
            if (result == 0) {
//                Toast.makeText(context, "Данные загружены из сети", Toast.LENGTH_SHORT).show()
            } else {
                supportActionBar?.title = getString(R.string.without_internet)
                toast(R.string.check_internet)
            }
        }
        adapter = if (deals.isEmpty()) {
            DealAdapter(emptyList, this)
        } else {
            DealAdapter(deals as ArrayList, this)
        }

        recyclerList.adapter = adapter
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerList.layoutManager = layoutManager
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
        NetworkControllerSearchDeals.pagination(currentPage)
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
