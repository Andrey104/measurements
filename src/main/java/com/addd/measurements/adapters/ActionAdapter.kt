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
import com.addd.measurements.modelAPI.Action

/**
 * Created by addd on 10.01.2018.
 */
class ActionAdapter(notesList: ArrayList<Action>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mActionsList: List<Action> = notesList.reversed()

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val action = mActionsList[position]
        val viewHolder = holder as ViewHolder
        viewHolder.status.text = action.action
        if (action.user?.firstName.isNullOrEmpty() && action.user?.lastName.isNullOrEmpty()) {
            viewHolder.textViewFio.text = MyApp.instance.getString(R.string.without_name)
        } else {
            viewHolder.textViewFio.text = action.user?.firstName + " " + action.user?.lastName
        }
        viewHolder.textViewCommentAction.text = action.comment

        viewHolder.timeAction.text = formatDateTime(action.autoDate ?: "")
    }


    override fun getItemCount(): Int {
        return mActionsList.size
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ActionAdapter.ViewHolder? {
        var v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_action, viewGroup, false)
        return ViewHolder(v)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var status: TextView
        var timeAction: TextView
        var textViewFio: TextView
        var textViewCommentAction: TextView

        init {
            status = itemView.findViewById(R.id.status_deal)
            timeAction = itemView.findViewById(R.id.timeAction)
            textViewFio = itemView.findViewById(R.id.textViewFioAction)
            textViewCommentAction = itemView.findViewById(R.id.textViewCommentAction)
        }
    }
}