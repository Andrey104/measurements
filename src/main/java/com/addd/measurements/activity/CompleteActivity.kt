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
    private lateinit var deal: String
    private lateinit var alert : AlertDialog
    private var serverDate: String? = null
    private val months = arrayOf("января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября",
            "декабря")

    override fun onResume() {
        NetworkController.registerCloseCallback(this)
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete)
        id = intent.getStringExtra("id")
        deal = intent.getStringExtra("deal")
        when (deal.length) {
            1 -> textViewDeal.text = "Договор 0000" + deal
            2 -> textViewDeal.text = "Договор 000" + deal
            3 -> textViewDeal.text = "Договор 00" + deal
            4 -> textViewDeal.text = "Договор 0" + deal
            else -> textViewDeal.text = "Договор $deal"
        }
        dateButton.setOnClickListener { datePick() }
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doCompleteRequest() }
    }

    private fun datePick() {
        val myCallBack = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            var day: String = if (dayOfMonth < 10) {
                "0" + dayOfMonth
            } else {
                dayOfMonth.toString()
            }
            serverDate = "$year-${monthOfYear + 1}-${day}T00:00"
            textViewDate.text = "$dayOfMonth ${months[monthOfYear]} $year года"
        }
        val calendar = Calendar.getInstance()
        val datePikerDialog = DatePickerDialog(this, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePikerDialog.show()
    }

    private fun doCompleteRequest(): Boolean {
        if (editTextSum.text.isEmpty() && editTextComment.text.isEmpty()) {
            Toast.makeText(applicationContext, "Введите сумму и комментарий", Toast.LENGTH_SHORT).show()
            return false
        } else if (editTextSum.text.isEmpty()) {
            Toast.makeText(applicationContext, "Введите сумму", Toast.LENGTH_SHORT).show()
            return false
        } else if (editTextComment.text.isEmpty()) {
            Toast.makeText(applicationContext, "Введите комментарий", Toast.LENGTH_SHORT).show()
            return false
        }

        val sum: Float = editTextSum.text.toString().toFloat()

        val close = Close(editTextComment.text.toString(),
                if (editTextPrepayment.text.isEmpty()) null else editTextPrepayment.text.toString().toFloat(),
                sum, checkBoxOffer.isChecked, serverDate)
        showDialog()
        NetworkController.closeMeasurement(this, close, id)
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

    override fun resultClose(code: Int) {
        if (code == 200) {
            setResult(200)
            Toast.makeText(applicationContext, "Замер завершен", Toast.LENGTH_SHORT).show()
            alert.dismiss()
            finish()
        } else {
            alert.dismiss()
            Toast.makeText(applicationContext, "При завершении произошла ошибка", Toast.LENGTH_SHORT).show()
        }
    }
}
