package ru.nextf.measurements.activity

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_mount.*
import ru.nextf.measurements.*
import ru.nextf.measurements.modelAPI.Mount
import ru.nextf.measurements.modelAPI.MountAdd
import ru.nextf.measurements.network.NetworkController
import java.util.*

class AddMountActivity : AppCompatActivity(), NetworkController.MountAddCallback {
    private var serverDate = ""
    private var description = ""
    private lateinit var mount: Mount
    private lateinit var dealId: String
    private var months = emptyArray<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerMountAddCallback(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_mount)
        dealId = intent.getStringExtra(DEAL_ID)
        months = resources.getStringArray(ru.nextf.measurements.R.array.months)
        dateButton2.setOnClickListener { datePick() }
        buttonOk.setOnClickListener { addMount() }
        buttonCancel.setOnClickListener { finish() }
        imageButton3.setOnClickListener {
            deleteDateMount()
            imageButton3.visibility = View.GONE
        }
    }

    private fun deleteDateMount() {
        serverDate = ""
        textViewDate2.text = getString(ru.nextf.measurements.R.string.select_date)
    }

    private fun datePick() {
        val myCallBack = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            serverDate = String.format("$year-%02d-%02d", monthOfYear + 1, dayOfMonth)
            imageButton3.visibility = View.VISIBLE
            textViewDate2.text = "$dayOfMonth ${months[monthOfYear]} $year года"
        }
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.show()
    }

    private fun addMount() {
        buttonOk.isEnabled = false
        progressBar8.visibility = View.VISIBLE
            NetworkController.addMount(dealId,
                    MountAdd(if (serverDate.isEmpty()) null else serverDate,
                            if (editComment2.text.isNullOrEmpty()) null else editComment2.text.toString()))
    }

    override fun resultMountAdd(result: Boolean) {
        buttonOk.isEnabled = true
        progressBar8.visibility = View.GONE
        if (result) {
            setResult(200)
            toast(getString(R.string.mount_added_successful))
            finish()
        } else {
            toast(R.string.error)
        }
    }

    private fun getSaveMount() {
        if (intent.hasExtra(MOUNT_NAME)) {
            val json = intent.getStringExtra(MOUNT_NAME)
            mount = if (json.isNullOrEmpty()) {
                Mount()
            } else {
                val type = object : TypeToken<Mount>() {
                }.type
                gson.fromJson(json, type)
            }
        } else {
            toast(ru.nextf.measurements.R.string.error)
            finish()
        }
    }
}
