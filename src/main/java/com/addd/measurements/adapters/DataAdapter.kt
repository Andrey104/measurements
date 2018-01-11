package com.addd.measurements.adapters

import android.content.Context
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Measurement
import kotlin.collections.ArrayList


/**
 * Created by addd on 07.12.2017.
 */

class DataAdapter(notesList: ArrayList<Measurement>, private val listener: CustomAdapterCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mNotesList: List<Measurement> = notesList
    private val ITEM = 0
    private val LOADING = 1
    private var isLoadingAdded = false

    fun isEmpty() = mNotesList.isEmpty()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val viewHolder = holder as ViewHolder
                var measurement = mNotesList[position]
                val id = measurement.id
                viewHolder.deal.text = String.format("%05d", measurement.deal)
                viewHolder.address.text = measurement.address
                viewHolder.time.text = measurement.time

                if (measurement.worker == null) {
                    viewHolder.workerName.text = "Не распределено"
                } else {
                    viewHolder.workerName.text = measurement.worker?.firstName + measurement.worker?.lastName
                }
                setColorResponsible(measurement.color ?: 0, viewHolder)

                viewHolder.symbol.text = measurement.company?.symbol

                if (viewHolder.symbol.text.length == 1) {
                    viewHolder.symbol.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30F)
                }
                setColorCompany(measurement.company?.id ?: 0, viewHolder)
            }
            LOADING -> {
            }
        }
    }
    /**
     * Создание новых View и ViewHolder элемента списка, которые впоследствии могут переиспользоваться.
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == ITEM) {
            v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_measurement, viewGroup, false)
            ViewHolder(v, listener)
        } else {
            v = LayoutInflater.from(viewGroup.context).inflate(R.layout.progressbar_item, viewGroup, false)
            LoadingVH(v)
        }
    }

    /**
     * Заполнение виджетов View данными из элемента списка с номером i
     */
    override fun getItemCount(): Int {
        return if (mNotesList == null) 0 else mNotesList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mNotesList.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    fun add(mc: Measurement) {
        (mNotesList as ArrayList<Measurement>).add(mc)
        notifyItemInserted(mNotesList.size - 1)
    }

    fun addAll(mcList: List<Measurement>) {
        for (measurement in mcList) {
            add(measurement)
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Measurement())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = mNotesList.size - 1
        val item = getItem(position)

        if (item != null) {
            (mNotesList as ArrayList<Measurement>).removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): Measurement? {
        return mNotesList[position]
    }

    private fun setColorResponsible(color: Int, viewHolder: ViewHolder) {
        selectColorVersion(viewHolder.workerName, when (color) {
            1 -> R.color.red
            2 -> R.color.green
            3 -> R.color.blue
            else -> R.color.blue
        }, viewHolder.itemView.context)

    }

    private fun setColorCompany(color: Int, viewHolder: ViewHolder) {
        selectColorVersion(viewHolder.symbol, when (color) {
            1 -> R.color.green
            2 ->R.color.orange
            3 ->  R.color.blue
            else -> R.color.blue
        }, viewHolder.itemView.context)

    }

    private fun selectColorVersion(item: TextView, color: Int, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            item.setTextColor(context.resources.getColor(color, context.theme))
        } else {
            item.setTextColor(context.resources.getColor(color))
        }
    }

    /**
     * Реализация класса ViewHolder, хранящего ссылки на виджеты.
     */

    class ViewHolder : RecyclerView.ViewHolder, View.OnClickListener, View.OnLongClickListener {
        override fun onLongClick(v: View?): Boolean {
            listener.onItemLongClick(adapterPosition)
            return true
        }

        private val listener: CustomAdapterCallback
        override fun onClick(v: View?) {
            listener.onItemClick(adapterPosition)
        }

        var symbol: TextView
        var deal: TextView
        var address: TextView
        var time: TextView
        var workerName: TextView

        constructor(itemView: View, listener: CustomAdapterCallback) : super(itemView) {
            deal = itemView.findViewById(R.id.deal)
            address = itemView.findViewById(R.id.address)
            time = itemView.findViewById(R.id.time)
            workerName = itemView.findViewById(R.id.worker_name)
            symbol = itemView.findViewById(R.id.symbol)
            this.listener = listener
            this.itemView.setOnClickListener(this)
            this.itemView.setOnLongClickListener(this)
        }

    }

    protected inner class LoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val proressBar: ProgressBar

        init {
            proressBar = itemView.findViewById(R.id.progressBar1)
        }
    }

    interface CustomAdapterCallback {
        fun onItemClick(pos: Int)
        fun onItemLongClick(pos: Int)
    }
}