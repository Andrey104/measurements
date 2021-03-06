package ru.nextf.measurements.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.nextf.measurements.modelAPI.Clients
import ru.nextf.measurements.modelAPI.Phone


/**
 * Created by addd on 13.12.2017.
 */
class ClientAdapter(notesList: List<Clients>, inflater: LayoutInflater, activity: Activity) : RecyclerView.Adapter<ClientAdapter.ViewHolder>() {
    private var mClientsList: List<Clients> = notesList
    private var mInflater: LayoutInflater = inflater
    private var activity: Activity = activity


    override fun getItemCount(): Int {
        return mClientsList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var client = mClientsList[position]
        holder.name.text = client.client?.fio.toString()
        val arrayList: ArrayList<Phone> = client.client?.phones as ArrayList<Phone>
        client.client?.phones?.let  {
            val phoneNumber = StringBuffer(it[0].number.toString())
            if (phoneNumber.length == 10) {
                phoneNumber.insert(8, '-')
                phoneNumber.insert(6, '-')
                phoneNumber.insert(3, ')')
                phoneNumber.insert(0, '(')
                phoneNumber.insert(0, '8')
            }
            holder.number.text = phoneNumber.toString()

        }
        holder.image.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${holder.number.text}")
            ru.nextf.measurements.MyApp.instance.startActivity(intent)
        }

        if (arrayList.size == 1) {
            holder.image2.visibility = View.GONE
        }

        holder.itemView.setOnClickListener({ v ->
            val builder = AlertDialog.Builder(ru.nextf.measurements.MyApp.instance)

            if (!arrayList.isEmpty()) {
                if (arrayList.size == 1) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${holder.number.text}")
                    ru.nextf.measurements.MyApp.instance.startActivity(intent)
                } else {
                    val builder = AlertDialog.Builder(activity)
                    val view = mInflater.inflate(ru.nextf.measurements.R.layout.dialog_phones, null)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        builder.setTitle(Html.fromHtml("<font color='#5c6b96'>${client.client?.fio}</font>", Html.FROM_HTML_MODE_LEGACY))
                    } else {
                        builder.setTitle(Html.fromHtml("<font color='#5c6b96'>${client.client?.fio}</font>"))
                    }
                    builder.setView(view)
                    val recycler = view.findViewById<RecyclerView>(ru.nextf.measurements.R.id.recyclerPhones)
                    recycler.adapter = PhonesAdapter(arrayList)
                    val layoutManager = LinearLayoutManager(ru.nextf.measurements.MyApp.instance, LinearLayoutManager.VERTICAL, false)
                    recycler.layoutManager = layoutManager
                    val alert = builder.create()
                    alert.show()
                }
            }
        })
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        var v = LayoutInflater.from(viewGroup.context).inflate(ru.nextf.measurements.R.layout.list_item_phone_client, viewGroup, false)
        return ViewHolder(v)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name: TextView
        var number: TextView
        var image: ImageView
        var image2: ImageView

        init {
            name = itemView.findViewById(ru.nextf.measurements.R.id.name)
            number = itemView.findViewById(ru.nextf.measurements.R.id.number)
            image = itemView.findViewById(ru.nextf.measurements.R.id.imageViewClient)
            image2 = itemView.findViewById(ru.nextf.measurements.R.id.imageView30)
        }


    }
}