package com.addd.measurements.activity

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.addd.measurements.MeasurementsAPI
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Transfer
import kotlinx.android.synthetic.main.activity_transfer.*
import retrofit2.Call
import retrofit2.Response
import java.util.*


class TransferActivity : AppCompatActivity() {
    private lateinit var id: String
    lateinit var alert: AlertDialog
    private var date: String? = null
    private var userDate: String? = null
    private var APP_TOKEN: String = "token"
    private lateinit var mSettings: SharedPreferences
    private var cause: Int? = null
    private val serviceAPI = MeasurementsAPI.Factory.create()
    private val months = arrayOf("января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября",
            "декабря")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)
        id = intent.getStringExtra("id")
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
        dateButton.setOnClickListener { datePick() }
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doTransferRequest() }
    }

    private fun doTransferRequest(): Boolean {
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

val comment = editComment.text.toString()
        val transfer = Transfer(date, cause,if(comment.isEmpty()) null else comment)
        val token = getToken()
        val call = serviceAPI.transferMeasurement(token, transfer, id)
        showDialog()
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                response?.let {
                    if (it.code() == 200) {
                    alert.dismiss()
                        setResult(200)
                        Toast.makeText(applicationContext, "Замер перенесен на $userDate", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                    alert.dismiss()
            }
        })
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
            date = "$year-${monthOfYear + 1}-${day}T00:00"
            textViewDate.text = "$dayOfMonth ${months[monthOfYear]} $year года"
            userDate = "$dayOfMonth ${months[monthOfYear]} $year года"
        }
        val calendar = Calendar.getInstance()
        val datePikerDialog = DatePickerDialog(this, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePikerDialog.show()
    }

    private fun getToken(): String {
        mSettings = PreferenceManager.getDefaultSharedPreferences(this)
        var token = ""
        if (mSettings.contains(APP_TOKEN)) {
            token = "Token " + mSettings.getString(APP_TOKEN, "")
        }
        return token
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val viewAlert = layoutInflater.inflate(R.layout.update_dialog, null)
        builder.setView(viewAlert)
                .setCancelable(false)
        alert = builder.create()
        alert.show()
    }
}
