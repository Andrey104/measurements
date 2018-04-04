package ru.nextf.measurements.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.nextf.measurements.activity.OneMountActivity
import ru.nextf.measurements.adapters.MountAdapter
import ru.nextf.measurements.modelAPI.Mount
import ru.nextf.measurements.*
import ru.nextf.measurements.network.NetworkControllerDeals
import kotlinx.android.synthetic.main.mount_fragment.view.*
import ru.nextf.measurements.activity.AddMountActivity

/**
 * Created by addd on 10.01.2018.
 */
class MountFragment : Fragment(), NetworkControllerDeals.MountsDealCallback, MountAdapter.CustomAdapterCallback {
    private lateinit var adapter: MountAdapter
    private lateinit var mView: View
    private lateinit var bundle: Bundle
    private lateinit var idDeal: String
    private var statusDeal: Int = 0
    private lateinit var mounts: List<Mount>
    var emptyList: ArrayList<Mount> = ArrayList(emptyList())

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        NetworkControllerDeals.registerMountsDealCallback(this)
        val view = inflater?.inflate(ru.nextf.measurements.R.layout.mount_fragment, container, false) ?: View(context)
        mView = view
        bundle = this.arguments
        idDeal = bundle.getString(DEAL_ID)
        bundle = this.arguments
        if (bundle.containsKey(DEAL_ID)) {
            (activity as AppCompatActivity).supportActionBar?.title = String.format("Монтажи %05d", bundle.getString(DEAL_ID).toInt())
            mView.progressBarMount.visibility = View.VISIBLE
            NetworkControllerDeals.getMountsDeal(idDeal)
        } else {
            toast(ru.nextf.measurements.R.string.error)
        }

        if (bundle.containsKey(DEAL_STATUS)) {
            statusDeal = bundle.getInt(DEAL_STATUS)
        }
        if (statusDeal == 2 || statusDeal == 3) {
            mView.fabAddMount.visibility = View.VISIBLE
        }
        mView.fabAddMount.setOnClickListener({
            addMount()
        })
        return view
    }

    private fun addMount() {
        val intent = Intent(context, AddMountActivity::class.java)
        intent.putExtra(DEAL_ID, idDeal)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mView.progressBarMount.visibility = View.VISIBLE
        NetworkControllerDeals.getMountsDeal(idDeal)
    }

    private fun displayMounts(mounts: List<Mount>) {
        adapter = MountAdapter(mounts, this)
        mView.recyclerListMounts.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mView.recyclerListMounts.layoutManager = layoutManager
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
            toast(ru.nextf.measurements.R.string.deal_dont_has_mounts)
            mView.progressBarMount.visibility = View.GONE
        }
        if (listMounts == null && !boolean) {
            toast(ru.nextf.measurements.R.string.error)
            mView.progressBarMount.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        NetworkControllerDeals.registerMountsDealCallback(null)
        super.onDestroy()
    }
}