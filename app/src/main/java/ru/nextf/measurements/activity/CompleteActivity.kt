package ru.nextf.measurements.activity

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.gson.reflect.TypeToken
import ru.nextf.measurements.modelAPI.Close
import ru.nextf.measurements.network.NetworkController
import kotlinx.android.synthetic.main.activity_complete.*
import ru.nextf.measurements.*
import java.util.*
import ru.nextf.measurements.modelAPI.Measurement
import android.support.v7.widget.LinearLayoutManager
import ru.nextf.measurements.adapters.HorizontalAdapter
import ru.nextf.measurements.modelAPI.MeasurementPhoto
import ru.nextf.measurements.modelAPI.Picture
import android.widget.Spinner


class CompleteActivity : AppCompatActivity(), NetworkController.CloseCallback {
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val REQUEST_EXTERNAL_STORAGE = 1
    private var id: String = ""
    private var deal: Int = 0
    private lateinit var alert: AlertDialog
    private lateinit var measurement: Measurement
    private var serverDate: String? = null
    private var months = emptyArray<String>()
    private var daySave = -1
    private var monthSave = -1
    private var yearSave = -1
    private var canRequest = false
    private var arrayPhoto = ArrayList<MeasurementPhoto>()


    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerCloseCallback(this)
        months = resources.getStringArray(ru.nextf.measurements.R.array.months)

        super.onCreate(savedInstanceState)
        setContentView(ru.nextf.measurements.R.layout.activity_complete)
        constraintLayout.setOnTouchListener { v, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
        textViewDate.setOnClickListener {
            datePick()
        }
        toolbarAst.setNavigationIcon(ru.nextf.measurements.R.drawable.ic_arrow_back_white_24dp)
        toolbarAst.setNavigationOnClickListener {
            finish()
        }

//        editTextSum.addTextChangedListener(OwnWatcher())
        checkBoxOffer.setOnClickListener {
            if (checkBoxOffer.isChecked) {
                textViewDateInstallation.visibility = View.VISIBLE
                dateButton.visibility = View.VISIBLE
                textViewDate.visibility = View.VISIBLE
            } else {
                textViewDateInstallation.visibility = View.GONE
                dateButton.visibility = View.GONE
                textViewDate.visibility = View.GONE
                imageButton.visibility = View.GONE
                deleteDateMount()
            }
        }

        getSavedMeasurement()
        id = intent?.getStringExtra(ID_KEY) ?: "0"
        deal = intent?.getIntExtra(DEAL_KEY, 0) ?: throw IllegalStateException()
        textViewDeal.text = String.format("Договор %05d", deal)

        dateButton.setOnClickListener { datePick() }
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doCompleteRequest() }
        imageButton.setOnClickListener {
            deleteDateMount()
            imageButton.visibility = View.GONE
        }
        editTextSum.addTextChangedListener(NumberTextWatcherForThousand(editTextSum))
        canRequest = measurement.pictures?.size != 0

        getPermission()
        displayPictures(measurement)
    }

    private fun displayPictures(measurement: Measurement) {
        measurement.pictures.let {
            arrayPhoto = ArrayList()
            var strBuilder = StringBuilder()
            var count = 0
            var index = 0
            for (photo in measurement.pictures?.iterator() ?: emptyList<Picture>().iterator()) {
                strBuilder.append(photo.url)
                strBuilder.reverse()
                for (char in strBuilder) {
                    index++
                    if (char == '/') count++
                    if (count == 3) {
                        index--
                        break
                    }
                }
                strBuilder.delete(index, strBuilder.length)
                strBuilder.reverse()
                arrayPhoto.add(MeasurementPhoto(BASE_URL + strBuilder.toString(), photo.id.toString()))
                count = 0
                index = 0
                strBuilder.delete(0, strBuilder.length)
            }
        }

        val adapter = HorizontalAdapter(this, arrayPhoto)
        val thirdManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        images.layoutManager = thirdManager
        images.adapter = adapter
        images.adapter.notifyDataSetChanged()
    }

    private fun getPermission() {
        val permission = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    private fun getSavedMeasurement() {
        val json = intent?.getStringExtra(MEASUREMENT_KEY)
        measurement = if (json.isNullOrEmpty()) {
            Measurement()
        } else {
            val type = object : TypeToken<Measurement>() {
            }.type
            gson.fromJson(json, type)
        }
    }

    private fun deleteDateMount() {
        serverDate = null
        textViewDate.text = getString(ru.nextf.measurements.R.string.select_date)
        yearSave = -1
        monthSave = -1
        daySave = 1
    }


    private fun datePick() {
        val myCallBack = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            serverDate = String.format("$year-%02d-%02d", monthOfYear + 1, dayOfMonth)
            imageButton.visibility = View.VISIBLE
            textViewDate.text = "$dayOfMonth ${months[monthOfYear]} $year года"
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
        datePicker.show()
    }

    private fun doCompleteRequest(): Boolean {
        if (editTextSum.text.isEmpty() && spinner.selectedItemPosition == 0) {
            toast(ru.nextf.measurements.R.string.enter_sum_payment)
            return false
        }
        if (editTextSum.text.isEmpty()) {
            toast(ru.nextf.measurements.R.string.enter_sum)
            return false
        }
        if (spinner.selectedItemPosition == 0) {
            toast(ru.nextf.measurements.R.string.enter_payment)
            return false
        }

        if (!canRequest) {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setCancelable(true)
                    .setTitle("Предупреждение")
                    .setMessage("Вы действительно хотите завершить замер без фотографий?")
                    .setPositiveButton(ru.nextf.measurements.R.string.yes)
                    { dialog, _ ->
                        complete()
                        dialog.cancel()
                    }
                    .setNegativeButton(ru.nextf.measurements.R.string.no)
                    { dialog, _ ->
                        dialog.cancel()
                    }
            val alert = builder.create()
            alert.show()
        }
        if (canRequest) {
            complete()
        }
        return true
    }

    private fun complete() {
        buttonOk.isEnabled = false
        var q = NumberTextWatcherForThousand.trimCommaOfString(editTextSum.text.toString())
        if (q[q.length - 1] == '.') {
            q = q.substring(0, q.length - 1)
        }
        val sum: Float = q.toFloat()

        var payment = spinner.selectedItemPosition
        val close = Close(if (editTextComment.text.isEmpty()) null else editTextComment.text.toString(),
                if (editTextPrepayment.text.isEmpty()) null else editTextPrepayment.text.toString().toFloat(),
                sum, checkBoxOffer.isChecked, serverDate, payment)
        showDialog()
        NetworkController.closeMeasurement(close, id)
    }

    override fun onDestroy() {
        NetworkController.registerCloseCallback(null)
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

    override fun resultClose(result: Boolean) {
        alert.dismiss()
        if (result) {
            setResult(200)
            toast(ru.nextf.measurements.R.string.measurement_closed)
            finish()
        } else {
            toast(ru.nextf.measurements.R.string.close_measurement_error)
            buttonOk.isEnabled = true
        }
    }
}
