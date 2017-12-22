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
import com.addd.measurements.MyApp
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.modelAPI.MyProblem

/**
 * Created by addd on 22.12.2017.
 */
class ProblemAdapter(notesList: ArrayList<MyProblem>, private val listener: CustomAdapterCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mProblemList: List<MyProblem> = notesList
    private val ITEM = 0
    private val LOADING = 1
    private var isLoadingAdded = false


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val viewHolder = holder as ViewHolder
                var problem = mProblemList[position]
                viewHolder.deal.text = String.format("Сделка %05d", problem.deal)
                viewHolder.headerText.text = problem.title
                viewHolder.addressText.text = problem.address ?: MyApp.instance.getString(R.string.address_not_set)
                if (problem.status == 0) {
                    viewHolder.imageView.setImageResource(R.drawable.clock)
                } else {
                    viewHolder.imageView.setImageResource(R.drawable.check_mark_black)
                }
            }
            LOADING -> {
            }
        }
    }


    fun isEmpty() = mProblemList.isEmpty()


    /**
     * Создание новых View и ViewHolder элемента списка, которые впоследствии могут переиспользоваться.
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == ITEM) {
            v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_problem, viewGroup, false)
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
        return if (mProblemList == null) 0 else mProblemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mProblemList.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    fun add(mc: MyProblem) {
        (mProblemList as ArrayList<MyProblem>).add(mc)
        notifyItemInserted(mProblemList.size - 1)
    }

    fun addAll(mcList: List<MyProblem>) {
        for (problem in mcList) {
            add(problem)
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(MyProblem(0, "","0", "0", 0, "0", "0", 0, emptyList()))
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = mProblemList.size - 1
        val item = getItem(position)

        if (item != null) {
            (mProblemList as ArrayList<Measurement>).removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): MyProblem? {
        return mProblemList[position]
    }

    class ViewHolder : RecyclerView.ViewHolder, View.OnClickListener {
        private val listener: CustomAdapterCallback
        override fun onClick(v: View?) {
            listener.onItemClick(adapterPosition)
        }

        var headerText: TextView
        var deal: TextView
        var addressText: TextView
        var imageView : ImageView

        constructor(itemView: View, listener: CustomAdapterCallback) : super(itemView) {
            deal = itemView.findViewById(R.id.dealText)
            addressText = itemView.findViewById(R.id.addressText)
            headerText = itemView.findViewById(R.id.headerText)
            imageView = itemView.findViewById(R.id.imageViewProblem)
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