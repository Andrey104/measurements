package com.addd.measurements.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.addd.measurements.PaginationScrollListener
import com.addd.measurements.R
import com.addd.measurements.adapters.ProblemAdapter
import com.addd.measurements.modelAPI.MyProblem
import com.addd.measurements.network.NetworkController
import com.addd.measurements.network.NetworkControllerProblem
import com.addd.measurements.toast
import kotlinx.android.synthetic.main.problems_fragment.*
import kotlinx.android.synthetic.main.problems_fragment.view.*

/**
 * Created by addd on 03.12.2017.
 */
class ProblemsFragment : Fragment(), NetworkControllerProblem.ProblemListCallback, ProblemAdapter.CustomAdapterCallback, NetworkControllerProblem.ProblemPaginationList {
    private lateinit var adapter: ProblemAdapter
    lateinit var problems: List<MyProblem>
    var emptyList: ArrayList<MyProblem> = ArrayList(emptyList())
    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1
    private var TOTAL_PAGES = 4

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        NetworkControllerProblem.registerProblemListCallback(this)
        NetworkControllerProblem.registerProblemPagination(this)
        val view = inflater?.inflate(R.layout.problems_fragment, container, false) ?: View(context)
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.hide()
        view.progressBarMain.visibility = View.VISIBLE
        NetworkControllerProblem.getProblems(1)
        return view
    }

    override fun onItemClick(pos: Int) {
        toast(problems[pos].deal.toString())
    }

    override fun resultProblemList(listProblems: List<MyProblem>, result: Boolean, count: Int) {
        problems = listProblems
        TOTAL_PAGES = if (count % 20 == 0) {
            count / 20
        } else {
            (count / 20) + 1
        }
        if (listProblems.isEmpty()) {
            if (!result) {
                Toast.makeText(context, getString(R.string.nothing_show), Toast.LENGTH_SHORT).show()
            }
        } else {
            if (result) {
//                Toast.makeText(context, "Данные загружены из сети", Toast.LENGTH_SHORT).show()
            } else {
                toast(R.string.no_internet)
            }
        }
        adapter = ProblemAdapter(listProblems as ArrayList, this)
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

        progressBarMain.visibility = View.GONE
    }

    private fun addFooter() {
        if (currentPage < TOTAL_PAGES) {
            adapter.addLoadingFooter()
        } else {
            isLastPage = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 200) {
            updateList()
        }
    }

    private fun loadNextPage() {
        NetworkControllerProblem.paginationProblem(currentPage)
    }

    fun updateList() {
        currentPage = 1
        isLastPage = false
        adapter = ProblemAdapter(emptyList, this)
        recyclerList.adapter = adapter
        progressBarMain.visibility = View.VISIBLE
        NetworkController.updateListInFragment()
    }
    override fun problemPaginationResult(list: List<MyProblem>, result: Boolean) {
        if (!adapter.isEmpty()) {
            adapter.removeLoadingFooter()
            isLoading = false

            adapter.addAll(list)

            if (currentPage != TOTAL_PAGES)
                adapter.addLoadingFooter()
            else
                isLastPage = true
        }
        isLoading = false
    }
}