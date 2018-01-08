package com.addd.measurements.fragments

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.addd.measurements.DEAL_KEY
import com.addd.measurements.R
import com.addd.measurements.gson
import com.addd.measurements.modelAPI.Deal
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.main_deal_fragment.view.*

/**
 * Created by addd on 08.01.2018.
 */

class MainDealFragment : Fragment() {
    private lateinit var deal: Deal
    private lateinit var bundle: Bundle

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater?.inflate(R.layout.main_deal_fragment, container, false) ?: View(context)
        bundle = this.arguments
        getSavedDeal()
        displayDeal(view)

        return view
    }

    private fun displayDeal(view: View) {
        (activity as AppCompatActivity).supportActionBar?.title = String.format("Объект %05d", deal.id)
        view.symbol.text = deal.company?.symbol
        view.address.text = deal.address

        if (deal.sum == null) {
            view.textViewSum.text = "Не оплачено"
        } else {
            view.textViewSum.text = "Сумма - " + deal.sum
        }
        setColorCompany(deal.company?.id ?: 1, view)

        view.progressBar3.visibility = View.GONE
    }

    private fun setColorCompany(color: Int, view: View) {
        selectColorVersion(view?.symbol, when (color) {
            1 -> R.color.green
            2 -> R.color.orange
            3 -> R.color.blue
            else -> R.color.blue
        })

    }

    private fun selectColorVersion(item: TextView?, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            item?.setTextColor(context.resources.getColor(color, context.theme))
        } else {
            item?.setTextColor(context.resources.getColor(color))
        }
    }

    private fun getSavedDeal(): Deal {
        val json = bundle?.getString(DEAL_KEY)
        deal = if (json.isNullOrEmpty()) {
            Deal()
        } else {
            val type = object : TypeToken<Deal>() {
            }.type
            gson.fromJson(json, type)
        }
        return deal
    }


}