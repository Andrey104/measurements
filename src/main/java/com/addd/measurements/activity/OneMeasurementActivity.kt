package com.addd.measurements.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.addd.measurements.network.MeasurementsAPI
import com.addd.measurements.R
import com.addd.measurements.adapters.ClientAdapter
import com.addd.measurements.modelAPI.Measurement
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_measurement.*
import kotlinx.android.synthetic.main.content_one_measurement.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OneMeasurementActivity : AppCompatActivity() {
    lateinit var measurement: Measurement
    private lateinit var alert : AlertDialog
    private lateinit var APP_TOKEN: String
    private val serviceAPI = MeasurementsAPI.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        APP_TOKEN = getString(R.string.token)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_measurement)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            showPopupMenu(view)
        }
        displayMeasurement(getSavedMeasurement())

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_images, menu)
        return true
    }

    //берем один замер по id
    private fun getOneMeasurement() {
        val mSettings = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        var token = ""
        if (mSettings.contains(APP_TOKEN)) {
            token = "Token " + mSettings.getString(APP_TOKEN, "")
        }
        val call = serviceAPI.getOneMeasurement(token, measurement.id.toString())
        call.enqueue(object : Callback<Measurement> {
            override fun onResponse(call: Call<Measurement>?, response: Response<Measurement>?) {
                if (response!!.body() != null) {
                    measurement = response!!.body()
                }
                displayMeasurement(measurement)
                alert.dismiss()
            }

            override fun onFailure(call: Call<Measurement>?, t: Throwable?) {
                Toast.makeText(applicationContext, "При обновлении данных произошла ошибка", Toast.LENGTH_SHORT).show()
                alert.dismiss()
            }

        })
    }

    private fun getSavedMeasurement(): Measurement {
        val gson = Gson()
        val json = intent.getStringExtra("measurement")
        if (json!!.isEmpty()) {
            measurement = Measurement()
        } else {
            val type = object : TypeToken<Measurement>() {
            }.type
            measurement = gson.fromJson(json, type)
        }
        return measurement
    }

    private fun displayMeasurement(measurement: Measurement) {
        title = "Замер ${intent.getStringExtra("id")}"
        setStatus(measurement)


        if (measurement.company!!.symbol!!.length == 1) {
            symbol.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30F)
        }
        symbol.text = measurement.company.symbol.toString()
        setColorSymbol(measurement)

        address.text = measurement.address.toString()
        time.text = measurement.time.toString()
        date.text = measurement.date.toString()
        if (measurement.workerName == null) {
            worker_name.text = "Не распределено"
        } else {
            worker_name.text = measurement.workerName.toString()
        }
        setColorWorker(measurement)
        comment.text = measurement.managerComment.toString()

        list_clients.adapter = ClientAdapter(measurement.clients!!)
        val layoutManager = LinearLayoutManager(applicationContext)
        list_clients.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(list_clients.context, layoutManager.orientation)
        list_clients.addItemDecoration(dividerItemDecoration)

    }

    private fun setColorSymbol(measurement: Measurement) {
        when (measurement.company!!.id) {
            1 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                symbol.setTextColor(resources.getColor(R.color.green, applicationContext.theme))
            } else {
                symbol.setTextColor(resources.getColor(R.color.green))
            }
            2 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                symbol.setTextColor(resources.getColor(R.color.orange, applicationContext.theme))
            } else {
                symbol.setTextColor(resources.getColor(R.color.orange))
            }
            3 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                symbol.setTextColor(resources.getColor(R.color.blue, applicationContext.theme))
            } else {
                symbol.setTextColor(resources.getColor(R.color.blue))
            }
        }
    }

    private fun setColorWorker(measurement: Measurement) {
        when (measurement.color) {
            1 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                worker_name.setTextColor(resources.getColor(R.color.red, applicationContext.theme))
            } else {
                worker_name.setTextColor(resources.getColor(R.color.red))
            }
            2 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                worker_name.setTextColor(resources.getColor(R.color.green, applicationContext.theme))
            } else {
                worker_name.setTextColor(resources.getColor(R.color.green))
            }
            3 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                worker_name.setTextColor(resources.getColor(R.color.blue, applicationContext.theme))
            } else {
                worker_name.setTextColor(resources.getColor(R.color.blue))
            }
        }
    }

    private fun setStatus(measurement: Measurement) {
        when (measurement.status) {
            0, 1 -> textViewStatus.text = "Замер не завершен"
            2, 3 -> {
                textViewStatus.text = "Замер завершен"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textViewStatus.setTextColor(resources.getColor(R.color.red, applicationContext.theme))
                } else {
                    textViewStatus.setTextColor(resources.getColor(R.color.red))
                }
            }
            4 -> {
                textViewStatus.text = "Замер отклонен"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textViewStatus.setTextColor(resources.getColor(R.color.red, applicationContext.theme))
                } else {
                    textViewStatus.setTextColor(resources.getColor(R.color.red))
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 200) {
            showDialog()
            getOneMeasurement()
        }
    }

    private fun showPopupMenu(view: View) {
        var popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        try {
            val classPopupMenu = Class.forName(popupMenu.javaClass.name)
            val mPopup = classPopupMenu.getDeclaredField("mPopup")
            mPopup.isAccessible = true
            val menuPopupHelper = mPopup.get(popupMenu)
            val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
            val setForceIcons = classPopupHelper.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
            setForceIcons.invoke(menuPopupHelper, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.complete -> {
                    Toast.makeText(applicationContext,
                            "Вы выбрали PopupMenu 1",
                            Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.shift -> {
                    val intent = Intent(applicationContext, TransferActivity::class.java)
                    intent.putExtra("id", measurement.id.toString())
                    startActivityForResult(intent, 0)
                    true
                }
                R.id.reject -> {
                    Toast.makeText(applicationContext,
                            "Вы выбрали PopupMenu 3",
                            Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.problem -> {
                    Toast.makeText(applicationContext,
                            "Вы выбрали PopupMenu 4",
                            Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
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
