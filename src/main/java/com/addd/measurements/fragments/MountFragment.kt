package com.addd.measurements.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.addd.measurements.DEAL_ID
import com.addd.measurements.DEAL_KEY
import com.addd.measurements.R
import com.addd.measurements.adapters.MountAdapter
import com.addd.measurements.modelAPI.Mount
import com.addd.measurements.network.NetworkControllerDeals
import com.addd.measurements.toast
import kotlinx.android.synthetic.main.mount_fragment.view.*

/**
 * Created by addd on 10.01.2018.
 */
class MountFragment : Fragment(), NetworkControllerDeals.MountsDealCallback, MountAdapter.CustomAdapterCallback {
    private lateinit var adapter: MountAdapter
    private lateinit var mView: View
    private lateinit var bundle: Bundle
    private lateinit var idDeal:String
    private lateinit var mounts : List<Mount>
    var emptyList: ArrayList<Mount> = ArrayList(emptyList())

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        NetworkControllerDeals.registerMountsDealCallback(this)
        val view = inflater?.inflate(R.layout.mount_fragment, container, false) ?: View(context)
        mView = view
        idDeal = bundle.getString(DEAL_ID)
        bundle = this.arguments
        if (bundle.containsKey(DEAL_ID)) {
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
        val bundle = Bundle()
        val fragment = ProblemDealFragment()
        fragment.arguments = bundle
        bundle.putString(DEAL_KEY, idDeal)
        val fragmentManager = (activity as AppCompatActivity).supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.containerDeal, fragment).commit()
        toast(pos)
    }

    override fun resultMountsDeal(listMounts: List<Mount>?, boolean: Boolean) {
        if (listMounts != null && boolean) {
            if (listMounts.size > 1) {
                mounts = listMounts
                displayMounts(listMounts)
            } else {
                //откроем фрагмент на один монтаж
            }
        }
        if (listMounts == null && boolean) {
            toast(R.string.deal_dont_has_mounts)
        }
        if (listMounts == null && !boolean) {
            toast(R.string.error)
        }
    }
}