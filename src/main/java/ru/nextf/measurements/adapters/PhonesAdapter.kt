package ru.nextf.measurements.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.nextf.measurements.modelAPI.Phone

/**
 * Created by addd on 01.02.2018.
 */
class PhonesAdapter(notesList: List<Phone>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var stageList: List<Phone> = notesList

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val phone = stageList[position]
        val viewHolder = holder as ViewHolder

        val phoneNumber = StringBuffer(phone.number.toString())
        if (phoneNumber.length == 10) {
            phoneNumber.insert(8, '-')
            phoneNumber.insert(6, '-')
            phoneNumber.insert(3, ')')
            phoneNumber.insert(0, '(')
            phoneNumber.insert(0, '8')
        }
        viewHolder.textViewNumber.text = phoneNumber.toString()
        viewHolder.textViewComment.text = phone.comment

        viewHolder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${viewHolder.textViewNumber.text}")
            ru.nextf.measurements.MyApp.instance.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return stageList.size
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder? {
        var v = LayoutInflater.from(viewGroup.context).inflate(ru.nextf.measurements.R.layout.list_item_phones, viewGroup, false)
        return ViewHolder(v)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var textViewNumber: TextView
        var textViewComment: TextView

        init {
            textViewNumber = itemView.findViewById(ru.nextf.measurements.R.id.textViewNumber)
            textViewComment = itemView.findViewById(ru.nextf.measurements.R.id.textViewComment)
        }
    }

}
