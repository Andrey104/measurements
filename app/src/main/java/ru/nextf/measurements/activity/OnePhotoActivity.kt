package ru.nextf.measurements.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import ru.nextf.measurements.CHECK
import ru.nextf.measurements.MEASUREMENT_PHOTO
import ru.nextf.measurements.modelAPI.MeasurementPhoto
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_one_photo.*

class OnePhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ru.nextf.measurements.R.layout.activity_one_photo)
        setSupportActionBar(toolbarAst)
        toolbarAst.setNavigationIcon(ru.nextf.measurements.R.drawable.ic_arrow_back_white_24dp)
        toolbarAst.setNavigationOnClickListener {
            finish()
        }
        title = "Фото ${intent.getIntExtra(CHECK, 1)}"

        val mImageView = findViewById<View>(ru.nextf.measurements.R.id.image) as com.github.chrisbanes.photoview.PhotoView
        val spacePhoto = intent.getParcelableExtra<MeasurementPhoto>(MEASUREMENT_PHOTO)

        Glide.with(this)
                .load(spacePhoto.getUrl())
                .asBitmap()
                .error(ru.nextf.measurements.R.drawable.ic_crop_original_black_24dp)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImageView)
    }

}
