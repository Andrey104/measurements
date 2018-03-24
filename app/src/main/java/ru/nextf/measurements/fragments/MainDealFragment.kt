package ru.nextf.measurements.fragments

import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.nextf.measurements.DEAL_KEY
import ru.nextf.measurements.adapters.ActionAdapter
import ru.nextf.measurements.gson
import ru.nextf.measurements.modelAPI.Action
import ru.nextf.measurements.modelAPI.Deal
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.dialog_address_comment.view.*
import kotlinx.android.synthetic.main.dialog_description.view.*
import kotlinx.android.synthetic.main.main_deal_fragment.view.*

/**
 * Created by addd on 08.01.2018.
 */

class MainDealFragment : Fragment() {
    private lateinit var deal: Deal
    private lateinit var bundle: Bundle
    var emptyList: ArrayList<Action> = ArrayList(emptyList())
    private lateinit var actions: List<Action>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater?.inflate(ru.nextf.measurements.R.layout.main_deal_fragment, container, false)
                ?: View(context)
        bundle = this.arguments
        getSavedDeal()
        displayDeal(view)
        setDialogs(view)

        return view
    }

    private fun setDialogs(view: View) {
        view.constraint1.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val view = layoutInflater.inflate(ru.nextf.measurements.R.layout.dialog_address_comment, null)
            view.textViewAddress.text = deal.address
            if (deal.addressComment == null || deal.addressComment == "") {
                view.textView10.text = "Комментарий отсутствует"
            } else {
                view.textViewAddressComment.text = deal.addressComment
            }
            builder.setView(view)
                    .setCancelable(true)
                    .setPositiveButton(ru.nextf.measurements.R.string.okay)
                    { dialog, _ ->
                        dialog.cancel()
                    }
            val alert = builder.create()
            alert.show()
        }

        view.constraint_description.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val view = layoutInflater.inflate(ru.nextf.measurements.R.layout.dialog_description, null)
            view.textViewDescription.text = deal.description
            builder.setView(view)
                    .setCancelable(true)
                    .setPositiveButton(ru.nextf.measurements.R.string.okay)
                    { dialog, _ ->
                        dialog.cancel()
                    }
            val alert = builder.create()
            alert.show()
        }

    }

    private fun displayDeal(view: View) {
        if (deal.description == null || deal.description == "") {
            view.constraint_description.visibility = View.GONE
        } else {
            view.description_text.text = deal.description
        }
        var mp: Drawable
        var n: Drawable
        var b: Drawable
        var unknown: Drawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mp = resources.getDrawable(ru.nextf.measurements.R.drawable.mp, null)
            n = resources.getDrawable(ru.nextf.measurements.R.drawable.n, null)
            b = resources.getDrawable(ru.nextf.measurements.R.drawable.b, null)
            unknown = resources.getDrawable(ru.nextf.measurements.R.drawable.unknown, null)
        } else {
            mp = resources.getDrawable(ru.nextf.measurements.R.drawable.mp)
            n = resources.getDrawable(ru.nextf.measurements.R.drawable.n)
            b = resources.getDrawable(ru.nextf.measurements.R.drawable.b)
            unknown = resources.getDrawable(ru.nextf.measurements.R.drawable.unknown)
        }


        when (deal.company?.symbol) {
            "МП", "MP" -> view.imageView13.background = mp
            "Б", "B" -> view.imageView13.background = b
            "Н", "H" -> view.imageView13.background = n
            else -> view.imageView13.background = unknown
        }
        view.address.text = deal.address

        if (deal.sum == null) {
            view.textViewSumBefore.text = getString(ru.nextf.measurements.R.string.unpaid)
        } else {
            view.textViewSumBefore.text = "Сумма: ${String.format("%,.2f", deal.sum)}р"
        }
        actions = deal.actions ?: emptyList
        val adapter = ActionAdapter(actions as ArrayList)
        view.actionRecyclerList.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        view.actionRecyclerList.layoutManager = layoutManager

        view.textViewStatusDeal.text = when (deal.status) {
            0 -> getString(ru.nextf.measurements.R.string.in_treatment)
            1 -> getString(ru.nextf.measurements.R.string.measurement)
            2 -> getString(ru.nextf.measurements.R.string.not_contract)
            3 -> getString(ru.nextf.measurements.R.string.mount)
            4 -> getString(ru.nextf.measurements.R.string.deal_complete)
            5 -> getString(ru.nextf.measurements.R.string.deal_reject)
            else -> getString(ru.nextf.measurements.R.string.status_deal)
        }
        view.progressBar3.visibility = View.GONE
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