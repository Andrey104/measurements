package ru.nextf.measurements.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import ru.nextf.measurements.modelAPI.MeasurementPhoto
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_one_photo.*
import ru.nextf.measurements.*
import ru.nextf.measurements.network.NetworkControllerPicture

class OnePhotoActivity : AppCompatActivity(), NetworkControllerPicture.UpdatePicturesDelete {
    private var idMeasurement = 0
    private var idPhoto = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ru.nextf.measurements.R.layout.activity_one_photo)
        setSupportActionBar(toolbarAst)
        toolbarAst.setNavigationIcon(ru.nextf.measurements.R.drawable.ic_arrow_back_white_24dp)
        toolbarAst.setNavigationOnClickListener {
            finish()
        }
        NetworkControllerPicture.registerUpdateDeleteCallback(this)
        title = "Фото ${intent.getIntExtra(CHECK, 1)}"

        idMeasurement = intent.getIntExtra(MEASUREMENT_ID_DELETE, 0)
        idPhoto = intent.getIntExtra(PHOTO_ID_DELETE, 0)

        val mImageView = findViewById<View>(ru.nextf.measurements.R.id.image) as com.github.chrisbanes.photoview.PhotoView
        val spacePhoto = intent.getParcelableExtra<MeasurementPhoto>(MEASUREMENT_PHOTO)

        Glide.with(this)
                .load(spacePhoto.getUrl())
                .asBitmap()
                .error(ru.nextf.measurements.R.drawable.ic_crop_original_black_24dp)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mImageView)
    }

    override fun resultUpdatePicDelete(result: Boolean) {
        if (result) {
            setResult(200)
            finish()
        } else {
            toast(R.string.error)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.delete_photo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == R.id.delete) {
            val ad = android.app.AlertDialog.Builder(this)
            ad.setTitle(getString(R.string.delete_photo))  // заголовок
            ad.setPositiveButton(ru.nextf.measurements.R.string.yes) { _, _ ->
                NetworkControllerPicture.deletePictureFile(idMeasurement.toString(), idPhoto.toString())
            }
            ad.setNegativeButton(ru.nextf.measurements.R.string.cancel) { _, _ -> }
            ad.setCancelable(true)
            ad.show()
        }
        return true
    }
}
