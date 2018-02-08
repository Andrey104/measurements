package com.addd.measurements.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.addd.measurements.DEAL_ID
import com.addd.measurements.R
import com.addd.measurements.modelAPI.RecalculationRequest
import com.addd.measurements.network.NetworkControllerDeals
import com.addd.measurements.toast
import kotlinx.android.synthetic.main.activity_add_recalculation.*


class AddRecalculationActivity : AppCompatActivity(), NetworkControllerDeals.DiscountCallback {
      private var dealId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkControllerDeals.registerDiscountCallback(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recalculation)
        toolbar2.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar2.setNavigationOnClickListener {
            finish()
        }
        dealId = intent.getStringExtra(DEAL_ID)
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doAddRecalculation() }
    }

    private fun doAddRecalculation(): Boolean {
        if (editTextHeader.text.isEmpty() && editTextDescription.text.isEmpty()) {
            toast(R.string.enter_sum_comment)
            return false
        } else if (editTextHeader.text.isEmpty()) {
            toast(R.string.enter_sum_after_discount)
            return false
        } else if (editTextDescription.text.isEmpty()) {
            toast(R.string.enter_comment)
            return false
        }
        buttonOk.isEnabled = false
        val discount = RecalculationRequest(editTextHeader.text.toString().toFloat(), editTextDescription.text.toString())
        NetworkControllerDeals.addDiscount(discount, dealId)
        return true
    }

    override fun resultAddDiscount(boolean: Boolean) {
        if (boolean) {
            toast(R.string.discount_added_successful)
            setResult(200)
            finish()
        } else {
            toast(R.string.error_add_discount)
            buttonOk.isEnabled = true
        }
    }

}
