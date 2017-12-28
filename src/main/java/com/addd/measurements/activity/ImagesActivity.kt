package com.addd.measurements.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.addd.measurements.R
import com.addd.measurements.adapters.ImageGalleryAdapter
import com.addd.measurements.gson
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.modelAPI.MeasurementPhoto
import com.addd.measurements.network.NetworkControllerPicture
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_images.*
import java.io.File


class ImagesActivity : AppCompatActivity(), NetworkControllerPicture.PictureCallback, NetworkControllerPicture.UpdatePicturesCallback {
    private val PERMISSIONS_STORAGE = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val BASE_URL = "http://188.225.46.31/"
    private lateinit var measurement: Measurement
    private var arrayPhoto = ArrayList<MeasurementPhoto>()
    private lateinit var recyclerPhotoList : RecyclerView


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

        fab.setOnClickListener { view ->
            val openGalleryIntent = Intent(Intent.ACTION_PICK)
            openGalleryIntent.type = "image/*"
            startActivityForResult(openGalleryIntent, 200)
        }
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
        if (measurement.pictures != null) {
            arrayPhoto = ArrayList<MeasurementPhoto>()
            for (photo in measurement.pictures!!) {
                val photoUrl = photo.url
//                photoUrl?.startsWith("media" true)
                arrayPhoto.add(MeasurementPhoto(BASE_URL + photo.url!!, photo.id.toString()!!))
            }
        }

        val adapter = ImageGalleryAdapter(this, arrayPhoto)
        recyclerView.adapter = adapter
        recyclerView.adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            NetworkControllerPicture.addPicture(measurement.id.toString(), uri)
//            val file = File(uri?.path)
//            Toast.makeText(applicationContext, file.path, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSavedMeasurement() {
        if (intent != null && intent.hasExtra("measurement")) {
            val json = intent.getStringExtra("measurement")
            if (json.isEmpty()) {
                measurement = Measurement()
            } else {
                val type = object : TypeToken<Measurement>() {
                }.type
                measurement = gson.fromJson(json, type)
            }
        }
    }

    override fun resultPictureAdd(result: Boolean) {
        if (result) {
            Toast.makeText(applicationContext, "Фотография добавлена", Toast.LENGTH_SHORT).show()
            NetworkControllerPicture.getOneMeasurement(measurement.id.toString())
            setResult(200)
        } else {
            Toast.makeText(applicationContext, "При загрузке произошла ошибка", Toast.LENGTH_SHORT).show()
        }
    }

    override fun resultUpdate(measurement: Measurement?) {
            this.measurement = measurement!!
            displayPictures(recyclerPhotoList)
    }
}
