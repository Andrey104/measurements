package ru.nextf.measurements.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_add_recalculation.*
import ru.nextf.measurements.DEAL_ID
import ru.nextf.measurements.NumberTextWatcherForThousand
import ru.nextf.measurements.modelAPI.RecalculationRequest
import ru.nextf.measurements.myWebSocket
import ru.nextf.measurements.network.NetworkControllerDeals
import ru.nextf.measurements.toast


class AddRecalculationActivity : AppCompatActivity(), NetworkControllerDeals.DiscountCallback {
    private var dealId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkControllerDeals.registerDiscountCallback(this)
        super.onCreate(savedInstanceState)
        setContentView(ru.nextf.measurements.R.layout.activity_add_recalculation)
        constraintLayout.setOnTouchListener { v, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
        toolbar2.setNavigationIcon(ru.nextf.measurements.R.drawable.ic_arrow_back_white_24dp)
        toolbar2.setNavigationOnClickListener {
            finish()
        }
        dealId = intent.getStringExtra(DEAL_ID)
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doAddRecalculation() }
        editTextHeader.addTextChangedListener(NumberTextWatcherForThousand(editTextHeader))
    }

    private fun doAddRecalculation(): Boolean {
        if (editTextHeader.text.isEmpty() && editTextDescription.text.isEmpty()) {
            toast(ru.nextf.measurements.R.string.enter_sum_comment)
            return false
        } else if (editTextHeader.text.isEmpty()) {
            toast(ru.nextf.measurements.R.string.enter_sum_after_discount)
            return false
        } else if (editTextDescription.text.isEmpty()) {
            toast(ru.nextf.measurements.R.string.enter_comment)
            return false
        }
        var after = NumberTextWatcherForThousand.trimCommaOfString(editTextHeader.text.toString())
        if (after[after.length - 1] == '.') {
            after = after.substring(0, after.length - 1)
        }
        val close: Float = after.toFloat()
        buttonOk.isEnabled = false
        val discount = RecalculationRequest(close, editTextDescription.text.toString())
        NetworkControllerDeals.addDiscount(discount, dealId)
        progressBar7.visibility = View.VISIBLE
        return true
    }

    override fun resultAddDiscount(boolean: Boolean) {
        progressBar7.visibility = View.GONE
        if (boolean) {
            toast(ru.nextf.measurements.R.string.discount_added_successful)
            setResult(200)
            finish()
        } else {
            toast(ru.nextf.measurements.R.string.error_add_discount)
            buttonOk.isEnabled = true
        }
    }

}
