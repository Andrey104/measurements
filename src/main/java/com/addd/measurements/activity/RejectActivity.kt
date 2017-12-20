package com.addd.measurements.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Reject
import com.addd.measurements.network.NetworkController
import kotlinx.android.synthetic.main.activity_reject.*

class RejectActivity : AppCompatActivity(), NetworkController.RejectCallback {
    private var cause: Int = 0
    private lateinit var id: String
    private lateinit var alert: AlertDialog
    private lateinit var intentIdKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        intentIdKey = getString(R.string.id)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reject)
        if (intent != null && intent.hasExtra(intentIdKey)) {
            id = intent.getStringExtra(intentIdKey)
        }
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doRejectRequest() }
    }

    override fun onResume() {
        NetworkController.registerRejectCallback(this)
        super.onResume()
    }

    private fun doRejectRequest(): Boolean {
        val check = radioGroupReject.checkedRadioButtonId
        if (check == -1) {
            Toast.makeText(this, getString(R.string.select_cause), Toast.LENGTH_SHORT).show()
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

    override fun onStop() {
        NetworkController.registerRejectCallback(null)
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

    override fun resultReject(code: Int) {
        if (code == 200) {
            Toast.makeText(this, getString(R.string.measurement_reject), Toast.LENGTH_SHORT).show()
            setResult(200)
            alert.dismiss()
            finish()
        } else {
            Toast.makeText(this, getString(R.string.error_measurement_reject), Toast.LENGTH_SHORT).show()
            alert.dismiss()
        }
    }
}
