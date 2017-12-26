package com.addd.measurements.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.widget.Toast
import com.addd.measurements.adapters.ImageGalleryAdapter
import com.addd.measurements.modelAPI.MeasurementPhoto
import com.addd.measurements.R
import com.addd.measurements.gson
import com.addd.measurements.modelAPI.Measurement
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_images.*
import android.content.Intent
import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.addd.measurements.network.NetworkControllerPicture
import java.io.File


class ImagesActivity : AppCompatActivity() {
    private val BASE_URL = "http://188.225.46.31/"
    private lateinit var measurement: Measurement
    private  var chosenImageUri : Uri? = null
    private lateinit var filePath : String
    private var arrayPhoto = ArrayList<MeasurementPhoto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)
        getSavedMeasurement()

        val layoutManager = GridLayoutManager(this, 2)
        val recyclerView = rv_images
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager

        if (measurement.pictures != null) {
            for (photo in measurement.pictures!!) {
                arrayPhoto.add(MeasurementPhoto(BASE_URL + photo.url!!,photo.id.toString()!!))
            }
        }

        val adapter = ImageGalleryAdapter(this, arrayPhoto)
        recyclerView.adapter = adapter

        fab.setOnClickListener { view ->
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photo = File(Environment.getExternalStorageDirectory(), "pic.jpg")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo))
            chosenImageUri = Uri.fromFile(photo)
            startActivityForResult(intent, 1)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode === Activity.RESULT_OK) {
                    NetworkControllerPicture.addPicture(chosenImageUri, measurement.id.toString())
                    Toast.makeText(applicationContext, chosenImageUri.toString(), Toast.LENGTH_SHORT).show()
                }
            }
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


}
