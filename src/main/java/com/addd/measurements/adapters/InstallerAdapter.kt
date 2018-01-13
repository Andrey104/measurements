package com.addd.measurements.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Installers

/**
 * Created by addd on 12.01.2018.
 */
class InstallerAdapter (notesList: List<Installers>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var installerList: List<Installers> = notesList

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val installer = installerList[position]
        val viewHolder = holder as ViewHolder

        viewHolder.textViewNameInstaller.text = installer.installer?.fio

    }


    override fun getItemCount(): Int {
        return installerList.size
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): InstallerAdapter.ViewHolder? {
        var v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_installer, viewGroup, false)
        return ViewHolder(v)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewNameInstaller: TextView

        init {
            textViewNameInstaller = itemView.findViewById(R.id.textViewNameInstaller)
        }
    }

    interface CustomAdapterCallback {
        fun onItemClick(pos: Int)
    }
}