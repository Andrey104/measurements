package ru.nextf.measurements.activity

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_edit_mount.*
import ru.nextf.measurements.*
import ru.nextf.measurements.modelAPI.Mount
import ru.nextf.measurements.modelAPI.MountEdit
import ru.nextf.measurements.network.NetworkController
import java.util.*

class EditMountActivity : AppCompatActivity(), NetworkController.MountEditCallback {
    private var serverDate: String? = null
    private var oldDate: String? = null
    private var causeIsRequired = false

    private var description: String? = null
    private var cause = 0
    private lateinit var mount: Mount
    private lateinit var dealId: String
    private var months = emptyArray<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerMountEditCallback(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_mount)
        dealId = intent.getStringExtra(DEAL_ID)
        months = resources.getStringArray(ru.nextf.measurements.R.array.months)
        dateButton2.setOnClickListener { datePick() }
        buttonOk.setOnClickListener { editMount() }
        buttonCancel.setOnClickListener { finish() }
        imageButton3.setOnClickListener {
            deleteDateMount()
            imageButton3.visibility = View.GONE
        }
        getSaveMount()
        if (intent.hasExtra(EDIT_MOUNT_DATE)) {
            oldDate = intent.getStringExtra(EDIT_MOUNT_DATE)
            imageButton3.visibility = View.VISIBLE
            serverDate = intent.getStringExtra(EDIT_MOUNT_DATE)
            textViewDate2.text = formatDateMount(intent.getStringExtra(EDIT_MOUNT_DATE))
        }
        if (intent.hasExtra(EDIT_MOUNT_DESCRIPTION)) {
            description = intent.getStringExtra(EDIT_MOUNT_DESCRIPTION)
            editComment2.setText(description)
        }


    }

    private fun deleteDateMount() {
        serverDate = null
        constraint_cause.visibility = View.GONE
        causeIsRequired = false
        textViewDate2.text = getString(ru.nextf.measurements.R.string.select_date)
    }

    private fun datePick() {
        val myCallBack = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            serverDate = String.format("$year-%02d-%02d", monthOfYear + 1, dayOfMonth)
            if (intent.hasExtra(EDIT_MOUNT_DATE)) {
                if (serverDate != oldDate) {
                    constraint_cause.visibility = View.VISIBLE
                    causeIsRequired = true
                } else {
                    cause = 0
                    constraint_cause.visibility = View.GONE
                    causeIsRequired = false
                }
            }
            imageButton3.visibility = View.VISIBLE
            textViewDate2.text = "$dayOfMonth ${months[monthOfYear]} $year года"
        }
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.show()
    }

    fun onRadioButtonClick(view: View) {
        val checked = (view as RadioButton).isChecked
        // Получаем нажатый переключатель
        if (checked) {
            when (view.getId()) {
                R.id.radioButtonClient -> cause = 1
                R.id.radioButtonCompany -> cause = 2

            }
        }
    }

    private fun editMount() {
        buttonOk.isEnabled = false
        progressBar8.visibility = View.VISIBLE
        if (causeIsRequired) {
            if (cause == 0) {
                toast(R.string.select_cause)
                buttonOk.isEnabled = true
                progressBar8.visibility = View.GONE
                return
            } else {
                NetworkController.editMount(mount.id.toString(),
                        MountEdit(if (serverDate == null) "1970-01-01" else serverDate,
                                if (editComment2.text.isNullOrEmpty()) "" else editComment2.text.toString(), cause))
            }
        } else {
            NetworkController.editMount(mount.id.toString(),
                    MountEdit(if (serverDate == null) "1970-01-01" else serverDate,
                            if (editComment2.text.isNullOrEmpty()) "" else editComment2.text.toString(), null))
        }
    }

    override fun resultMountEdit(result: Boolean, mount: Mount?) {
        buttonOk.isEnabled = true
        progressBar8.visibility = View.GONE
        if (result) {
            if (mount != null) {
                print(mount)
            }
            setResult(200)
            toast(R.string.mount_edited_successful)
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
