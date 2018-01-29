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
import com.addd.measurements.*
import com.addd.measurements.activity.OneProblemActivity
import com.addd.measurements.activity.ProblemActivity
import com.addd.measurements.adapters.ProblemAdapter
import com.addd.measurements.modelAPI.Deal
import com.addd.measurements.modelAPI.MyProblem
import com.addd.measurements.network.NetworkControllerProblem
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.problems_deal_fragment.view.*

/**
 * Created by addd on 08.01.2018.
 */
class ProblemDealFragment : Fragment(), ProblemAdapter.CustomAdapterCallback, NetworkControllerProblem.DealProblems {
    private lateinit var bundle: Bundle
    private lateinit var dealID: String
    private lateinit var adapter: ProblemAdapter
    private lateinit var problems: List<MyProblem>
    var emptyList: ArrayList<MyProblem> = ArrayList(emptyList())
    private lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        NetworkControllerProblem.registerDealProblemsCallback(this)
        val view = inflater?.inflate(R.layout.problems_deal_fragment, container, false) ?: View(context)
        mView = view
        mView.progressBarMain.visibility = View.VISIBLE
        bundle = this.arguments
        bundle.let {
            if (bundle.containsKey(DEAL_ID)) {
                dealID = bundle.getString(DEAL_ID)
            }
        }
        (activity as AppCompatActivity).supportActionBar?.title = String.format("Проблемы %05d", dealID.toInt())
        mView.fab2.setOnClickListener {
            val intent = Intent(context, ProblemActivity::class.java)
            intent.putExtra(DEAL_KEY, dealID)
            startActivityForResult(intent, 30)
        }
        NetworkControllerProblem.getDealProblem(dealID)
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 200) {
            adapter = ProblemAdapter(emptyList, this)
            mView.recyclerList.adapter = adapter
            mView.progressBarMain.visibility = View.VISIBLE
            NetworkControllerProblem.getDealProblem(dealID)
        }
    }


    override fun onItemClick(pos: Int) {
        val intent = Intent(context, OneProblemActivity::class.java)
        val json = gson.toJson(problems[pos])
        intent.putExtra(PROBLEM_KEY, json)

        startActivityForResult(intent, 0)
    }

    override fun resultGetProblems(listProblems: List<MyProblem>?, boolean: Boolean) {
        if (listProblems != null) {
            if (listProblems.isEmpty() && boolean) {
                toast(R.string.no_problem)
                mView.fab2.visibility = View.VISIBLE
                adapter = ProblemAdapter(emptyList, this)
            } else if (listProblems.isEmpty() && !boolean) {
                toast(R.string.error)
                mView.fab2.visibility = View.GONE
                adapter = ProblemAdapter(emptyList, this)
            } else {
                mView.fab2.visibility = View.VISIBLE
                problems = listProblems as ArrayList
                adapter = ProblemAdapter(problems as ArrayList, this)
                mView.recyclerList.adapter = adapter
                val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                mView.recyclerList.layoutManager = layoutManager
                val dividerItemDecoration = DividerItemDecoration(mView.recyclerList.context, layoutManager.orientation)
                mView.recyclerList.addItemDecoration(dividerItemDecoration)
            }
        } else {
            mView.fab2.visibility = View.GONE
            toast(R.string.error)
        }
        mView.progressBarMain.visibility = View.GONE
    }

    override fun onDestroy() {
        NetworkControllerProblem.registerDealProblemsCallback(null)
        super.onDestroy()
    }
}
