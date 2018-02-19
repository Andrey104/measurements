package ru.nextf.measurements.fragments


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import ru.nextf.measurements.activity.OneDealActivity
import ru.nextf.measurements.*
import ru.nextf.measurements.adapters.DealAdapter
import ru.nextf.measurements.modelAPI.Deal
import ru.nextf.measurements.network.NetworkControllerDeals
import kotlinx.android.synthetic.main.my_deals_fragment.*
import kotlinx.android.synthetic.main.my_deals_fragment.view.*
import java.util.*


/**
 * Created by addd on 03.12.2017.
 */
class DealsFragment : Fragment(),
        NetworkControllerDeals.PaginationCallback, NetworkControllerDeals.CallbackListDeals,
        DealAdapter.CustomAdapterCallback {

    private lateinit var adapter: DealAdapter
    private lateinit var recyclerList: RecyclerView
    private lateinit var refresher: SwipeRefreshLayout
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
    private var daySave = -1
    private var monthSave = -1
    private var yearSave = -1
    private var isFabOpen = false
    private lateinit var fabOpen: Animation
    private lateinit var fabOpen08: Animation
    private lateinit var fabClose: Animation
    private lateinit var textOpen: Animation
    private lateinit var textClose: Animation

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (activity as AppCompatActivity).supportActionBar?.show()
        NetworkControllerDeals.registerCallBack(this)
        NetworkControllerDeals.registerPaginationCallback(this)
        val view = inflater?.inflate(ru.nextf.measurements.R.layout.my_deals_fragment, container, false)
                ?: View(context)
        progressBarMainDeal = view.findViewById(ru.nextf.measurements.R.id.progressBarMainDeal)
        recyclerList = view.findViewById(ru.nextf.measurements.R.id.recyclerListDeals)
        bundle = this.arguments
        recyclerList.setOnTouchListener { v, event ->
            hideFub()
            false
        }
        val toolbar = (activity as AppCompatActivity).supportActionBar
        bundle?.let {
            when (bundle.getInt(CHECK)) {
                STATUS_CURRENT -> {
                    status = STATUS_CURRENT
                    toolbar?.title = getString(ru.nextf.measurements.R.string.current)
                    NetworkControllerDeals.getAllCurrentDeals()
                }
                STATUS_REJECT -> {
                    status = STATUS_REJECT
                    toolbar?.title = getString(ru.nextf.measurements.R.string.rejected)
                    NetworkControllerDeals.getAllRejectedDeals()
                }
                STATUS_CLOSE -> {
                    status = STATUS_CLOSE
                    toolbar?.title = getString(ru.nextf.measurements.R.string.closed)
                    NetworkControllerDeals.getAllClosedDeals()
                }
                else -> {
                    status = STATUS_CURRENT
                    toolbar?.title = getString(ru.nextf.measurements.R.string.current)
                    NetworkControllerDeals.getAllCurrentDeals()
                }
            }
        }
        fabOpen = AnimationUtils.loadAnimation(context, ru.nextf.measurements.R.anim.fab_open)
        fabOpen08 = AnimationUtils.loadAnimation(context, ru.nextf.measurements.R.anim.fab_open_08)
        fabClose = AnimationUtils.loadAnimation(context, ru.nextf.measurements.R.anim.fab_close)
        textOpen = AnimationUtils.loadAnimation(context, ru.nextf.measurements.R.anim.text_open)
        textClose = AnimationUtils.loadAnimation(context, ru.nextf.measurements.R.anim.text_close)

        view.fabMain.setOnClickListener {
            showFubs()
        }

        view.fabMainClose.setOnClickListener {
            hideFub()
        }

        view.fabAll.setOnClickListener {
            hideFub()
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

        view.fabDate.setOnClickListener {
            hideFub()
            datePick()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.refresh.setColorSchemeColors(resources.getColor(R.color.colorAccent, context.theme),
                    resources.getColor(R.color.colorPrimary, context.theme))
        } else {
            view.refresh.setColorSchemeColors(resources.getColor(R.color.colorAccent),
                    resources.getColor(R.color.colorPrimary))
        }

        view.refresh.setOnRefreshListener {
            view.refresh.isRefreshing = true
            updateList()
        }
        refresher = view.refresh

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 200) {
            updateList()
        }
    }

    private fun updateList() {
        currentPage = 1
        isLastPage = false
        adapter = DealAdapter(emptyList, this)
        recyclerList.adapter = adapter
        progressBarMainDeal.visibility = View.VISIBLE
        NetworkControllerDeals.updateListInFragment()
    }


    private fun hideFub() {
        if (isFabOpen) {
            fabAll.startAnimation(fabClose)
            fabMainClose.startAnimation(fabClose)
            fabMain.startAnimation(fabOpen)
            fabDate.startAnimation(fabClose)
            textViewAll.startAnimation(textClose)
            textViewDate.startAnimation(textClose)
            fabAll.isClickable = false
            fabMain.isClickable = true
            fabMainClose.isClickable = false
            fabDate.isClickable = false
            isFabOpen = false
        }
    }

    private fun showFubs() {
        fabMain.isClickable = false
        fabMain.startAnimation(fabClose)
        fabMainClose.startAnimation(fabOpen)
        fabAll.startAnimation(fabOpen08)
        fabDate.startAnimation(fabOpen08)
        textViewAll.startAnimation(textOpen)
        textViewDate.startAnimation(textOpen)
        fabAll.isClickable = true
        fabMainClose.isClickable = true
        fabDate.isClickable = true
        fabMain.isClickable = true
        isFabOpen = true

    }

    override fun onItemClick(pos: Int) {
        val intent = Intent(context, OneDealActivity::class.java)
        intent.putExtra(DEAL_ID, deals[pos].id.toString())
        startActivityForResult(intent, 0)
    }


    private fun datePick() {
        val myCallBack = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            daySave = dayOfMonth
            monthSave = monthOfYear
            yearSave = year
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
        val datePickerDialog = if (yearSave == -1) {
            DatePickerDialog(activity, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        } else {
            DatePickerDialog(activity, myCallBack, yearSave, monthSave, daySave)
        }
        datePickerDialog.show()
    }

    override fun resultList(listDeals: List<Deal>, result: Int, date: String, count: Int) {
        refresher.isRefreshing = false
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
                    if (deals.isEmpty()) toast(ru.nextf.measurements.R.string.nothing_to_show)
//                Toast.makeText(context, "Данные загружены из сети", Toast.LENGTH_SHORT).show()
                } else {
                    if (this.arguments.getInt(CHECK) == STATUS_CURRENT) toolbar?.title = getString(ru.nextf.measurements.R.string.without_internet)
                    toast(ru.nextf.measurements.R.string.nothing_to_show)
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
        recyclerList.addOnScrollListener(object : ru.nextf.measurements.PaginationScrollListener(recyclerList.layoutManager as LinearLayoutManager) {
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

    override fun onDestroy() {
        NetworkControllerDeals.registerCallBack(null)
        NetworkControllerDeals.registerPaginationCallback(null)
        super.onDestroy()
    }

    override fun resultPagination(listDeals: List<Deal>, result: Int) {
        if (!adapter.isEmpty()) {
            adapter.removeLoadingFooter()
            isLoading = false

            if (!listDeals.isEmpty()) {
                adapter.addAll(listDeals)
            } else {
                currentPage -= 1
            }

            if (currentPage != TOTAL_PAGES) {
                adapter.addLoadingFooter()
            } else {
                isLastPage = true
            }
        }
        isLoading = false
    }

}