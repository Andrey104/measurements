package com.addd.measurements.adapters

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.addd.measurements.MEASUREMENT_PHOTO
import com.addd.measurements.modelAPI.MeasurementPhoto
import com.addd.measurements.R
import com.addd.measurements.activity.OnePhotoActivity
import com.bumptech.glide.Glide

/**
 * Created by addd on 26.12.2017.
 */
class ImageGalleryAdapter(context: Context, measurementPhotos: ArrayList<MeasurementPhoto>) : RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>() {
    private var mMeasurementPhotos: ArrayList<MeasurementPhoto>? = measurementPhotos
    private var mContext: Context? = context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.list_item_photo, parent, false)
        return MyViewHolder(photoView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val spacePhoto = mMeasurementPhotos?.get(position)
        val photo = mMeasurementPhotos?.get(position)
        val imageView = holder.mPhotoImageView

        Glide.with(mContext)
                .load(spacePhoto?.getUrl())
                .placeholder(R.drawable.ic_crop_original_black_24dp)
                .into(imageView)
    }

    override fun getItemCount(): Int {
        return mMeasurementPhotos?.size ?: 0
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var mPhotoImageView: ImageView

        init {
            mPhotoImageView = itemView.findViewById(R.id.iv_photo) as ImageView
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {

            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val spacePhoto = mMeasurementPhotos?.get(position)
                val intent = Intent(mContext, OnePhotoActivity::class.java)
                intent.putExtra(MEASUREMENT_PHOTO, spacePhoto)
                ContextCompat.startActivity(mContext, intent, null)
            }
        }
    }
}