package com.addd.measurements.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import com.addd.measurements.*
import com.addd.measurements.activity.OneMeasurementActivity
import com.addd.measurements.adapters.DataAdapter
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.network.NetworkController
import kotlinx.android.synthetic.main.measurements_fragment.*
import kotlinx.android.synthetic.main.measurements_fragment.view.*
import java.util.Calendar
import kotlin.collections.ArrayList


/**
 * Created by addd on 03.12.2017.
 */

class MeasurementsFragment : Fragment(), NetworkController.CallbackListMeasurements,
        DataAdapter.CustomAdapterCallback, NetworkController.ResponsibleCallback,
        NetworkController.PaginationCallback {
    private lateinit var date: String
    var emptyList: ArrayList<Measurement> = ArrayList(emptyList())
    lateinit var fragmentListMeasurements: List<Measurement>
    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1
    private var TOTAL_PAGES = 4
    private lateinit var bundle: Bundle
    private lateinit var adapter: DataAdapter
    private var daySave = -1
    private var monthSave = -1
    private var yearSave = -1

    private lateinit var fabOpen: Animation
    private lateinit var fabOpen08: Animation
    private lateinit var fabClose: Animation
    private var isFabOpen = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        NetworkController.registerCallBack(this)
        NetworkController.registerResponsibleCallback(this)
        NetworkController.registerPaginationCallback(this)
        val view: View = inflater.inflate(R.layout.measurements_fragment, container, false)
                ?: View(context)
        fabOpen = AnimationUtils.loadAnimation(context, R.anim.fab_open)
        fabOpen08 = AnimationUtils.loadAnimation(context, R.anim.fab_open_08)
        fabClose = AnimationUtils.loadAnimation(context, R.anim.fab_close)

        view.fabMain.setOnClickListener { showFubs() }
        view.fabMainClose.setOnClickListener { hideFub() }
        view.fabToday.setOnClickListener { todayFab() }
        view.fabTomorrow.setOnClickListener { tomorrowFab() }
        view.fabDate.setOnClickListener { dateFab() }
        view.recyclerList.setOnTouchListener { _, _ ->
            hideFub()
            false
        }

        selectColorVersion(view.buttonAll, R.color.colorPrimaryDark)

        view.buttonAll.setOnClickListener {
            allMeasurements()
        }
        view.buttonFree.setOnClickListener {
            freeMeasurements()
        }
        view.buttonMy.setOnClickListener {
            myMeasurements()
        }

        bundle = this.arguments
        date = getTodayDate()
        adapter = DataAdapter(emptyList, this)
        view.recyclerList.adapter = adapter
        view.progressBarMain.visibility = View.VISIBLE
        bundle?.let {
            when (bundle.getInt(CHECK)) {
                STATUS_CURRENT -> NetworkController.getCurrentMeasurements(date, APP_LIST_TODAY_CURRENT)
                STATUS_REJECT -> NetworkController.getRejectMeasurements(date, APP_LIST_TODAY_REJECTED)
                STATUS_CLOSE -> NetworkController.getCloseMeasurements(date, APP_LIST_TODAY_CLOSED)
            }
        }

        return view
    }

    private fun hideFub() {
        if (isFabOpen) {
            fabToday.startAnimation(fabClose)
            fabMainClose.startAnimation(fabClose)
            fabTomorrow.startAnimation(fabClose)
            fabDate.startAnimation(fabClose)
            fabMain.startAnimation(fabOpen)
            fabToday.isClickable = false
            fabMainClose.isClickable = false
            fabTomorrow.isClickable = false
            fabDate.isClickable = false
            fabMain.isClickable = true
            isFabOpen = false
        }
    }

    private fun showFubs() {
        fabMain.isClickable = false
        fabMain.startAnimation(fabClose)
        fabMainClose.startAnimation(fabOpen)
        fabToday.startAnimation(fabOpen08)
        fabTomorrow.startAnimation(fabOpen08)
        fabDate.startAnimation(fabOpen08)
        fabToday.isClickable = true
        fabMainClose.isClickable = true
        fabTomorrow.isClickable = true
        fabDate.isClickable = true
        isFabOpen = true

    }

    private fun todayFab() {
        hideFub()
        date = getTodayDate()
        adapter = DataAdapter(emptyList, this)
        recyclerList.adapter = adapter
        progressBarMain.visibility = View.VISIBLE
        currentPage = 1
        isLastPage = false
        when (bundle.get(CHECK)) {
            STATUS_CURRENT -> NetworkController.getCurrentMeasurements(date, APP_LIST_TODAY_CURRENT)
            STATUS_REJECT -> NetworkController.getRejectMeasurements(date, APP_LIST_TODAY_REJECTED)
            STATUS_CLOSE -> NetworkController.getCloseMeasurements(date, APP_LIST_TODAY_CLOSED)
        }
    }

    private fun tomorrowFab() {
        hideFub()
        date = getTomorrowDate()
        adapter = DataAdapter(emptyList, this)
        recyclerList.adapter = adapter
        progressBarMain.visibility = View.VISIBLE
        currentPage = 1
        isLastPage = false
        when (bundle.get(CHECK)) {
            STATUS_CURRENT -> NetworkController.getCurrentMeasurements(date, APP_LIST_TOMORROW_CURRENT)
            STATUS_REJECT -> NetworkController.getRejectMeasurements(date, APP_LIST_TOMORROW_REJECTED)
            STATUS_CLOSE -> NetworkController.getCloseMeasurements(date, APP_LIST_TOMORROW_CLOSED)
        }
    }

    private fun dateFab() {
        hideFub()
        datePick()
    }

    private fun selectColorVersion(item: Button, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            item.setBackgroundColor(context.resources.getColor(color, context.theme))
        } else {
            item.setBackgroundColor(context.resources.getColor(color))
        }
    }

    private fun allMeasurements() {
        hideFub()
        buttonAll.textSize = 16.0F
        buttonFree.textSize = 14.0F
        buttonMy.textSize = 14.0F
        selectColorVersion(buttonAll, R.color.colorPrimaryDark)
        selectColorVersion(buttonFree, R.color.colorPrimary)
        selectColorVersion(buttonMy, R.color.colorPrimary)
        toast("Саня еще не сделал")
    }

    private fun freeMeasurements() {
        hideFub()
        buttonFree.textSize = 16.0F
        buttonAll.textSize = 14.0F
        buttonMy.textSize = 14.0F
        selectColorVersion(buttonFree, R.color.colorPrimaryDark)
        selectColorVersion(buttonAll, R.color.colorPrimary)
        selectColorVersion(buttonMy, R.color.colorPrimary)
        toast("Саня еще не сделал")
    }

    private fun myMeasurements() {
        hideFub()
        buttonMy.textSize = 16.0F
        buttonFree.textSize = 14.0F
        buttonAll.textSize = 14.0F
        selectColorVersion(buttonMy, R.color.colorPrimaryDark)
        selectColorVersion(buttonAll, R.color.colorPrimary)
        selectColorVersion(buttonFree, R.color.colorPrimary)
        toast("Саня еще не сделал")
    }

    override fun onItemClick(pos: Int) {
        hideFub()
        val intent = Intent(context, OneMeasurementActivity::class.java)
        var deal = fragmentListMeasurements[pos].deal
        val json = gson.toJson(fragmentListMeasurements[pos])
        intent.putExtra(MEASUREMENT_KEY, json)
        intent.putExtra(ID_KEY, deal)
        intent.putExtra(SYMBOL_KEY, fragmentListMeasurements[pos].company?.symbol?.length.toString())
        startActivityForResult(intent, 0)
    }

    override fun onItemLongClick(pos: Int) {
        hideFub()
        val ad = android.app.AlertDialog.Builder(context)
        ad.setTitle(R.string.become_response)  // заголовок
        var id = fragmentListMeasurements[pos].id
        ad.setPositiveButton(R.string.yes) { _, _ ->
            if (id != null) {
                NetworkController.becomeResponsible(id)
            }
        }
        ad.setNegativeButton(R.string.cancel) { _, _ -> }

        ad.setCancelable(true)
        ad.show()
    }


    private fun datePick() {
        val bundle = this.arguments
        val myCallBack = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            daySave = dayOfMonth
            monthSave = monthOfYear
            yearSave = year
            currentPage = 1
            isLastPage = false
            date = String.format("$year-%02d-%02d", monthOfYear + 1, dayOfMonth)
            adapter = DataAdapter(emptyList, this)
            recyclerList.adapter = adapter
            progressBarMain.visibility = View.VISIBLE
            when (bundle.get(CHECK)) {
                STATUS_CURRENT -> NetworkController.getCurrentMeasurements(date, null)
                STATUS_REJECT -> NetworkController.getRejectMeasurements(date, null)
                STATUS_CLOSE -> NetworkController.getCloseMeasurements(date, null)
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

    private fun updateList() {
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


    @SuppressLint("SetTextI18n")
    override fun resultList(listMeasurements: List<Measurement>, result: Int, date: String, allMeasurements: Int?, myMeasurements: Int?, notDistributed: Int?, count: Int) {
        TOTAL_PAGES = if (count % 20 == 0) {
            count / 20
        } else {
            (count / 20) + 1
        }
        fragmentListMeasurements = listMeasurements

        val toolbar = (activity as AppCompatActivity).supportActionBar
        if (this.arguments.getInt(CHECK) == STATUS_CURRENT) {
            buttonAll.text = "$allMeasurements\nВсе"
            buttonFree.text = "$notDistributed\nСвободные"
            buttonMy.text = "$myMeasurements\nМои"
            toolbar?.title = "${formatDate(date)}"
        }
        if (this.arguments.getInt(CHECK) == STATUS_REJECT) {
            toolbar?.title = getString(R.string.rejected)
        }
        if (this.arguments.getInt(CHECK) == STATUS_CLOSE) {
            toolbar?.title = getString(R.string.closed)
        }

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
                if (this.arguments.getInt(CHECK) == STATUS_CURRENT) toolbar?.title = getString(R.string.without_internet)
                toast(R.string.no_internet)
            }
        }
        adapter = if (listMeasurements.isEmpty()) {
            DataAdapter(this.emptyList, this)
        } else {
            DataAdapter(listMeasurements as ArrayList, this)
        }
        recyclerList.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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

        progressBarMain.visibility = View.GONE
    }

    private fun addFooter() {
        if (currentPage < TOTAL_PAGES) {
            adapter.addLoadingFooter()
        } else {
            isLastPage = true
        }
    }

    override fun resultResponsible(result: Boolean) {
        if (result) {
            updateList()
        }
    }

    override fun onResume() {
        NetworkController.registerCallBack(this)
        NetworkController.registerResponsibleCallback(this)
        NetworkController.registerPaginationCallback(this)
        super.onResume()
    }

    override fun onDestroy() {
        NetworkController.registerCallBack(null)
        NetworkController.registerPaginationCallback(null)
        NetworkController.registerResponsibleCallback(null)
        super.onDestroy()
    }

    private fun loadNextPage() {
        NetworkController.pagination(currentPage)
    }

    override fun resultPaginationClose(listMeasurements: List<Measurement>, result: Int) {
        if (!adapter.isEmpty()) {
            adapter.removeLoadingFooter()
            isLoading = false

            adapter.addAll(listMeasurements)

            if (currentPage != TOTAL_PAGES) {
                adapter.addLoadingFooter()
            } else {
            }
            isLastPage = true
        }
        isLoading = false
    }

}
