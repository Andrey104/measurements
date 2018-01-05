package com.addd.measurements.activity

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.View
import android.widget.TextView
import com.addd.measurements.DEAL_KEY
import com.addd.measurements.R
import com.addd.measurements.gson
import com.addd.measurements.modelAPI.Deal
import com.addd.measurements.toast
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_deal.*

class OneDealActivity : AppCompatActivity() {
    private lateinit var deal: Deal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_deal)
        setSupportActionBar(toolbarDeal)

        onClickMenu(bottomNavigation)
        deal = getSavedDeal()
        displayDeal()
    }

    private fun onClickMenu(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.measurement -> {
                    toast("замер")
                }
                R.id.problems -> {
                    toast("проблемы")
                }
                R.id.recalculation -> {
                    toast("перерасчет")
                }
                R.id.mount -> {
                    toast("монтаж")
                }
                R.id.something -> {
                    toast("что-то")
                }
            }
            true
        }
    }

    private fun getSavedDeal(): Deal {
        val json = intent?.getStringExtra(DEAL_KEY)
        deal = if (json.isNullOrEmpty()) {
            Deal()
        } else {
            val type = object : TypeToken<Deal>() {
            }.type
            gson.fromJson(json, type)
        }
        return deal
    }

    private fun displayDeal() {
        title = String.format("Объект %05d", deal.id)
        symbol.text = deal.company?.symbol
        address.text = deal.address
        setColorCompany(deal.company?.id ?: 1)


        progressBar3.visibility = View.GONE
    }

    private fun setColorCompany(color: Int) {
        selectColorVersion(symbol, when (color) {
            1 -> R.color.green
            2 -> R.color.orange
            3 -> R.color.blue
            else -> R.color.blue
        })

    }

    private fun selectColorVersion(item: TextView, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            item.setTextColor(applicationContext.resources.getColor(color, applicationContext.theme))
        } else {
            item.setTextColor(applicationContext.resources.getColor(color))
        }
    }

}
