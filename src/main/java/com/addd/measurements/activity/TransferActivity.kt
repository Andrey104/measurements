package com.addd.measurements.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Transfer
import com.addd.measurements.network.NetworkController
import kotlinx.android.synthetic.main.activity_transfer.*
import java.util.*


class TransferActivity : AppCompatActivity(), NetworkController.TransferMeasurementCallback {
    private lateinit var id: String
    lateinit var alert: AlertDialog
    private var date: String? = null
    private var userDate: String? = null
    private var cause: Int? = null
    private var months = emptyArray<String>()
    private lateinit var intentIdKey: String


    override fun onCreate(savedInstanceState: Bundle?) {
        months = resources.getStringArray(R.array.months)
        intentIdKey = getString(R.string.id)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)
        if (intent != null && intent.hasExtra(intentIdKey)) {
            id = intent.getStringExtra(intentIdKey)
        }
        dateButton.setOnClickListener { datePick() }
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doTransferRequest() }
    }

    private fun doTransferRequest(): Boolean {
        val check = radioGroup.checkedRadioButtonId
        if (check == -1 && date == null) {
            Toast.makeText(this, getString(R.string.select_date_cause), Toast.LENGTH_SHORT).show()
            return false
        } else if (date == null) {
            Toast.makeText(this, getString(R.string.select_date), Toast.LENGTH_SHORT).show()
            return false
        } else if (check == -1) {
            Toast.makeText(this, getString(R.string.select_cause), Toast.LENGTH_SHORT).show()
            return false
        }

        val comment = editComment.text.toString()
        val transfer = Transfer(date, cause, if (comment.isEmpty()) null else comment)
        showDialog()

        NetworkController.doTransferMeasurement(transfer, id)

        return true
    }

    fun onRadioButtonClick(view: View) {
        val checked = (view as RadioButton).isChecked
        // Получаем нажатый переключатель
        when (view.getId()) {
            R.id.radioButtonClient -> if (checked) {
                cause = 1
            }
            R.id.radioButtonDauger -> if (checked) {
                cause = 2
            }
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

    override fun onResume() {
        NetworkController.registerTransferMeasurementCallback(this)
        super.onResume()
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val viewAlert = layoutInflater.inflate(R.layout.update_dialog, null)
        builder.setView(viewAlert)
                .setCancelable(false)
        alert = builder.create()
        alert.show()
    }

    override fun resultTransfer(code: Int) {
        if (code == 200) {
            alert.dismiss()
            setResult(200)
            Toast.makeText(applicationContext, "Замер перенесен на $userDate", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            alert.dismiss()
            Toast.makeText(applicationContext, getString(R.string.transfer_error), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        NetworkController.registerTransferMeasurementCallback(null)
        super.onStop()
    }
}
