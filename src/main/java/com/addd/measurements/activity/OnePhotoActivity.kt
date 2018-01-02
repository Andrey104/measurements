package com.addd.measurements.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.addd.measurements.modelAPI.MeasurementPhoto
import com.addd.measurements.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class OnePhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_photo)

        val mImageView = findViewById<View>(R.id.image) as ImageView
        val spacePhoto = intent.getParcelableExtra<MeasurementPhoto>("MEASUREMENT_PHOTO")

        Glide.with(this)
                .load(spacePhoto.getUrl())
                .asBitmap()
                .error(R.drawable.ic_crop_original_black_24dp)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImageView)
    }

}
