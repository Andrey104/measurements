package ru.nextf.measurements.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import ru.nextf.measurements.ID_KEY
import ru.nextf.measurements.modelAPI.Reject
import ru.nextf.measurements.network.NetworkController
import ru.nextf.measurements.toast
import kotlinx.android.synthetic.main.activity_reject.*

class RejectActivity : AppCompatActivity(), NetworkController.RejectCallback {
    private var cause: Int = 0
    private lateinit var id: String
    private lateinit var alert: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerRejectCallback(this)

        super.onCreate(savedInstanceState)
        setContentView(ru.nextf.measurements.R.layout.activity_reject)
        toolbarAst.setNavigationIcon(ru.nextf.measurements.R.drawable.ic_arrow_back_white_24dp)
        toolbarAst.setNavigationOnClickListener {
            finish()
        }
        constraintLayout.setOnTouchListener { v, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        id = intent?.getStringExtra(ID_KEY) ?: "0"

        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doRejectRequest() }
    }

    private fun doRejectRequest(): Boolean {
        if (radioGroupReject.checkedRadioButtonId == -1) {
            toast(ru.nextf.measurements.R.string.select_cause)
            return false
        }
        buttonOk.isEnabled = false
        val comment = editComment.text.toString()

        val reject = Reject(cause, if (comment.isEmpty()) null else comment)
        showDialog()
        NetworkController.rejectMeasurement(reject, id)
        return true
    }

    fun onRadioButtonClick(view: View) {
        val checked = (view as RadioButton).isChecked
        // Получаем нажатый переключатель
        if (checked) {
            when (view.getId()) {
                ru.nextf.measurements.R.id.radioButtonAnother -> cause = 1
                ru.nextf.measurements.R.id.radioButtonWrongGauger -> cause = 2
                ru.nextf.measurements.R.id.radioButtonWrongManager -> cause = 3
                ru.nextf.measurements.R.id.radioButtonClientReject -> cause = 4

            }
        }
    }

    override fun onDestroy() {
        NetworkController.registerRejectCallback(null)
        super.onDestroy()
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val viewAlert = layoutInflater.inflate(ru.nextf.measurements.R.layout.update_dialog, null)
        builder.setView(viewAlert)
                .setCancelable(false)
        alert = builder.create()
        alert.show()
    }

    override fun resultReject(result: Boolean) {
        if (result) {
            toast(ru.nextf.measurements.R.string.measurement_reject)
            setResult(200)
            alert.dismiss()
            finish()
        } else {
            toast(ru.nextf.measurements.R.string.error_measurement_reject)
            buttonOk.isEnabled = true
            alert.dismiss()
        }
    }
}
