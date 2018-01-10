package com.addd.measurements.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.addd.measurements.ID_KEY
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Transfer
import com.addd.measurements.network.NetworkController
import com.addd.measurements.toast
import kotlinx.android.synthetic.main.activity_transfer.*
import java.util.*


class TransferActivity : AppCompatActivity(), NetworkController.TransferMeasurementCallback {
    private lateinit var id: String
    private lateinit var alert: AlertDialog
    private var date: String? = null
    private var userDate: String? = null
    private var cause: Int? = null
    private var months = emptyArray<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerTransferMeasurementCallback(this)
        months = resources.getStringArray(R.array.months)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        id = intent?.getStringExtra(ID_KEY) ?: "0"

        dateButton.setOnClickListener { datePick() }
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doTransferRequest() }
    }

    private fun doTransferRequest(): Boolean {
        val check = radioGroup.checkedRadioButtonId
        if (check == -1 && date == null) {
            toast(R.string.select_date_cause)
            return false
        } else if (date == null) {
            toast(R.string.select_date)
            return false
        } else if (check == -1) {
            toast(R.string.select_cause)
            return false
        }
        buttonOk.isEnabled = false
        val comment = editComment.text.toString()
        val transfer = Transfer(date, cause, if (comment.isEmpty()) null else comment)
        showDialog()

        NetworkController.doTransferMeasurement(transfer, id)

        return true
    }

    fun onRadioButtonClick(view: View) {
        cause = when (view.id) {
            R.id.radioButtonClient -> 1
            R.id.radioButtonDauger -> 2
            else -> -1

        }
    }

    private fun datePick() {
        val myCallBack = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            date = String.format("$year-%02d-%02dT00:00", monthOfYear + 1, dayOfMonth)
            textViewDate.text = "$dayOfMonth ${months[monthOfYear]} $year года"
            userDate = "$dayOfMonth ${months[monthOfYear]} $year года"
            textViewDate.text = userDate
        }
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        datePicker.show()

    }


    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val viewAlert = layoutInflater.inflate(R.layout.update_dialog, null)
        builder.setView(viewAlert)
                .setCancelable(false)
        alert = builder.create()
        alert.show()
    }

    override fun resultTransfer(result: Boolean) {
        if (result) {
            alert.dismiss()
            setResult(200)
            toast("Замер перенесен на $userDate")
            finish()
        } else {
            alert.dismiss()
            toast(R.string.transfer_error)
            buttonOk.isEnabled = true
        }
    }

    override fun onDestroy() {
        NetworkController.registerTransferMeasurementCallback(null)
        super.onDestroy()
    }
}
