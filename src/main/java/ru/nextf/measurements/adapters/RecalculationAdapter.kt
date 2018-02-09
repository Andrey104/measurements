package ru.nextf.measurements.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.nextf.measurements.formatDateTime
import ru.nextf.measurements.modelAPI.Recalculation

/**
 * Created by addd on 10.01.2018.
 */
class RecalculationAdapter(notesList: List<Recalculation>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mRecalculationsList: List<Recalculation> = notesList.reversed()

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val recalculation = mRecalculationsList[position]
        val viewHolder = holder as ViewHolder

        viewHolder.textViewFio.text = recalculation.user?.firstName + " " + recalculation.user?.lastName
        viewHolder.textViewSumBefore.text = "${recalculation.before}"
        viewHolder.textViewSumAfter.text = "${recalculation.after}"
        viewHolder.textViewComment.text = recalculation.comment

        var fullDate = formatDateTime(recalculation.autoDate ?: "")
        viewHolder.textViewTime.text = fullDate.substring(0, 5)
        viewHolder.textViewDate.text = fullDate.substring(6, fullDate.length - 1)
    }


    override fun getItemCount(): Int {
        return mRecalculationsList.size
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecalculationAdapter.ViewHolder? {
        var v = LayoutInflater.from(viewGroup.context).inflate(ru.nextf.measurements.R.layout.list_item_recalculation, viewGroup, false)
        return ViewHolder(v)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewFio: TextView
        var textViewDate: TextView
        var textViewTime: TextView
        var textViewSumBefore: TextView
        var textViewSumAfter: TextView
        var textViewComment: TextView

        init {
            textViewFio = itemView.findViewById(ru.nextf.measurements.R.id.textViewFio)
            textViewDate = itemView.findViewById(ru.nextf.measurements.R.id.textViewDateR)
            textViewTime = itemView.findViewById(ru.nextf.measurements.R.id.textViewTimeR)
            textViewSumBefore = itemView.findViewById(ru.nextf.measurements.R.id.textViewSumBefore)
            textViewSumAfter = itemView.findViewById(ru.nextf.measurements.R.id.textViewSumAfter)
            textViewComment = itemView.findViewById(ru.nextf.measurements.R.id.textViewCommentR)
        }
    }
}