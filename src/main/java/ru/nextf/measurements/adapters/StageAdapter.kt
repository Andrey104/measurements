package ru.nextf.measurements.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.nextf.measurements.formatDate
import ru.nextf.measurements.modelAPI.Stage

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



        viewHolder.textViewStageDate.text = formatDate(stage.date ?: "2000-20-20")

        viewHolder.textViewStageStatus.text = if(stage.status == 0) ru.nextf.measurements.MyApp.instance.getString(ru.nextf.measurements.R.string.in_proccess) else ru.nextf.measurements.MyApp.instance.getString(ru.nextf.measurements.R.string.stage_completed)

    }


    override fun getItemCount(): Int {
        return stageList.size
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): StageAdapter.ViewHolder? {
        var v = LayoutInflater.from(viewGroup.context).inflate(ru.nextf.measurements.R.layout.list_item_stage, viewGroup, false)
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
            textViewStageDate = itemView.findViewById(ru.nextf.measurements.R.id.textViewStageDate)
            textViewStageStatus = itemView.findViewById(ru.nextf.measurements.R.id.textViewStageStatus)
            textViewStageCount = itemView.findViewById(ru.nextf.measurements.R.id.textViewStageCount)
            this.listener = listener
            this.itemView.setOnClickListener(this)
        }
    }

    interface CustomAdapterCallback {
        fun onItemClick(pos: Int)
    }
}