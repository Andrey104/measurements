package ru.nextf.measurements.adapters

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.nextf.measurements.*
import ru.nextf.measurements.activity.OnePhotoActivity
import ru.nextf.measurements.modelAPI.MeasurementPhoto
import java.util.*


/**
 * Created by left0ver on 27.03.18.
 */
class HorizontalAdapter(context: Context,
                        measurementPhotos: LinkedList<MeasurementPhoto>,
                        private val listener: CustomAdapterCallback) : RecyclerView.Adapter<HorizontalAdapter.MyViewHolder>() {
    private var mMeasurementPhotos: LinkedList<MeasurementPhoto>? = measurementPhotos
    private var mContext: Context? = context

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val context = viewGroup.context
        val inflater = LayoutInflater.from(context)
        val photoView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_horizontal, viewGroup, false)
        return MyViewHolder(photoView, listener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val imageView = holder.mPhotoImageView
        if (position == 0) {
            imageView.setImageResource(R.drawable.add_photo)
            imageView.maxWidth = 50
            imageView.maxHeight = 50
        } else {
            val spacePhoto = mMeasurementPhotos?.get(position)

            Glide.with(mContext)
                    .load(spacePhoto?.getUrl())
                    .placeholder(ru.nextf.measurements.R.drawable.ic_crop_original_black_24dp)
                    .into(imageView)
        }
    }

    override fun getItemCount(): Int {
        return mMeasurementPhotos?.size ?: 0
    }

    inner class MyViewHolder(itemView: View, listener: CustomAdapterCallback) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var mPhotoImageView: ImageView
        private val listener: CustomAdapterCallback

        init {
            this.listener = listener
            mPhotoImageView = itemView.findViewById(ru.nextf.measurements.R.id.image_horizontal) as ImageView
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
                        val position = adapterPosition
            if (position != RecyclerView.NO_POSITION && position > 0) {
                listener.OnPhotoCLick(adapterPosition)
            }
            if (position != RecyclerView.NO_POSITION && position == 0) {
                listener.onFirstItemClick(adapterPosition)
            }
        }
    }

    interface CustomAdapterCallback {
        fun OnPhotoCLick(pos: Int)
        fun onFirstItemClick(pos: Int)
    }
}
