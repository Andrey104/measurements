package com.addd.measurements.adapters

import android.content.Context
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Deal

/**
 * Created by addd on 25.12.2017.
 */
class DealAdapter(notesList: ArrayList<Deal>, private val listener: CustomAdapterCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val viewHolder = holder as ViewHolder
                var deal = mDealList[position]
                viewHolder.deal.text = String.format("%05d", deal.id)
                viewHolder.address.text = deal.address
                viewHolder.timeMount.text = "Что-то про монтаж"

                viewHolder.symbol.text = deal.company?.symbol

                if (viewHolder.symbol.text.length == 1) {
                    viewHolder.symbol.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30F)
                }
                setColorCompany(deal.company?.id ?: 0, viewHolder)
            }
            LOADING -> {
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == ITEM) {
            v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_deal, viewGroup, false)
            ViewHolder(v, listener)
        } else {
            v = LayoutInflater.from(viewGroup.context).inflate(R.layout.progressbar_item, viewGroup, false)
            LoadingVH(v)
        }
    }

    private var mDealList: List<Deal> = notesList
    private val ITEM = 0
    private val LOADING = 1
    private var isLoadingAdded = false

    fun isEmpty() = mDealList.isEmpty()

    override fun getItemCount(): Int {
        return if (mDealList == null) 0 else mDealList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mDealList.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    fun add(mc: Deal) {
        (mDealList as ArrayList<Deal>).add(mc)
        notifyItemInserted(mDealList.size - 1)
    }

    fun addAll(mcList: List<Deal>) {
        for (deal in mcList) {
            add(deal)
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Deal())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = mDealList.size - 1
        val item = getItem(position)

        if (item != null) {
            (mDealList as ArrayList<Deal>).removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): Deal? {
        return mDealList[position]
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

    class ViewHolder : RecyclerView.ViewHolder, View.OnClickListener {
        private val listener: DealAdapter.CustomAdapterCallback
        override fun onClick(v: View?) {
            listener.onItemClick(adapterPosition)
        }

        var address: TextView
        var deal: TextView
        var timeMount: TextView
        var symbol : TextView

        constructor(itemView: View, listener: DealAdapter.CustomAdapterCallback) : super(itemView) {
            deal = itemView.findViewById(R.id.deal)
            address = itemView.findViewById(R.id.address)
            timeMount = itemView.findViewById(R.id.time)
            symbol = itemView.findViewById(R.id.symbol)
            this.listener = listener
            this.itemView.setOnClickListener(this)
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
    }
}