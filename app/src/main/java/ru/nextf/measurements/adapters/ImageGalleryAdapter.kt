package ru.nextf.measurements.adapters

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ru.nextf.measurements.CHECK
import ru.nextf.measurements.MEASUREMENT_PHOTO
import ru.nextf.measurements.activity.OnePhotoActivity
import ru.nextf.measurements.modelAPI.MeasurementPhoto
import com.bumptech.glide.Glide

/**
 * Created by addd on 26.12.2017.
 */
class ImageGalleryAdapter(context: Context, measurementPhotos: ArrayList<MeasurementPhoto>, private val listener: CustomAdapterCallback) : RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>() {
    private var mMeasurementPhotos: ArrayList<MeasurementPhoto>? = measurementPhotos
    private var mContext: Context? = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(ru.nextf.measurements.R.layout.list_item_photo, parent, false)
        return MyViewHolder(photoView, listener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val spacePhoto = mMeasurementPhotos?.get(position)
        val photo = mMeasurementPhotos?.get(position)
        val imageView = holder.mPhotoImageView

        Glide.with(mContext)
                .load(spacePhoto?.getUrl())
                .placeholder(ru.nextf.measurements.R.drawable.ic_crop_original_black_24dp)
                .into(imageView)
    }

    override fun getItemCount(): Int {
        return mMeasurementPhotos?.size ?: 0
    }

    class MyViewHolder : RecyclerView.ViewHolder,
            View.OnClickListener, View.OnLongClickListener {

        override fun onLongClick(v: View?): Boolean {
            listener.onItemLongClick(adapterPosition)
            return true
        }

        var mPhotoImageView: ImageView
        private val listener: CustomAdapterCallback

        constructor(itemView: View, listener: CustomAdapterCallback) : super(itemView) {
            this.listener = listener
            mPhotoImageView = itemView.findViewById(ru.nextf.measurements.R.id.iv_photo) as ImageView
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            listener.onItemClick(adapterPosition)
        }

    }

    interface CustomAdapterCallback {
        fun onItemClick(pos: Int)
        fun onItemLongClick(pos: Int)
    }
}