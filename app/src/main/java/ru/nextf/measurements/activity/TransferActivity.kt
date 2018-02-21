package ru.nextf.measurements.activity

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import ru.nextf.measurements.ID_KEY
import ru.nextf.measurements.modelAPI.Transfer
import ru.nextf.measurements.network.NetworkController
import ru.nextf.measurements.toast
import kotlinx.android.synthetic.main.activity_transfer.*
import java.util.*


class TransferActivity : AppCompatActivity(), NetworkController.TransferMeasurementCallback {
    private lateinit var id: String
    private lateinit var alert: AlertDialog
    private var date: String? = null
    private var userDate: String? = null
    private var cause: Int? = null
    private var months = emptyArray<String>()
    private var daySave = -1
    private var monthSave = -1
    private var yearSave = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerTransferMeasurementCallback(this)
        months = resources.getStringArray(ru.nextf.measurements.R.array.months)

        super.onCreate(savedInstanceState)
        setContentView(ru.nextf.measurements.R.layout.activity_transfer)
        constraintLayout.setOnTouchListener { v, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
        textViewDate.setOnClickListener { datePick() }
        toolbarAst.setNavigationIcon(ru.nextf.measurements.R.drawable.ic_arrow_back_white_24dp)
        toolbarAst.setNavigationOnClickListener {
            finish()
        }

        imageButton2.setOnClickListener {
            textViewDate.text = getString(ru.nextf.measurements.R.string.select_date)
            date = null
            imageButton2.visibility = View.GONE
        }
        id = intent?.getStringExtra(ID_KEY) ?: "0"

        dateButton.setOnClickListener { datePick() }
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doTransferRequest() }
    }

    override fun onResume() {
        super.onResume()
        NetworkController.registerTransferMeasurementCallback(this)
    }

    private fun doTransferRequest(): Boolean {
        val check = radioGroup.checkedRadioButtonId
        if (check == -1 && date == null) {
            toast(ru.nextf.measurements.R.string.select_date_cause)
            return false
        } else if (date == null) {
            toast(ru.nextf.measurements.R.string.select_date)
            return false
        } else if (check == -1) {
            toast(ru.nextf.measurements.R.string.select_cause)
            return false
        }
        buttonOk.isEnabled = false
        progressBar6.visibility = View.VISIBLE
        val comment = editComment.text.toString()
        val transfer = Transfer(date, cause, if (comment.isEmpty()) null else comment)

        NetworkController.doTransferMeasurement(transfer, id)

        return true
    }

    fun onRadioButtonClick(view: View) {
        cause = when (view.id) {
            ru.nextf.measurements.R.id.radioButtonClient -> 1
            ru.nextf.measurements.R.id.radioButtonDauger -> 2
            else -> -1

        }
    }

    private fun datePick() {
        val myCallBack = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            date = String.format("$year-%02d-%02d", monthOfYear + 1, dayOfMonth)
            imageButton2.visibility = View.VISIBLE
            textViewDate.text = "$dayOfMonth ${months[monthOfYear]} $year года"
            userDate = "$dayOfMonth ${months[monthOfYear]} $year года"
            textViewDate.text = userDate
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

    }



    override fun resultTransfer(result: Boolean) {
        progressBar6.visibility = View.GONE
        if (result) {
            setResult(200)
            toast("Замер перенесен на $userDate")
            finish()
        } else {
            toast(ru.nextf.measurements.R.string.transfer_error)
            buttonOk.isEnabled = true
        }
    }

    override fun onDestroy() {
        NetworkController.registerTransferMeasurementCallback(null)
        super.onDestroy()
    }
}
