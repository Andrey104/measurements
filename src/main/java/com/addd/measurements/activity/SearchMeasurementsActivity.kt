package com.addd.measurements.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.addd.measurements.*
import com.addd.measurements.adapters.DataAdapter
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.network.NetworkController
import kotlinx.android.synthetic.main.activity_search_measurements.*

class SearchMeasurementsActivity : AppCompatActivity(), NetworkController.PaginationCallback, NetworkController.CallbackListMeasurements, DataAdapter.CustomAdapterCallback {
    private lateinit var search: String
    var emptyList: ArrayList<Measurement> = ArrayList(emptyList())
    lateinit var fragmentListMeasurements: List<Measurement>
    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1
    private var TOTAL_PAGES = 4
    private lateinit var adapter: DataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerCallBack(this)
        NetworkController.registerPaginationCallback(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_measurements)
        setSupportActionBar(toolbarAst)

        search = if (intent.hasExtra(IS_IT_SEARCH)) {
            intent.getStringExtra(IS_IT_SEARCH)
        } else {
            "0"
        }

        NetworkController.search(search)
    }

    override fun resultList(listMeasurements: List<Measurement>, result: Int, date: String, allMeasurements: Int?, myMeasurements: Int?, notDistributed: Int?, count: Int) {
        TOTAL_PAGES = if (count % 20 == 0) {
            count / 20
        } else {
            (count / 20) + 1
        }
        fragmentListMeasurements = listMeasurements


        if (listMeasurements.isEmpty()) {
            if (result == 1) {
                toast(R.string.no_save_data)
            } else {
                toast(R.string.nothing_show)
            }
        } else {
            if (result == 0) {
//                Toast.makeText(context, "Данные загружены из сети", Toast.LENGTH_SHORT).show()
            } else {
                supportActionBar?.title = getString(R.string.without_internet)
                toast(R.string.no_internet)
            }
        }
        adapter = if (listMeasurements.isEmpty()) {
            DataAdapter(this.emptyList, this)
        } else {
            DataAdapter(listMeasurements as ArrayList, this)
        }
        recyclerList.adapter = adapter
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
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

        progressBarMain.visibility = View.GONE
    }

    private fun addFooter() {
        if (currentPage < TOTAL_PAGES) {
            adapter.addLoadingFooter()
        } else {
            isLastPage = true
        }
    }


    override fun onResume() {
        NetworkController.registerCallBack(this)
        NetworkController.registerPaginationCallback(this)
        super.onResume()
    }

    override fun onDestroy() {
        NetworkController.registerCallBack(null)
        NetworkController.registerPaginationCallback(null)
        super.onDestroy()
    }

    private fun loadNextPage() {
        NetworkController.pagination(currentPage, search)
    }

    override fun resultPaginationClose(listMeasurements: List<Measurement>, result: Int) {
        if (!adapter.isEmpty()) {
            adapter.removeLoadingFooter()
            isLoading = false

            adapter.addAll(listMeasurements)

            if (currentPage != TOTAL_PAGES)
                adapter.addLoadingFooter()
            else
                isLastPage = true
        }
        isLoading = false
    }

    override fun onItemClick(pos: Int) {
        val intent = Intent(applicationContext, OneMeasurementActivity::class.java)
        var deal = fragmentListMeasurements[pos].deal
        val json = gson.toJson(fragmentListMeasurements[pos])
        intent.putExtra(MEASUREMENT_KEY, json)
        intent.putExtra(ID_KEY, deal)
        intent.putExtra(SYMBOL_KEY, fragmentListMeasurements[pos].company?.symbol?.length.toString())
        startActivityForResult(intent, 0)
    }

    override fun onItemLongClick(pos: Int) {

    }
    fun updateList() {
        currentPage = 1
        isLastPage = false
        adapter = DataAdapter(emptyList, this)
        recyclerList.adapter = adapter
        progressBarMain.visibility = View.VISIBLE
        NetworkController.updateListInFragment()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 200) {
            updateList()
        }
    }
}
