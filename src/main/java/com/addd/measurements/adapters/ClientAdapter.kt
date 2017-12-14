package com.addd.measurements.adapters

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Clients


/**
 * Created by addd on 13.12.2017.
 */
class ClientAdapter : RecyclerView.Adapter<ClientAdapter.ViewHolder> {
    private var mClientsList: List<Clients> = ArrayList()

    constructor(notesList: List<Clients>) {
        mClientsList = notesList
    }


    override fun getItemCount(): Int {
        return mClientsList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var client = mClientsList[position]
        holder.name.text = client.client!!.fio.toString()
        val arrayList: ArrayList<String> = ArrayList()
        if (client!!.client!!.phones != null) {
            val linearLayout = holder.itemView.findViewById<LinearLayout>(R.id.linearLayoutPhone)
            var textView: TextView
            for (phone in client!!.client!!.phones!!) {
                textView = TextView(holder.itemView.context)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16F)
                val phoneNumber = StringBuffer(phone.number.toString())
                phoneNumber.insert(8, '-')
                phoneNumber.insert(6, '-')
                phoneNumber.insert(3, ')')
                phoneNumber.insert(0, '(')
                phoneNumber.insert(0, '8')

                textView.text = phoneNumber.toString()
                linearLayout.addView(textView)
                arrayList.add(phoneNumber.toString())
            }
        }
        holder.itemView.setOnClickListener({ v ->
            if (!arrayList.isEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL)
                if (arrayList.size == 1) {
                    intent.data = Uri.parse("tel:${arrayList[0]}")
                    v.context.startActivity(intent)
                } else {
                    val builder = AlertDialog.Builder(v.context);
                    builder.setTitle("Выберите номер")
                    var strings = Array(arrayList.size, init = { index -> arrayList[index] })
                    builder.setItems(strings, { dialog, which ->
                        intent.data = Uri.parse("tel:${arrayList[which]}")
                        v.context.startActivity(intent)
                    })
                    val alert = builder.create()
                    alert.show()
                }
            }
        })
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        var v = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_phone_client, viewGroup, false)
        return ViewHolder(v)
    }


    class ViewHolder : RecyclerView.ViewHolder {

        var name: TextView


        constructor(itemView: View) : super(itemView) {
            name = itemView.findViewById(R.id.name)
        }

    }
}