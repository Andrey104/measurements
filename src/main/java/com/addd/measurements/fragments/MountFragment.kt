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
import com.addd.measurements.activity.OneMountActivity
import com.addd.measurements.adapters.MountAdapter
import com.addd.measurements.modelAPI.Mount
import com.addd.measurements.network.NetworkControllerDeals
import kotlinx.android.synthetic.main.mount_fragment.view.*

/**
 * Created by addd on 10.01.2018.
 */
class MountFragment : Fragment(), NetworkControllerDeals.MountsDealCallback, MountAdapter.CustomAdapterCallback {
    private lateinit var adapter: MountAdapter
    private lateinit var mView: View
    private lateinit var bundle: Bundle
    private lateinit var idDeal: String
    private lateinit var mounts: List<Mount>
    var emptyList: ArrayList<Mount> = ArrayList(emptyList())

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        NetworkControllerDeals.registerMountsDealCallback(this)
        val view = inflater?.inflate(R.layout.mount_fragment, container, false) ?: View(context)
        mView = view
        bundle = this.arguments
        idDeal = bundle.getString(DEAL_ID)
        bundle = this.arguments
        if (bundle.containsKey(DEAL_ID)) {
            (activity as AppCompatActivity).supportActionBar?.title = String.format("Монтажи %05d", bundle.getString(DEAL_ID).toInt())
            mView.progressBarMount.visibility = View.VISIBLE
            NetworkControllerDeals.getMountsDeal(idDeal)
        } else {
            toast(R.string.error)
        }

        return view
    }

    private fun displayMounts(mounts: List<Mount>) {
        adapter = MountAdapter(mounts, this)
        mView.recyclerListMounts.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mView.recyclerListMounts.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(mView.recyclerListMounts.context, layoutManager.orientation)
        mView.recyclerListMounts.addItemDecoration(dividerItemDecoration)
        mView.progressBarMount.visibility = View.GONE
    }

    override fun onItemClick(pos: Int) {
        val json = gson.toJson(mounts[pos])
        val intent = Intent(context, OneMountActivity::class.java)
        intent.putExtra(MOUNT_NAME, json)
        startActivityForResult(intent, 1)
    }

    override fun resultMountsDeal(listMounts: List<Mount>?, boolean: Boolean) {
        if (listMounts != null && boolean) {
            mounts = listMounts
            displayMounts(listMounts)
        }
        if (listMounts?.isEmpty() != false && boolean) {
            toast(R.string.deal_dont_has_mounts)
            mView.progressBarMount.visibility = View.GONE
        }
        if (listMounts == null && !boolean) {
            toast(R.string.error)
            mView.progressBarMount.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        NetworkControllerDeals.registerMountsDealCallback(null)
        super.onDestroy()
    }
}