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
import com.addd.measurements.activity.OneMeasurementActivity
import com.addd.measurements.adapters.DataAdapter
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.network.NetworkController
import kotlinx.android.synthetic.main.measurements_list_fragment.view.*

/**
 * Created by addd on 08.01.2018.
 */

class ListMeasurementsFragment : Fragment(), DataAdapter.CustomAdapterCallback, NetworkController.MeasurementsDealCallback {
    private lateinit var bundle: Bundle
    private lateinit var dealID: String
    private lateinit var adapter: DataAdapter
    var emptyList: ArrayList<Measurement> = ArrayList(emptyList())
    private lateinit var measurements: List<Measurement>
    private lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater?.inflate(R.layout.measurements_list_fragment, container, false) ?: View(context)
        mView = view
        mView.progressBar4.visibility = View.VISIBLE
        NetworkController.registerMeasurementsDealCallback(this)
        bundle = this.arguments
        bundle.let {
            if (bundle.containsKey(DEAL_ID)) {
                dealID = bundle.getString(DEAL_ID)
            }
        }
        (activity as AppCompatActivity).supportActionBar?.title = String.format("Замеры %05d", dealID.toInt())
        NetworkController.getMeasurementsDeals(dealID)
        return view
    }

    override fun onItemClick(pos: Int) {
        val intent = Intent(context, OneMeasurementActivity::class.java)
        var id = measurements[pos].id
        intent.putExtra(ID_KEY, id)
        intent.putExtra(FROM_DEAL, true)
        intent.putExtra(MEASUREMENT_EXPANDED, true)
        startActivityForResult(intent, 10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 200) {
            adapter = DataAdapter(emptyList, this)
            mView.recyclerListMeasurements.adapter = adapter
            mView.progressBar4.visibility = View.VISIBLE
            NetworkController.getMeasurementsDeals(dealID)
        }
    }

    override fun onItemLongClick(pos: Int) {
    }



    override fun resultMeasurementsDeal(listMeasurements: List<Measurement>, result: Boolean) {
        if (listMeasurements.isEmpty() || !result) {
            toast(R.string.error)
        } else {
            measurements = listMeasurements as ArrayList<Measurement>
            adapter = DataAdapter(measurements as ArrayList<Measurement>, this)
            mView.recyclerListMeasurements.adapter = adapter
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            mView.recyclerListMeasurements.layoutManager = layoutManager
            val dividerItemDecoration = DividerItemDecoration(mView.recyclerListMeasurements.context, layoutManager.orientation)
            mView.recyclerListMeasurements.addItemDecoration(dividerItemDecoration)
        }
            mView.progressBar4.visibility = View.GONE
    }

    override fun onDestroy() {
        NetworkController.registerMeasurementsDealCallback(null)
        super.onDestroy()
    }
}