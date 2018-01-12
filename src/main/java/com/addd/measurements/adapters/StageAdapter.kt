package com.addd.measurements.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.addd.measurements.MyApp
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Stage

/**
 * Created by addd on 11.01.2018.
 */
class StageAdapter(notesList: List<Stage>, private val listener: CustomAdapterCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var stageList: List<Stage> = notesList

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val stage = stageList[position]
        val viewHolder = holder as ViewHolder


        viewHolder.textViewStageCount.text = (position + 1).toString()


        val strBuilder = StringBuilder(stage.date)
        strBuilder.replace(10, 11, " ")
        strBuilder.delete(16, strBuilder.length)
        val newStrBuilder = StringBuilder()
        for (i in 0..10) {
            newStrBuilder.append(strBuilder[i])
        }
        viewHolder.textViewStageDate.text = newStrBuilder.toString()

        when (stage.status) {
            1 -> viewHolder.textViewStageStatus.text = MyApp.instance.getString(R.string.stage_added)
            2 -> viewHolder.textViewStageStatus.text = MyApp.instance.getString(R.string.date_added)
            3 -> viewHolder.textViewStageStatus.text = MyApp.instance.getString(R.string.mount_close_successful)
            4 -> viewHolder.textViewStageStatus.text = MyApp.instance.getString(R.string.reject_stage)
            else -> viewHolder.textViewStageStatus.text = MyApp.instance.getString(R.string.status)
        }

    }


    override fun getItemCount(): Int {
        return stageList.size
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): StageAdapter.ViewHolder? {
        var v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_stage, viewGroup, false)
        return ViewHolder(v, listener)
    }


    class ViewHolder(itemView: View, listener: CustomAdapterCallback) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(v: View?) {
            listener.onItemClick(adapterPosition)
        }

        private val listener: CustomAdapterCallback
        var textViewStageDate: TextView
        var textViewStageStatus: TextView
        var textViewStageCount: TextView

        init {
            textViewStageDate = itemView.findViewById(R.id.textViewStageDate)
            textViewStageStatus = itemView.findViewById(R.id.textViewStageStatus)
            textViewStageCount = itemView.findViewById(R.id.textViewStageCount)
            this.listener = listener
            this.itemView.setOnClickListener(this)
        }
    }

    interface CustomAdapterCallback {
        fun onItemClick(pos: Int)
    }
}