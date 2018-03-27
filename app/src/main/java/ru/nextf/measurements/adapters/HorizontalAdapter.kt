package ru.nextf.measurements.adapters

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import ru.nextf.measurements.R
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.nextf.measurements.CHECK
import ru.nextf.measurements.MEASUREMENT_PHOTO
import ru.nextf.measurements.activity.OnePhotoActivity
import ru.nextf.measurements.modelAPI.MeasurementPhoto


/**
 * Created by left0ver on 27.03.18.
 */
class HorizontalAdapter(context: Context, measurementPhotos: ArrayList<MeasurementPhoto>) : RecyclerView.Adapter<HorizontalAdapter.MyViewHolder>() {
    private var mMeasurementPhotos: ArrayList<MeasurementPhoto>? = measurementPhotos
    private var mContext: Context? = context

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val context = viewGroup.context
        val inflater = LayoutInflater.from(context)
        val photoView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_horizontal, viewGroup, false)
        return MyViewHolder(photoView)
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

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var mPhotoImageView: ImageView

        init {
            mPhotoImageView = itemView.findViewById(ru.nextf.measurements.R.id.image_horizontal) as ImageView
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {

            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val spacePhoto = mMeasurementPhotos?.get(position)
                val intent = Intent(mContext, OnePhotoActivity::class.java)
                intent.putExtra(MEASUREMENT_PHOTO, spacePhoto)
                intent.putExtra(CHECK, position + 1)
                ContextCompat.startActivity(mContext, intent, null)
            }
        }
    }
}
