package com.addd.measurements.fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.addd.measurements.R
import java.util.*


/**
 * Created by addd on 03.12.2017.
 */
class MeasurementsFragment : Fragment() {
    private var dayOfMonth: Int = 0
    private var month: Int = 0
    private var year: Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(R.layout.measurements_fragment, container, false)
        getTodayDate()

        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottomNavigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.today -> {
                   Toast.makeText(activity.applicationContext, "Сегодня $dayOfMonth $month $year", Toast.LENGTH_SHORT).show()
                }
                R.id.tomorrow -> {
                    Toast.makeText(activity.applicationContext, "Завтра ${dayOfMonth+1} $month $year", Toast.LENGTH_SHORT).show()
                }
                R.id.date -> {
                    val myCallBack = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                        Toast.makeText(activity.applicationContext, "Today is $dayOfMonth/$monthOfYear/$year", Toast.LENGTH_SHORT).show()

                    }
                    val datePikerDialog: DatePickerDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        DatePickerDialog(context, myCallBack, year, month, dayOfMonth)
                    } else {
                        TODO("VERSION.SDK_INT < N")
                    }
                    datePikerDialog.show()
                }
            }
            true
        }

        return view


    }

    private fun getTodayDate() {
        val calendar = Calendar.getInstance()
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
    }

}
