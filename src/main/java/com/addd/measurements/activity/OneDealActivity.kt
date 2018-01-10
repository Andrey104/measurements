package com.addd.measurements.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.addd.measurements.*
import com.addd.measurements.fragments.*
import com.addd.measurements.modelAPI.Deal
import com.addd.measurements.network.NetworkControllerDeals
import kotlinx.android.synthetic.main.activity_one_deal.*

class OneDealActivity : AppCompatActivity(), NetworkControllerDeals.OneDealCallback {
    private lateinit var deal: Deal
    private lateinit var json: String
    private lateinit var dealID: String
    private var fragmentName = MAIN_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkControllerDeals.registerOneDealCallback(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_deal)
        setSupportActionBar(toolbarDeal)
        onClickMenu(bottomNavigation)
        getDeal()
    }

    private fun onClickMenu(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.measurement -> {
                    supportActionBar?.show()
                    val bundle = Bundle()
                    if (!json.isNullOrEmpty()) {
                        bundle.putString(DEAL_KEY, json)
                        val fragment = ListMeasurementsFragment()
                        fragment.arguments = bundle
                        val fragmentManager = supportFragmentManager
                        fragmentManager.beginTransaction().replace(R.id.containerDeal, fragment).commit()
                    } else {
                        toast(R.string.error)
                    }


                }

                R.id.problems -> {
                    if (!json.isNullOrEmpty()) {
                        val bundle = Bundle()
                        val fragment = ProblemDealFragment()
                        fragment.arguments = bundle
                        bundle.putString(DEAL_KEY, json)
                        val fragmentManager = supportFragmentManager
                        fragmentManager.beginTransaction().replace(R.id.containerDeal, fragment).commit()
                    }
                }
                R.id.recalculation -> {
                    fragmentName = RECALCULATION_NAME
                    val fragment = LoadFragment()
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.containerDeal, fragment).commit()
                    getDeal()
                }
                R.id.mount -> {
                    fragmentName = MOUNT_NAME
                    val fragment = LoadFragment()
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.containerDeal, fragment).commit()
                    getDeal()
                }
                R.id.mainDeal -> {
                    supportActionBar?.title = String.format("Объект %05d", deal.id)
                    fragmentName = MAIN_NAME
                    val fragment = LoadFragment()
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.containerDeal, fragment).commit()
                    getDeal()
                }
            }
            true
        }
    }

    private fun getDeal() {
        supportActionBar?.show()
        if (intent.hasExtra(DEAL_ID)) {
            dealID = intent.getStringExtra(DEAL_ID)
            supportActionBar?.title = String.format("Объект %05d", dealID.toInt())
            NetworkControllerDeals.getOneDeal(dealID)
        } else {
            bottomNavigation.visibility = View.GONE
            toast(R.string.error)
        }
    }

    override fun resultOneDeal(deal: Deal?, boolean: Boolean) {
        if (deal != null && boolean) {
            json = gson.toJson(deal)
            this.deal = deal
            val bundle = Bundle()
            bundle.putString(DEAL_KEY, json)
            when (fragmentName) {
                MAIN_NAME -> {
                    val fragment = MainDealFragment()
                    fragment.arguments = bundle
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.containerDeal, fragment).commit()
                }

                RECALCULATION_NAME -> {
                    val fragment = RecalculationFragment()
                    fragment.arguments = bundle
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.containerDeal, fragment).commit()
                }

                MOUNT_NAME -> {
                    val fragment = MountFragment()
                    fragment.arguments = bundle
                    val fragmentManager = supportFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.containerDeal, fragment).commit()
                }
            }
        } else {
            toast(R.string.error)
        }
        progressBarOneDeal.visibility = View.GONE
    }
}
