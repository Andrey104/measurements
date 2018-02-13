package ru.nextf.measurements.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.nextf.measurements.formatDateTime
import ru.nextf.measurements.modelAPI.Action

/**
 * Created by addd on 10.01.2018.
 */
class ActionAdapter(notesList: ArrayList<Action>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mActionsList: List<Action> = notesList

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val action = mActionsList[position]
        val viewHolder = holder as ViewHolder
        viewHolder.status.text = action.action
        if (action.user?.firstName.isNullOrEmpty() && action.user?.lastName.isNullOrEmpty()) {
            viewHolder.textViewFio.text = ru.nextf.measurements.MyApp.instance.getString(ru.nextf.measurements.R.string.without_name)
        } else {
            viewHolder.textViewFio.text = action.user?.firstName + " " + action.user?.lastName
        }
        if (action.comment.isNullOrEmpty()) {
            viewHolder.textViewCommentAction.visibility = View.GONE
        } else {
            viewHolder.textViewCommentAction.text = action.comment
        }
        var fullDate = formatDateTime(action.autoDate ?: "")
        viewHolder.timeAction.text = fullDate.substring(0, 5)
        viewHolder.dateAction.text = fullDate.substring(6, fullDate.length)
    }


    override fun getItemCount(): Int {
        return mActionsList.size
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ActionAdapter.ViewHolder? {
        var v = LayoutInflater.from(viewGroup.context).inflate(ru.nextf.measurements.R.layout.list_item_action, viewGroup, false)
        return ViewHolder(v)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var status: TextView
        var timeAction: TextView
        var dateAction: TextView
        var textViewFio: TextView
        var textViewCommentAction: TextView

        init {
            status = itemView.findViewById(ru.nextf.measurements.R.id.status_deal)
            timeAction = itemView.findViewById(ru.nextf.measurements.R.id.timeAction)
            dateAction = itemView.findViewById(ru.nextf.measurements.R.id.dateAction)
            textViewFio = itemView.findViewById(ru.nextf.measurements.R.id.textViewFioAction)
            textViewCommentAction = itemView.findViewById(ru.nextf.measurements.R.id.textViewCommentAction)
        }
    }
}