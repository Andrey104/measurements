package com.addd.measurements.adapters

import android.content.Intent
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.addd.measurements.R
import com.addd.measurements.activity.OneMeasurementActivity
import com.addd.measurements.modelAPI.Measurement
import java.util.*


/**
 * Created by addd on 07.12.2017.
 */

class DataAdapter : RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private var mNotesList: List<Measurement> = ArrayList()

    constructor(notesList: List<Measurement>) {
        mNotesList = notesList
    }

    /**
     * Создание новых View и ViewHolder элемента списка, которые впоследствии могут переиспользоваться.
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        var v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_measurement, viewGroup, false)
        return ViewHolder(v)
    }

    /**
     * Заполнение виджетов View данными из элемента списка с номером i
     */
    override
    fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        var measurement = mNotesList[i]
        when (measurement.deal.toString().length) {
            1 -> viewHolder.deal.text = "0000" + measurement.deal.toString()
            2 -> viewHolder.deal.text = "000" + measurement.deal.toString()
            3 -> viewHolder.deal.text = "00" + measurement.deal.toString()
            4 -> viewHolder.deal.text = "0" + measurement.deal.toString()
        }

        viewHolder.address.text = measurement.address
        viewHolder.time.text = measurement.time

        if (measurement.workerName == null) {
            viewHolder.workerName.text = "Не распределено"
        } else {
            viewHolder.workerName.text = measurement.workerName.toString()
        }
        when (measurement.color) {
            1 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                viewHolder.workerName.setTextColor(viewHolder.itemView.resources.getColor(R.color.red, viewHolder.itemView.context.theme))
            } else {
                viewHolder.workerName.setTextColor(viewHolder.itemView.resources.getColor(R.color.red))
            }
            2 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                viewHolder.workerName.setTextColor(viewHolder.itemView.resources.getColor(R.color.green, viewHolder.itemView.context.theme))
            } else {
                viewHolder.workerName.setTextColor(viewHolder.itemView.resources.getColor(R.color.green))
            }
            3 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                viewHolder.workerName.setTextColor(viewHolder.itemView.resources.getColor(R.color.blue, viewHolder.itemView.context.theme))
            } else {
                viewHolder.workerName.setTextColor(viewHolder.itemView.resources.getColor(R.color.blue))
            }
        }

        viewHolder.symbol.text = measurement.company!!.symbol

        if (viewHolder.symbol.text.length == 1) {
            viewHolder.symbol.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30F)
        }
        when (measurement.company!!.id) {
            1 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                viewHolder.symbol.setTextColor(viewHolder.itemView.resources.getColor(R.color.green, viewHolder.itemView.context.theme))
            } else {
                viewHolder.symbol.setTextColor(viewHolder.itemView.resources.getColor(R.color.green))
            }
            2 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                viewHolder.symbol.setTextColor(viewHolder.itemView.resources.getColor(R.color.orange, viewHolder.itemView.context.theme))
            } else {
                viewHolder.symbol.setTextColor(viewHolder.itemView.resources.getColor(R.color.orange))
            }
            3 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                viewHolder.symbol.setTextColor(viewHolder.itemView.resources.getColor(R.color.blue, viewHolder.itemView.context.theme))
            } else {
                viewHolder.symbol.setTextColor(viewHolder.itemView.resources.getColor(R.color.blue))
            }
        }

        viewHolder.itemView.setOnClickListener({ v ->
            val intent = Intent(v.context, OneMeasurementActivity::class.java)
            var id : String = viewHolder.deal.text.toString()
            while (id.startsWith("0")) {
                id = id.substring(1)
            }
            intent.putExtra("id", id)
            intent.putExtra("symbol", viewHolder.symbol.text.length.toString())
            v.context.startActivity(intent)
//            Toast.makeText(v.context, id, Toast.LENGTH_SHORT).show()
        })



    }


    override fun getItemCount(): Int {
        return mNotesList.size
    }

    /**
     * Реализация класса ViewHolder, хранящего ссылки на виджеты.
     */

    class ViewHolder : RecyclerView.ViewHolder {

        var symbol: TextView
        var deal: TextView
        var address: TextView
        var time: TextView
        var workerName: TextView

        constructor(itemView: View) : super(itemView) {
            deal = itemView.findViewById(R.id.deal)
            address = itemView.findViewById(R.id.address)
            time = itemView.findViewById(R.id.time)
            workerName = itemView.findViewById(R.id.worker_name)
            symbol = itemView.findViewById(R.id.symbol)
        }

    }
}