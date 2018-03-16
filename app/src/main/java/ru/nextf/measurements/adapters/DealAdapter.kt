package ru.nextf.measurements.adapters

import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import ru.nextf.measurements.modelAPI.Deal

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
                viewHolder.timeMount.text = when (deal.status) {
                    0 -> ru.nextf.measurements.MyApp.instance.getString(ru.nextf.measurements.R.string.in_treatment)
                    1 -> ru.nextf.measurements.MyApp.instance.getString(ru.nextf.measurements.R.string.measurement)
                    2 -> ru.nextf.measurements.MyApp.instance.getString(ru.nextf.measurements.R.string.not_contract)
                    3 -> ru.nextf.measurements.MyApp.instance.getString(ru.nextf.measurements.R.string.mount)
                    4 -> ru.nextf.measurements.MyApp.instance.getString(ru.nextf.measurements.R.string.deal_complete)
                    5 -> ru.nextf.measurements.MyApp.instance.getString(ru.nextf.measurements.R.string.deal_reject)
                    else -> ru.nextf.measurements.MyApp.instance.getString(ru.nextf.measurements.R.string.status_deal)
                }

                val mp = ResourcesCompat.getDrawable(ru.nextf.measurements.MyApp.instance.resources, ru.nextf.measurements.R.drawable.mp, null)
                val n = ResourcesCompat.getDrawable(ru.nextf.measurements.MyApp.instance.resources, ru.nextf.measurements.R.drawable.n, null)
                val b = ResourcesCompat.getDrawable(ru.nextf.measurements.MyApp.instance.resources, ru.nextf.measurements.R.drawable.b, null)
                val unknown = ResourcesCompat.getDrawable(ru.nextf.measurements.MyApp.instance.resources, ru.nextf.measurements.R.drawable.unknown, null)

                when (deal.company?.symbol) {
                    "МП", "MP" -> viewHolder.symbol.background = mp
                    "Б", "B" -> viewHolder.symbol.background = b
                    "Н", "H" -> viewHolder.symbol.background = n
                    else -> viewHolder.symbol.background = unknown
                }
            }
            LOADING -> {
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == ITEM) {
            v = LayoutInflater.from(viewGroup.context).inflate(ru.nextf.measurements.R.layout.list_item_deal, viewGroup, false)
            ViewHolder(v, listener)
        } else {
            v = LayoutInflater.from(viewGroup.context).inflate(ru.nextf.measurements.R.layout.progressbar_item, viewGroup, false)
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


    class ViewHolder : RecyclerView.ViewHolder, View.OnClickListener {
        private val listener: DealAdapter.CustomAdapterCallback
        override fun onClick(v: View?) {
            listener.onItemClick(adapterPosition)
        }

        var address: TextView
        var deal: TextView
        var timeMount: TextView
        var symbol : ImageView

        constructor(itemView: View, listener: DealAdapter.CustomAdapterCallback) : super(itemView) {
            deal = itemView.findViewById(ru.nextf.measurements.R.id.deal)
            address = itemView.findViewById(ru.nextf.measurements.R.id.address)
            timeMount = itemView.findViewById(ru.nextf.measurements.R.id.time)
            symbol = itemView.findViewById(ru.nextf.measurements.R.id.imageViewSymbol)
            this.listener = listener
            this.itemView.setOnClickListener(this)
        }

    }

    protected inner class LoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val proressBar: ProgressBar

        init {
            proressBar = itemView.findViewById(ru.nextf.measurements.R.id.progressBar1)
        }
    }

    interface CustomAdapterCallback {
        fun onItemClick(pos: Int)
    }
}