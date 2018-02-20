package ru.nextf.measurements.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
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
import ru.nextf.measurements.modelAPI.*
import ru.nextf.measurements.network.NetworkController
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by addd on 01.02.2018.
 */
class MeasurementPhotoFragment : Fragment(), NetworkControllerPicture.PictureCallback,
        NetworkControllerPicture.UpdatePicturesCallback, MyWebSocket.SocketCallback {
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val REQUEST_CAMERA = 1
    private val REQUEST_GALERY = 2
    private lateinit var file: File
    private lateinit var mView: View

    var photoFile: File? = null
    var mCurrentPhotoPath = ""
    private lateinit var measurement: Measurement
    private var arrayPhoto = ArrayList<MeasurementPhoto>()
    private lateinit var recyclerPhotoList: RecyclerView
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        NetworkControllerPicture.registerPictureCallback(this)
        NetworkControllerPicture.registerUpdateCallback(this)
        mView = inflater?.inflate(ru.nextf.measurements.R.layout.measurement_photo_fragment, container, false) ?: View(context)

        getSavedMeasurement()

        val layoutManager = GridLayoutManager(context, 2)
        recyclerPhotoList = mView.rv_images
        recyclerPhotoList.setHasFixedSize(true)
        recyclerPhotoList.layoutManager = layoutManager

        getPermission()

        displayPictures(recyclerPhotoList)

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
        val permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
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

        val adapter = ImageGalleryAdapter(context, arrayPhoto)
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
            toast(ru.nextf.measurements.R.string.error)
        }
        NetworkControllerPicture.addPictureFile(measurement.id.toString(), file)
    }

    private fun postPictureUri(file: File, uri: Uri?) {
        try {
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                val inputStream = ru.nextf.measurements.MyApp.instance.contentResolver.openInputStream(uri)
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

    override fun resultPictureAdd(result: Boolean) {
        file.delete()
        if (result) {
            toast(ru.nextf.measurements.R.string.photo_added)
            NetworkControllerPicture.getOneMeasurement(measurement.id.toString())
            activity.setResult(200)
        } else {
            toast(ru.nextf.measurements.R.string.error_add_photo)
        }
    }

    override fun message(text: String) {
        val type = object : TypeToken<Event>() {}.type
        val event = gson.fromJson<Event>(text, type)
        when (event.event) {
            "on_create_measurement",
            "on_complete_measurement", "on_reject_measurement", "on_take",
            "on_transfer_measurement", "on_comment_measurement" -> {
                val type = object : TypeToken<EventUpdateList>() {}.type
                val event = gson.fromJson<EventUpdateList>(gson.toJson(event.data), type)
                if (measurement.id == event.id) {
                    NetworkController.getOneMeasurement(measurement.id.toString())
                    activity.setResult(200)
                }
            }
        }
    }

    override fun resultUpdate(measurement: Measurement?) {
        if (measurement == null) {
            toast(ru.nextf.measurements.R.string.error_add_photo)
        } else {
            this.measurement = measurement
            displayPictures(recyclerPhotoList)
        }
    }

    override fun onDestroyView() {
        NetworkControllerPicture.registerPictureCallback(null)
        NetworkControllerPicture.registerUpdateCallback(null)
        super.onDestroyView()
    }


}
