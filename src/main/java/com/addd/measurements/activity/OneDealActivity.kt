package com.addd.measurements.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.addd.measurements.DEAL_KEY
import com.addd.measurements.R
import com.addd.measurements.fragments.ListMeasurementsFragment
import com.addd.measurements.fragments.MainDealFragment
import com.addd.measurements.fragments.ProblemDealFragment
import com.addd.measurements.fragments.ProblemsFragment
import com.addd.measurements.toast
import kotlinx.android.synthetic.main.activity_one_deal.*

class OneDealActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
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
                    if (intent.hasExtra(DEAL_KEY)) {
                        bundle.putString(DEAL_KEY, intent.getStringExtra(DEAL_KEY))
                        val fragment = ListMeasurementsFragment()
                        fragment.arguments = bundle
                        val fragmentManager = supportFragmentManager
                        fragmentManager.beginTransaction().replace(R.id.containerDeal, fragment).commit()
                    } else {
                        toast(R.string.error)
                    }


                }

                R.id.problems -> {
                    if (intent.hasExtra(DEAL_KEY)) {
                        val bundle = Bundle()
                        val fragment = ProblemDealFragment()
                        fragment.arguments = bundle
                        bundle.putString(DEAL_KEY, intent.getStringExtra(DEAL_KEY))
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
            bundle.putString(DEAL_KEY, intent.getStringExtra(DEAL_KEY))
            val fragment = MainDealFragment()
            fragment.arguments = bundle
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.containerDeal, fragment).commit()
        } else {
            toast(R.string.error)
        }
    }
}
