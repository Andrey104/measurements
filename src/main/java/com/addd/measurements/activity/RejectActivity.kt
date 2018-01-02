package com.addd.measurements.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.RadioButton
import com.addd.measurements.ID_KEY
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Reject
import com.addd.measurements.network.NetworkController
import com.addd.measurements.toast
import kotlinx.android.synthetic.main.activity_reject.*

class RejectActivity : AppCompatActivity(), NetworkController.RejectCallback {
    private var cause: Int = 0
    private lateinit var id: String
    private lateinit var alert: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerRejectCallback(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reject)

        id = intent?.getStringExtra(ID_KEY) ?: "0"

        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doRejectRequest() }
    }

    private fun doRejectRequest(): Boolean {
        if (radioGroupReject.checkedRadioButtonId == -1) {
            toast(R.string.select_cause)
            return false
        }
        val comment = editComment.text.toString()

        val reject = Reject(cause, if (comment.isEmpty()) null else comment)
        showDialog()
        NetworkController.rejectMeasurement(reject, id)
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

    override fun onDestroy() {
        NetworkController.registerRejectCallback(null)
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

    override fun resultReject(result: Boolean) {
        if (result) {
            toast(R.string.measurement_reject)
            setResult(200)
            alert.dismiss()
            finish()
        } else {
            toast(R.string.error_measurement_reject)
            alert.dismiss()
        }
    }
}
