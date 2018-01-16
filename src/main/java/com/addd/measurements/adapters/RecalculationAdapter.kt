package com.addd.measurements.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.addd.measurements.MyApp
import com.addd.measurements.R
import com.addd.measurements.formatDateTime
import com.addd.measurements.modelAPI.Recalculation

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
        viewHolder.textViewSum.text = "${recalculation.before} -> ${recalculation.after}"
        viewHolder.textViewComment.text = recalculation.comment

        viewHolder.textViewDate.text = formatDateTime(recalculation.autoDate ?: "")
    }


    override fun getItemCount(): Int {
        return mRecalculationsList.size
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecalculationAdapter.ViewHolder? {
        var v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_recalculation, viewGroup, false)
        return ViewHolder(v)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewFio: TextView
        var textViewDate: TextView
        var textViewSum: TextView
        var textViewComment: TextView

        init {
            textViewFio = itemView.findViewById(R.id.textViewFio)
            textViewDate = itemView.findViewById(R.id.textViewDateR)
            textViewSum = itemView.findViewById(R.id.textViewSumR)
            textViewComment = itemView.findViewById(R.id.textViewCommentR)
        }
    }
}