package com.addd.measurements.activity

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.addd.measurements.R
import kotlinx.android.synthetic.main.activity_transfer.*
import java.util.*
import android.widget.TextView
import android.widget.RadioButton
import android.widget.RadioGroup


class TransferActivity : AppCompatActivity() {
    private var date: String? = null
    private var cause : Int? = null
    private val months = arrayOf("января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября",
            "декабря")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)
        dateButton.setOnClickListener { datePick() }
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doTransferRequest() }
    }

    private fun doTransferRequest() : Boolean {
        val check = radioGroup.checkedRadioButtonId
        if (check == -1 && date == null) {
            Toast.makeText(this, "Выберите дату и причину", Toast.LENGTH_SHORT).show()
            return false
        } else if (date == null) {
            Toast.makeText(this, "Выберите дату", Toast.LENGTH_SHORT).show()
            return false
        } else if (check == -1) {
            Toast.makeText(this, "Выберите причину", Toast.LENGTH_SHORT).show()
            return false
        }

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
            var day: String = if (dayOfMonth < 10) {
                "0" + dayOfMonth
            } else {
                dayOfMonth.toString()
            }
            date = "$year-${monthOfYear + 1}-$day"
            textViewDate.text = "$dayOfMonth ${months[monthOfYear]} $year года"
        }
        val calendar = Calendar.getInstance()
        val datePikerDialog = DatePickerDialog(this, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePikerDialog.show()
    }
}
