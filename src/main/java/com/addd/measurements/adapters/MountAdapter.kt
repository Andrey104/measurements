package com.addd.measurements.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.addd.measurements.MyApp
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Mount

/**
 * Created by addd on 11.01.2018.
 */
class MountAdapter(notesList: List<Mount>, private val listener: CustomAdapterCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mountList: List<Mount> = notesList

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val mount = mountList[position]
        val viewHolder = holder as ViewHolder


        when (mount.status) {
            0 -> viewHolder.textViewMountStatus.text = MyApp.instance.getString(R.string.not_processed)
            1 -> viewHolder.textViewMountStatus.text = MyApp.instance.getString(R.string.stage_added)
            2 -> viewHolder.textViewMountStatus.text = MyApp.instance.getString(R.string.closed_successful)
            3 -> viewHolder.textViewMountStatus.text =MyApp.instance.getString(R.string.closed_not_successful)
            else -> viewHolder.textViewMountStatus.text = MyApp.instance.getString(R.string.not_processed)
        }

        if (mount.date == null) {
            viewHolder.textViewMountDate.text = MyApp.instance.getString(R.string.mount_for_phone)
        } else {
            val strBuilder = StringBuilder(mount.date)
            strBuilder.replace(10, 11, " ")
            strBuilder.delete(16, strBuilder.length)
            val newStrBuilder = StringBuilder()
            newStrBuilder.append(" ")
            for (i in 0..10) {
                newStrBuilder.append(strBuilder[i])
            }
            viewHolder.textViewMountDate.text = newStrBuilder.toString()
        }
    }


    override fun getItemCount(): Int {
        return mountList.size
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MountAdapter.ViewHolder? {
        var v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_mount, viewGroup, false)
        return ViewHolder(v, listener)
    }


    class ViewHolder(itemView: View, listener: CustomAdapterCallback) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(v: View?) {
            listener.onItemClick(adapterPosition)
        }

        private val listener: CustomAdapterCallback
        var textViewMountDate: TextView
        var textViewMountStatus: TextView

        init {
            textViewMountDate = itemView.findViewById(R.id.textViewStageDate)
            textViewMountStatus = itemView.findViewById(R.id.textViewMountStatus)
            this.listener = listener
            this.itemView.setOnClickListener(this)
        }
    }

    interface CustomAdapterCallback {
        fun onItemClick(pos: Int)
    }
}