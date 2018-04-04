package ru.nextf.measurements.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.nextf.measurements.adapters.ImageGalleryAdapter
import ru.nextf.measurements.*
import ru.nextf.measurements.network.NetworkControllerPicture
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.measurement_photo_fragment.view.*
import ru.nextf.measurements.activity.OnePhotoActivity
import ru.nextf.measurements.modelAPI.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by addd on 01.02.2018.
 */
class MeasurementPhotoFragment : Fragment(), ImageGalleryAdapter.CustomAdapterCallback {
    private val DELETE_PHOTO = 1212
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val REQUEST_CAMERA = 1
    private val REQUEST_GALERY = 2
    private lateinit var mView: View
    private var uriPhotoFile: Uri? = null
    var photoFile: File? = null
    var mCurrentPhotoPath = ""
    private lateinit var measurement: Measurement
    private var arrayPhoto = ArrayList<MeasurementPhoto>()
    private lateinit var recyclerPhotoList: RecyclerView
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater?.inflate(ru.nextf.measurements.R.layout.measurement_photo_fragment, container, false) ?: View(context)

        getSavedMeasurement()

        val layoutManager = GridLayoutManager(context, 2)
        recyclerPhotoList = mView.rv_images
        recyclerPhotoList.setHasFixedSize(true)
        recyclerPhotoList.layoutManager = layoutManager

        getPermission()

        displayPictures(measurement)

        mView.fabMain.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(ru.nextf.measurements.R.string.what_use)
                    .setCancelable(true)
                    .setPositiveButton(ru.nextf.measurements.R.string.new_photo)
                    { _, _ ->
                        getPhotoFromCamera()
                    }
                    .setNegativeButton(ru.nextf.measurements.R.string.from_gallery) { _, _ -> getPhotoFromGallery() }
            val alert = builder.create()
            alert.show()
        }

        return mView
    }

    override fun onItemClick(pos: Int) {
        if (pos != RecyclerView.NO_POSITION) {
            val spacePhoto = arrayPhoto[pos]
            val intent = Intent(context, OnePhotoActivity::class.java)
            intent.putExtra(MEASUREMENT_ID_DELETE, measurement.id)
            intent.putExtra(PHOTO_ID_DELETE, arrayPhoto[pos].getId())
            intent.putExtra(MEASUREMENT_PHOTO, spacePhoto)
            intent.putExtra(CHECK, pos + 1)
            activity.startActivityForResult(intent, DELETE_PHOTO)
        }
    }

    override fun onItemLongClick(pos: Int) {
    }

    private fun getPhotoFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                toast(ru.nextf.measurements.R.string.itis_error)
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                uriPhotoFile = Uri.fromFile(photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile))
                activity.startActivityForResult(takePictureIntent, REQUEST_CAMERA)
            }
        }
    }

    private fun getPhotoFromGallery() {
        val openGalleryIntent = Intent(Intent.ACTION_PICK)
        openGalleryIntent.type = "image/*"
        activity.startActivityForResult(openGalleryIntent, REQUEST_GALERY)
    }

    private fun getPermission() {
        val permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    fun displayPictures(measurement: Measurement) {
        measurement.pictures.let {
            arrayPhoto = ArrayList()
            for (photo in measurement.pictures?.iterator() ?: emptyList<Picture>().iterator()) {
                arrayPhoto.add(MeasurementPhoto(photo.url.toString(), photo.id))
            }
        }

        val adapter = ImageGalleryAdapter(context, arrayPhoto, this)
        recyclerPhotoList.adapter = adapter
        recyclerPhotoList.adapter.notifyDataSetChanged()
    }


    fun postPictureFile(file: File) {
        try {
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                val inputStream = FileInputStream(photoFile)
                val selectedImage = BitmapFactory.decodeStream(inputStream)
                rotateOrientation(selectedImage).compress(Bitmap.CompressFormat.JPEG, 20, fos)
            } finally {
                if (fos != null) fos.close()
            }
        } catch (e: Exception) {
            toast(ru.nextf.measurements.R.string.error)
        }
        NetworkControllerPicture.addPictureFile(measurement.id.toString(), file)
    }


    private fun rotateOrientation(srcBitmap: Bitmap): Bitmap {
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(uriPhotoFile?.path)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val orientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        exif?.setAttribute(ExifInterface.TAG_ORIENTATION, 0.toString())
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                matrix.postRotate(90f)
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                matrix.postRotate(180f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                matrix.postRotate(270f)
            }
        }
        return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.width,
                srcBitmap.height, matrix, true)
    }


    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.absolutePath
        return image
    }

    fun galleryAddPic() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(mCurrentPhotoPath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

    private fun getSavedMeasurement() {
        val json = this.arguments.getString(MEASUREMENT_KEY)
        measurement = if (json.isNullOrEmpty()) {
            Measurement()
        } else {
            val type = object : TypeToken<Measurement>() {
            }.type
            gson.fromJson(json, type)
        }
    }

}
