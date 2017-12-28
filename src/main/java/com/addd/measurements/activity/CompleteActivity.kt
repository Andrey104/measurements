package com.addd.measurements.activity

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Close
import com.addd.measurements.network.NetworkController
import kotlinx.android.synthetic.main.activity_complete.*
import java.util.*

class CompleteActivity : AppCompatActivity(), NetworkController.CloseCallback {
    private lateinit var id: String
    private var deal: Int = 0
    private lateinit var alert: AlertDialog
    private var serverDate: String? = null
    private lateinit var intentIdKey: String
    private lateinit var intentDealKey: String
    private var months = emptyArray<String>()

    override fun onResume() {
        NetworkController.registerCloseCallback(this)
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        intentDealKey = getString(R.string.deal)
        intentIdKey = getString(R.string.id)
        months = resources.getStringArray(R.array.months)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete)
        if (intent != null) {
            if (intent.hasExtra(intentIdKey)) {
                id = intent.getStringExtra(intentIdKey)
            }
            if (intent.hasExtra(intentDealKey)) {
                deal = intent.getIntExtra(intentDealKey, 0)
            }
        }
        textViewDeal.text = String.format("Договор %05d", deal)
        dateButton.setOnClickListener { datePick() }
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doCompleteRequest() }
    }

    private fun datePick() {
        val myCallBack = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            serverDate = String.format("$year-%02d-%02dT00:00", monthOfYear + 1, dayOfMonth)
            textViewDate.text = "$dayOfMonth ${months[monthOfYear]} $year года"
        }
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    private fun doCompleteRequest(): Boolean {
        if (editTextSum.text.isEmpty() && editTextComment.text.isEmpty()) {
            Toast.makeText(applicationContext, getString(R.string.enter_sum_comment), Toast.LENGTH_SHORT).show()
            return false
        } else if (editTextSum.text.isEmpty()) {
            Toast.makeText(applicationContext, getString(R.string.enter_sum), Toast.LENGTH_SHORT).show()
            return false
        } else if (editTextComment.text.isEmpty()) {
            Toast.makeText(applicationContext, getString(R.string.enter_comment), Toast.LENGTH_SHORT).show()
            return false
        }

        val sum: Float = editTextSum.text.toString().toFloat()

        val close = Close(editTextComment.text.toString(),
                if (editTextPrepayment.text.isEmpty()) null else editTextPrepayment.text.toString().toFloat(),
                sum, checkBoxOffer.isChecked, serverDate,checkBoxCash.isChecked)
        showDialog()
        NetworkController.closeMeasurement(close, id)
        return true
    }

    override fun onStop() {
        NetworkController.registerCloseCallback(null)
        super.onStop()
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
        if (result) {
            setResult(200)
            Toast.makeText(applicationContext, getString(R.string.measurement_closed), Toast.LENGTH_SHORT).show()
            alert.dismiss()
            finish()
        } else {
            alert.dismiss()
            Toast.makeText(applicationContext, getString(R.string.close_measurement_error), Toast.LENGTH_SHORT).show()
        }
    }
}
