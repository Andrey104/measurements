package com.addd.measurements.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Reject
import com.addd.measurements.network.NetworkController
import kotlinx.android.synthetic.main.activity_reject.*

class RejectActivity : AppCompatActivity(), NetworkController.RejectCallback {
    override fun resultReject(code: Int) {
        Toast.makeText(this,code.toString(),Toast.LENGTH_SHORT).show()
    }

    private var cause: Int = 0
    private lateinit var id :String

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerRejectCallback(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reject)
        id = intent.getStringExtra("id")
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doRejectRequest() }
    }

    private fun doRejectRequest(): Boolean {
        val check = radioGroupReject.checkedRadioButtonId
        if (check == -1) {
            Toast.makeText(this, "Выберите причину", Toast.LENGTH_SHORT).show()
            return false
        }
        val comment = editComment.text.toString()
        val reject = Reject(cause,comment)
        NetworkController.rejectMeasurement(this, reject, id)
        return true
    }

    fun onRadioButtonClick(view: View) {
        val checked = (view as RadioButton).isChecked
        // Получаем нажатый переключатель
        when (view.getId()) {
            R.id.radioButtonAnother -> if (checked) {
                cause = 1
            }
            R.id.radioButtonWrongGauger -> if (checked) {
                cause = 2
            }
            R.id.radioButtonWrongManager -> if (checked) {
                cause = 3
            }
        }
    }
}
