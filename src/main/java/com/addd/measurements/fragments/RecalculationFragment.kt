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
import com.addd.measurements.activity.AddRecalculationActivity
import com.addd.measurements.adapters.RecalculationAdapter
import com.addd.measurements.modelAPI.Deal
import com.addd.measurements.modelAPI.Recalculation
import com.addd.measurements.network.NetworkControllerDeals
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_deal.*
import kotlinx.android.synthetic.main.recalculation_fragment.view.*

/**
 * Created by addd on 10.01.2018.
 */
class RecalculationFragment : Fragment(), NetworkControllerDeals.OneDealCallback {
    private lateinit var mView: View
    private lateinit var bundle: Bundle
    private lateinit var deal: Deal
    var emptyList: ArrayList<Recalculation> = ArrayList(emptyList())
    private lateinit var recalculations: List<Recalculation>


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        NetworkControllerDeals.registerOneDealCallback(this)
        bundle = this.arguments
        val view = inflater?.inflate(R.layout.recalculation_fragment, container, false) ?: View(context)
        mView = view
        deal = getSavedDeal()
        (activity as AppCompatActivity).supportActionBar?.title = String.format("Перерасчеты %05d", deal.id)
        recalculations = deal.discounts ?: emptyList
        displayRecalculations()
        mView.fab3.setOnClickListener { addRecalculation() }
        return view
    }

    private fun getSavedDeal(): Deal {
        val json = bundle?.getString(DEAL_KEY)
        return if (json.isNullOrEmpty()) {
            Deal()
        } else {
            val type = object : TypeToken<Deal>() {
            }.type
            gson.fromJson(json, type)
        }
    }

    private fun displayRecalculations() {
        val adapter = RecalculationAdapter(recalculations)
        mView.recyclerListRecalculation.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mView.recyclerListRecalculation.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(mView.recyclerListRecalculation.context, layoutManager.orientation)
        mView.recyclerListRecalculation.addItemDecoration(dividerItemDecoration)
    }

    private fun addRecalculation() {
        val intent = Intent(context, AddRecalculationActivity::class.java)
        intent.putExtra(DEAL_ID, deal.id)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 200) {
            val fragment = LoadFragment()
            val fragmentManager = (activity as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.containerDeal, fragment).commit()
            NetworkControllerDeals.getOneDeal(deal.id.toString())
        }
    }

    override fun resultOneDeal(deal: Deal?, boolean: Boolean) {
        if (deal != null && boolean) {
            recalculations = deal.discounts ?: emptyList
            displayRecalculations()
        } else {
            toast(R.string.error)
        }
    }
}