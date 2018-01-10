package com.addd.measurements.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.addd.measurements.MyApp
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Action

/**
 * Created by addd on 10.01.2018.
 */
class ActionAdapter(notesList: ArrayList<Action>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mActionsList: List<Action> = notesList.reversed()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val action = mActionsList[position]
        val viewHolder = holder as ViewHolder
        viewHolder.status.text = when (action.type) {
            0 -> MyApp.instance.getString(R.string.reject_deal)
            1 -> MyApp.instance.getString(R.string.measurement_added)
            2 -> MyApp.instance.getString(R.string.mount_added)
            3 -> MyApp.instance.getString(R.string.successful_reject)
            4 -> MyApp.instance.getString(R.string.added_client)
            5 -> MyApp.instance.getString(R.string.change_manager)
            6 -> MyApp.instance.getString(R.string.add_discussion)
            else -> "Статус"
        }
        val strBuilder = StringBuilder(action.autoDate)
        strBuilder.replace(10,11," ")
        strBuilder.delete(16,strBuilder.length)
        val newStrBuilder = StringBuilder()
        for (i in 11..15) {
            newStrBuilder.append(strBuilder[i])
        }
        newStrBuilder.append(" ")
        for (i in 0..10) {
            newStrBuilder.append(strBuilder[i])
        }
        viewHolder.timeAction.text =newStrBuilder.toString()
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

        init {
            status = itemView.findViewById(R.id.status_deal)
            timeAction = itemView.findViewById(R.id.timeAction)
        }
    }
}