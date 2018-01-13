package com.addd.measurements.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.addd.measurements.*
import com.addd.measurements.adapters.ImageGalleryAdapter
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.modelAPI.MeasurementPhoto
import com.addd.measurements.modelAPI.Picture
import com.addd.measurements.network.NetworkControllerPicture
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_images.*
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ImagesActivity : AppCompatActivity(), NetworkControllerPicture.PictureCallback, NetworkControllerPicture.UpdatePicturesCallback {
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val REQUEST_CAMERA = 1
    private val REQUEST_GALERY = 2
    private lateinit var file: File

    var photoFile: File? = null
    var mCurrentPhotoPath = ""
    private lateinit var measurement: Measurement
    private var arrayPhoto = ArrayList<MeasurementPhoto>()
    private lateinit var recyclerPhotoList: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkControllerPicture.registerPictureCallback(this)
        NetworkControllerPicture.registerUpdateCallback(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)

        getSavedMeasurement()

        val layoutManager = GridLayoutManager(this, 2)
        recyclerPhotoList = rv_images
        recyclerPhotoList.setHasFixedSize(true)
        recyclerPhotoList.layoutManager = layoutManager

        getPermission()

        displayPictures(recyclerPhotoList)

        fab.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.what_use)
                    .setCancelable(true)
                    .setPositiveButton(R.string.new_photo)
                    { _, _ ->
                        getPhotoFromCamera()
                    }
                    .setNegativeButton(R.string.from_gallery) { _, _ -> getPhotoFromGallery() }
            val alert = builder.create()
            alert.show()
        }
    }

    private fun getPhotoFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                toast(R.string.itis_error)
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile))
                startActivityForResult(takePictureIntent, REQUEST_CAMERA)
            }
        }
    }

    private fun getPhotoFromGallery() {
        val openGalleryIntent = Intent(Intent.ACTION_PICK)
        openGalleryIntent.type = "image/*"
        startActivityForResult(openGalleryIntent, REQUEST_GALERY)
    }

    private fun getPermission() {
        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    private fun displayPictures(recyclerView: RecyclerView) {
        measurement.pictures.let {
            arrayPhoto = ArrayList()
            var strBuilder = StringBuilder()
            var count = 0
            var index = 0
            for (photo in measurement.pictures?.iterator() ?: emptyList<Picture>().iterator()) {
                strBuilder.append(photo.url)
                strBuilder.reverse()
                for (char in strBuilder) {
                    index++
                    if (char == '/') count++
                    if (count == 3) {
                        index--
                        break
                    }
                }
                strBuilder.delete(index, strBuilder.length)
                strBuilder.reverse()
                arrayPhoto.add(MeasurementPhoto(BASE_URL + strBuilder.toString(), photo.id.toString()))
                count = 0
                index = 0
                strBuilder.delete(0, strBuilder.length)
            }
        }

        val adapter = ImageGalleryAdapter(this, arrayPhoto)
        recyclerView.adapter = adapter
        recyclerView.adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "savedBitmapADDDpicture.jpeg")
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            galleryAddPic()
            postPictureFile(file)
        }
        if (requestCode == REQUEST_GALERY && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            postPictureUri(file, uri)
        }
    }

    private fun postPictureFile(file: File) {
        try {
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                val inputStream = FileInputStream(photoFile)
                val selectedImage = BitmapFactory.decodeStream(inputStream)
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 20, fos)
            } finally {
                if (fos != null) fos.close()
            }
        } catch (e: Exception) {
            toast(R.string.error)
        }
        NetworkControllerPicture.addPictureFile(measurement.id.toString(), file)
    }

    private fun postPictureUri(file: File, uri: Uri?) {
        try {
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                val inputStream = MyApp.instance.contentResolver.openInputStream(uri)
                val selectedImage = BitmapFactory.decodeStream(inputStream)
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 20, fos)
            } finally {
                if (fos != null) fos.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        NetworkControllerPicture.addPictureFile(measurement.id.toString(), file)
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

    private fun galleryAddPic() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(mCurrentPhotoPath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        this.sendBroadcast(mediaScanIntent)
    }

    private fun getSavedMeasurement() {
        val json = intent?.getStringExtra(MEASUREMENT_KEY)
        measurement = if (json.isNullOrEmpty()) {
            Measurement()
        } else {
            val type = object : TypeToken<Measurement>() {
            }.type
            gson.fromJson(json, type)
        }
    }

    override fun resultPictureAdd(result: Boolean) {
        file.delete()
        if (result) {
            toast(R.string.photo_added)
            NetworkControllerPicture.getOneMeasurement(measurement.id.toString())
            setResult(200)
        } else {
            toast(R.string.error_add_photo)
        }
    }

    override fun resultUpdate(measurement: Measurement?) {
        this.measurement = measurement ?: Measurement()
        displayPictures(recyclerPhotoList)
    }

    override fun onDestroy() {
        NetworkControllerPicture.registerPictureCallback(null)
        NetworkControllerPicture.registerUpdateCallback(null)
        super.onDestroy()
    }
}
