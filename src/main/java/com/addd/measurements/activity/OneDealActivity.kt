package com.addd.measurements.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.addd.measurements.*
import com.addd.measurements.fragments.ListMeasurementsFragment
import com.addd.measurements.fragments.MainDealFragment
import com.addd.measurements.fragments.ProblemDealFragment
import com.addd.measurements.modelAPI.Deal
import com.addd.measurements.network.NetworkControllerDeals
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_deal.*

class OneDealActivity : AppCompatActivity(), NetworkControllerDeals.OneDealCallback {
    private lateinit var deal : Deal
    private lateinit var json : String

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkControllerDeals.registerOneDealCallback(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_deal)
        setSupportActionBar(toolbarDeal)
        onClickMenu(bottomNavigation)
        mainDealFragment()
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
                    toast("перерасчет")
                }
                R.id.mount -> {
                    toast("монтаж")
                }
                R.id.mainDeal -> {
                    mainDealFragment()
                }
            }
            true
        }
    }

    private fun mainDealFragment() {
        supportActionBar?.show()
        val bundle = Bundle()
        if (intent.hasExtra(DEAL_KEY)) {
            json =  intent.getStringExtra(DEAL_KEY)
            bundle.putString(DEAL_KEY, json)
            getDeal(json)
            val fragment = MainDealFragment()
            fragment.arguments = bundle
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.containerDeal, fragment).commit()
        } else {
            if (intent.hasExtra(DEAL_ID)) {
                NetworkControllerDeals.getOneDeal(intent.getStringExtra(DEAL_ID))
            } else {
                toast(R.string.error)
            }
        }
    }

    override fun resultOneDeal(deal: Deal?) {
        json = gson.toJson(deal)
        val bundle = Bundle()
        bundle.putString(DEAL_KEY, json)
        getDeal(json)
        val fragment = MainDealFragment()
        fragment.arguments = bundle
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.containerDeal, fragment).commit()
    }

    private fun getDeal(string: String): Deal {
        deal = if (string.isNullOrEmpty()) {
            Deal()
        } else {
            val type = object : TypeToken<Deal>() {
            }.type
            gson.fromJson(string, type)
        }
        return deal
    }

}
