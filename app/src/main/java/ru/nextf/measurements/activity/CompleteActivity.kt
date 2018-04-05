package ru.nextf.measurements.activity

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.gson.reflect.TypeToken
import ru.nextf.measurements.modelAPI.Close
import ru.nextf.measurements.network.NetworkController
import kotlinx.android.synthetic.main.activity_complete.*
import ru.nextf.measurements.*
import java.util.*
import ru.nextf.measurements.modelAPI.Measurement
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import ru.nextf.measurements.adapters.HorizontalAdapter
import ru.nextf.measurements.modelAPI.MeasurementPhoto
import ru.nextf.measurements.modelAPI.Picture
import android.widget.Spinner
import ru.nextf.measurements.network.NetworkControllerPicture
import java.io.*
import java.text.SimpleDateFormat


class CompleteActivity : AppCompatActivity(), NetworkController.CloseCallback, HorizontalAdapter.CustomAdapterCallback,
        NetworkControllerPicture.PictureCallback,
        NetworkControllerPicture.UpdatePicturesCallback {
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val REQUEST_CAMERA = 1
    private val DELETE_PHOTO = 1212
    private val REQUEST_GALERY = 2
    private var uriPhotoFile: Uri? = null
    var photoFile: File? = null
    private lateinit var file: File
    private val matrix = Matrix()
    var mCurrentPhotoPath = ""
    private var id: String = ""
    private var deal: Int = 0
    private lateinit var alert: AlertDialog
    private lateinit var measurement: Measurement
    private var serverDate: String? = null
    private var months = emptyArray<String>()
    private var daySave = -1
    private var monthSave = -1
    private var yearSave = -1
    private var canRequest = false
    private var arrayPhoto = LinkedList<MeasurementPhoto>()


    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerCloseCallback(this)
        months = resources.getStringArray(ru.nextf.measurements.R.array.months)
        NetworkControllerPicture.registerUpdateCallback(this)
        NetworkControllerPicture.registerPictureCallback(this)

        super.onCreate(savedInstanceState)
        setContentView(ru.nextf.measurements.R.layout.activity_complete)
        constraintLayout.setOnTouchListener { v, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
        textViewDate.setOnClickListener {
            datePick()
        }
        toolbarAst.setNavigationIcon(ru.nextf.measurements.R.drawable.ic_arrow_back_white_24dp)
        toolbarAst.setNavigationOnClickListener {
            finish()
        }

//        editTextSum.addTextChangedListener(OwnWatcher())
        checkBoxOffer.setOnClickListener {
            if (checkBoxOffer.isChecked) {
                textViewDateInstallation.visibility = View.VISIBLE
                dateButton.visibility = View.VISIBLE
                textViewDate.visibility = View.VISIBLE
                editTextPrepayment.visibility = View.VISIBLE
                spinner.visibility = View.VISIBLE
                textView9.visibility = View.VISIBLE
            } else {
                editTextPrepayment.visibility = View.GONE
                spinner.visibility = View.GONE
                textView9.visibility = View.GONE
                textViewDateInstallation.visibility = View.GONE
                dateButton.visibility = View.GONE
                textViewDate.visibility = View.GONE
                imageButton.visibility = View.GONE
                deleteDateMount()
                editTextPrepayment.text = null
                spinner.setSelection(0)
            }
        }

        getSavedMeasurement()
        id = intent?.getStringExtra(ID_KEY) ?: "0"
        deal = intent?.getIntExtra(DEAL_KEY, 0) ?: throw IllegalStateException()
        textViewDeal.text = String.format("Договор %05d", deal)

        dateButton.setOnClickListener { datePick() }
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doCompleteRequest() }
        imageButton.setOnClickListener {
            deleteDateMount()
            imageButton.visibility = View.GONE
        }
        editTextSum.addTextChangedListener(NumberTextWatcherForThousand(editTextSum))
        editTextPrepayment.addTextChangedListener(NumberTextWatcherForThousand(editTextPrepayment))
        canRequest = measurement.pictures?.size != 0

        getPermission()
        displayPictures(measurement)
    }

    // начало рабоыт с картинками
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DELETE_PHOTO) {
            if (resultCode == 200) {
                NetworkControllerPicture.getOneMeasurement(measurement.id.toString())
            }
            return
        }
        file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "savedBitmapADDDpicture.jpeg")
        if (requestCode == REQUEST_GALERY && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            try {
                matrix.postRotate(getImageOrientation(data ?: Intent()).toFloat())
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            postPictureUri(file, uri)
        }
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            galleryAddPic()
            postPictureFile(file)
        }
        super.onActivityResult(requestCode, resultCode, intent)

    }

    private fun postPictureUri(file: File, uri: Uri?) {
        try {
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                val inputStream = ru.nextf.measurements.MyApp.instance.contentResolver.openInputStream(uri)
                val selectedImage = BitmapFactory.decodeStream(inputStream)
                val rotatedBitmap = Bitmap.createBitmap(selectedImage, 0, 0, selectedImage.width, selectedImage.height, matrix, true)
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, fos)
            } finally {
                if (fos != null) fos.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        NetworkControllerPicture.addPictureFile(measurement.id.toString(), file)
    }

    override fun resultPictureAdd(result: Boolean) {
        file.delete()
        if (result) {
            toast(ru.nextf.measurements.R.string.photo_added)
            NetworkControllerPicture.getOneMeasurement(measurement.id.toString())
            setResult(200)
        } else {
            toast(ru.nextf.measurements.R.string.error_add_photo)
        }
    }

    override fun resultUpdatePicAdd(measurement: Measurement?) {
        if (measurement == null) {
            toast(ru.nextf.measurements.R.string.error_add_photo)
        } else {
            this.measurement = measurement
            displayPictures(measurement)
        }
    }

    override fun onFirstItemClick(pos: Int) {
        val builder = AlertDialog.Builder(this)
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

    private fun getPhotoFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
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
                startActivityForResult(takePictureIntent, REQUEST_CAMERA)
            }
        }
    }

    private fun getPhotoFromGallery() {
        val openGalleryIntent = Intent(Intent.ACTION_PICK)
        openGalleryIntent.type = "image/*"
        startActivityForResult(openGalleryIntent, REQUEST_GALERY)
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
        applicationContext.sendBroadcast(mediaScanIntent)
    }

    private fun getImageOrientation(data: Intent): Int {
        println(data)
        val imageUri = data.data
        val orientationColumn = arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur = contentResolver.query(imageUri, orientationColumn, null, null, null)
        var orientation = -1
        if (cur != null && cur.moveToFirst()) {
            try {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
            } catch (e: Exception) {
                return orientation
            }
        } else {
            Log.d("orientation", "Wrong picture orientation: " + orientation)
        }
        if (cur != null) cur.close()

        return orientation
    }

    private fun displayPictures(measurement: Measurement) {
        measurement.pictures.let {
            arrayPhoto = LinkedList()
            for (photo in measurement.pictures?.iterator() ?: emptyList<Picture>().iterator()) {
                arrayPhoto.add(MeasurementPhoto(photo.url.toString(), photo.id))
            }
        }
        arrayPhoto.addFirst(MeasurementPhoto(BASE_URL, -2))
        val adapter = HorizontalAdapter(this, arrayPhoto, this)
        val thirdManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        images.layoutManager = thirdManager
        images.adapter = adapter
        images.adapter.notifyDataSetChanged()
    }

    private fun getPermission() {
        val permission = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    override fun OnPhotoCLick(pos: Int) {
        val spacePhoto = arrayPhoto.get(pos)
        val intent = Intent(applicationContext, OnePhotoActivity::class.java)
        intent.putExtra(MEASUREMENT_ID_DELETE, measurement.id)
        intent.putExtra(PHOTO_ID_DELETE, spacePhoto.getId())
        intent.putExtra(MEASUREMENT_PHOTO, spacePhoto)
        intent.putExtra(CHECK, pos)
        startActivityForResult(intent, DELETE_PHOTO)
    }


    //конец работы с картинками
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

    private fun deleteDateMount() {
        serverDate = null
        textViewDate.text = getString(ru.nextf.measurements.R.string.select_date)
        yearSave = -1
        monthSave = -1
        daySave = 1
    }


    private fun datePick() {
        val myCallBack = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            serverDate = String.format("$year-%02d-%02d", monthOfYear + 1, dayOfMonth)
            imageButton.visibility = View.VISIBLE
            textViewDate.text = "$dayOfMonth ${months[monthOfYear]} $year года"
            yearSave = year
            monthSave = monthOfYear
            daySave = dayOfMonth
        }
        val calendar = Calendar.getInstance()
        val datePicker = if (yearSave == -1) {
            DatePickerDialog(this, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        } else {
            DatePickerDialog(this, myCallBack, yearSave, monthSave, daySave)
        }
        datePicker.show()
    }

    private fun doCompleteRequest(): Boolean {
        if (editTextSum.text.isEmpty() && !editTextPrepayment.text.isEmpty() && spinner.selectedItemPosition == 0) {
            toast(ru.nextf.measurements.R.string.enter_sum_payment)
            return false
        }
        if (editTextSum.text.isEmpty()) {
            toast(ru.nextf.measurements.R.string.enter_sum)
            return false
        }
        if (!editTextPrepayment.text.isEmpty() && spinner.selectedItemPosition == 0) {
            toast(ru.nextf.measurements.R.string.enter_payment)
            return false
        }

        if (!canRequest) {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setCancelable(true)
                    .setTitle("Предупреждение")
                    .setMessage("Вы действительно хотите завершить замер без фотографий?")
                    .setPositiveButton(ru.nextf.measurements.R.string.yes)
                    { dialog, _ ->
                        complete()
                        dialog.cancel()
                    }
                    .setNegativeButton(ru.nextf.measurements.R.string.no)
                    { dialog, _ ->
                        dialog.cancel()
                    }
            val alert = builder.create()
            alert.show()
        }
        if (canRequest) {
            complete()
        }
        return true
    }

    private fun complete() {
        buttonOk.isEnabled = false
        var q = NumberTextWatcherForThousand.trimCommaOfString(editTextSum.text.toString())
        if (q[q.length - 1] == '.') {
            q = q.substring(0, q.length - 1)
        }
        val sum: Float = q.toFloat()

        var prepayment = NumberTextWatcherForThousand.trimCommaOfString(editTextPrepayment.text.toString())
        var prepaymentClose: Float?
        if (prepayment.isNotEmpty()) {
            if (prepayment[prepayment.length - 1] == '.') {
                prepayment = prepayment.substring(0, prepayment.length - 1)
            }
            prepaymentClose = prepayment.toFloat()
        } else {
            prepaymentClose = null
        }

        var paymentMethod = spinner.selectedItemPosition
        val close = Close(if (editTextComment.text.isEmpty()) null else editTextComment.text.toString(),
                prepaymentClose,
                sum, checkBoxOffer.isChecked, serverDate, paymentMethod)
        showDialog()
        NetworkController.closeMeasurement(close, id)
    }

    override fun onDestroy() {
        NetworkController.registerCloseCallback(null)
        super.onDestroy()
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        val viewAlert = layoutInflater.inflate(ru.nextf.measurements.R.layout.update_dialog, null)
        builder.setView(viewAlert)
                .setCancelable(false)
        alert = builder.create()
        alert.show()
    }

    override fun resultClose(result: Boolean) {
        alert.dismiss()
        if (result) {
            setResult(200)
            toast(ru.nextf.measurements.R.string.measurement_closed)
            finish()
        } else {
            toast(ru.nextf.measurements.R.string.close_measurement_error)
            buttonOk.isEnabled = true
        }
    }
}
