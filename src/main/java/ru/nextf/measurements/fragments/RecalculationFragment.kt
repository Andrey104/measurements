package ru.nextf.measurements.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.nextf.measurements.activity.AddRecalculationActivity
import ru.nextf.measurements.adapters.RecalculationAdapter
import ru.nextf.measurements.*
import ru.nextf.measurements.modelAPI.Deal
import ru.nextf.measurements.modelAPI.Recalculation
import ru.nextf.measurements.network.NetworkControllerDeals
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.recalculation_fragment.view.*

/**
 * Created by addd on 10.01.2018.
 */
class RecalculationFragment : Fragment(), NetworkControllerDeals.OneDealCallbackRecalc {
    private lateinit var mView: View
    private lateinit var bundle: Bundle
    private lateinit var deal: Deal
    private lateinit var mFragmentManager: FragmentManager
    var emptyList: ArrayList<Recalculation> = ArrayList(emptyList())
    private lateinit var recalculations: List<Recalculation>


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        NetworkControllerDeals.registerOneDealRecalcCallback(this)
        bundle = this.arguments
        mFragmentManager = (activity as AppCompatActivity).supportFragmentManager
        val view = inflater?.inflate(ru.nextf.measurements.R.layout.recalculation_fragment, container, false)
                ?: View(context)
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
        if (recalculations.isEmpty()) toast(ru.nextf.measurements.R.string.no_recalculation_in_deal)
        val adapter = RecalculationAdapter(recalculations)
        mView.recyclerListRecalculation.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mView.recyclerListRecalculation.layoutManager = layoutManager
    }

    private fun addRecalculation() {
        val intent = Intent(context, AddRecalculationActivity::class.java)
        intent.putExtra(DEAL_ID, deal.id.toString())
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 200) {
            val fragment = LoadFragment()
            mFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.containerDeal, fragment).commit()
            NetworkControllerDeals.getOneDeal(deal.id.toString())
        }
    }

    override fun resultOneDealRecalc(deal: Deal?, boolean: Boolean) {
        if (deal != null && boolean) {
            val fragment = RecalculationFragment()
            val json = gson.toJson(deal)
            val bundle = Bundle()
            bundle.putString(DEAL_KEY, json)
            fragment.arguments = bundle
            mFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.containerDeal, fragment).commit()
        } else {
            toast(ru.nextf.measurements.R.string.error)
        }
    }

    override fun onDestroy() {
        NetworkControllerDeals.registerOneDealRecalcCallback(null)
        super.onDestroy()
    }
}