package com.addd.measurements.activity

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.addd.measurements.DEAL_KEY
import com.addd.measurements.ID_KEY
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Close
import com.addd.measurements.network.NetworkController
import com.addd.measurements.toast
import kotlinx.android.synthetic.main.activity_complete.*
import java.util.*

class CompleteActivity : AppCompatActivity(), NetworkController.CloseCallback {
    private var id: String = ""
    private var deal: Int = 0
    private lateinit var alert: AlertDialog
    private var serverDate: String? = null
    private var months = emptyArray<String>()
    private var daySave = -1
    private var monthSave = -1
    private var yearSave = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerCloseCallback(this)
        months = resources.getStringArray(R.array.months)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete)
        toolbarAst.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbarAst.setNavigationOnClickListener {
            finish()
        }

        checkBoxOffer.setOnClickListener {
            if (checkBoxOffer.isChecked) {
                textViewDateInstallation.visibility = View.VISIBLE
                linearLayoutDateMount.visibility = View.VISIBLE
            } else {
                linearLayoutDateMount.visibility = View.GONE
                textViewDateInstallation.visibility = View.GONE
                deleteDateMount()
            }
        }

        id = intent?.getStringExtra(ID_KEY) ?: "0"
        deal = intent?.getIntExtra(DEAL_KEY, 0) ?: throw IllegalStateException()
        textViewDeal.text = String.format("Договор %05d", deal)

        dateButton.setOnClickListener { datePick() }
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doCompleteRequest() }
        imageButton.setOnClickListener {
           deleteDateMount()
        }
    }

    private fun deleteDateMount() {
        serverDate = null
        textViewDate.text = getString(R.string.select_date)
        yearSave = -1
        monthSave = -1
        daySave = 1
    }
    private fun datePick() {
        val myCallBack = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            serverDate = String.format("$year-%02d-%02d", monthOfYear + 1, dayOfMonth)
            textViewDate.text = "$dayOfMonth ${months[monthOfYear]} $year года"
            yearSave = year
            monthSave = monthOfYear
            daySave = dayOfMonth
        }
        val calendar = Calendar.getInstance()
        val datePicker = if (yearSave == -1) {
            DatePickerDialog(this, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        } else {
            DatePickerDialog(this, myCallBack, yearSave, monthSave, daySave)
        }
        datePicker.show()
        datePicker.show()
    }

    private fun doCompleteRequest(): Boolean {
        if (editTextSum.text.isEmpty() && editTextComment.text.isEmpty()) {
            toast(R.string.enter_sum_comment)
            return false
        } else if (editTextSum.text.isEmpty()) {
            toast(R.string.enter_sum)
            return false
        } else if (editTextComment.text.isEmpty()) {
            toast(R.string.enter_comment)
            return false
        }
        buttonOk.isEnabled = false
        val sum: Float = editTextSum.text.toString().toFloat()

        val close = Close(editTextComment.text.toString(),
                if (editTextPrepayment.text.isEmpty()) null else editTextPrepayment.text.toString().toFloat(),
                sum, checkBoxOffer.isChecked, serverDate, checkBoxCash.isChecked)
        showDialog()
        NetworkController.closeMeasurement(close, id)
        return true
    }

    override fun onDestroy() {
        NetworkController.registerCloseCallback(null)
        super.onDestroy()
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val viewAlert = layoutInflater.inflate(R.layout.update_dialog, null)
        builder.setView(viewAlert)
                .setCancelable(false)
        alert = builder.create()
        alert.show()
    }

    override fun resultClose(result: Boolean) {
        alert.dismiss()
        if (result) {
            setResult(200)
            toast(R.string.measurement_closed)
            finish()
        } else {
            toast(R.string.close_measurement_error)
            buttonOk.isEnabled = true
        }
    }
}
