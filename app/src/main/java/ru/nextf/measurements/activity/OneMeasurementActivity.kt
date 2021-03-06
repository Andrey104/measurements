package ru.nextf.measurements.activity

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.view.View
import ru.nextf.measurements.fragments.*
import ru.nextf.measurements.network.NetworkController
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_measurement.*
import ru.nextf.measurements.*
import ru.nextf.measurements.modelAPI.*
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import ru.nextf.measurements.network.NetworkControllerPicture
import android.media.ExifInterface
import android.R.attr.path
import java.io.*
import android.provider.MediaStore
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.R.attr.data
import android.content.SharedPreferences
import android.graphics.Matrix
import android.preference.PreferenceManager
import android.util.Log
import ru.nextf.measurements.network.NetworkControllerComment
import ru.nextf.measurements.network.NetworkControllerVoice


class OneMeasurementActivity : AppCompatActivity(), NetworkController.CallbackUpdateOneMeasurement,
        MainMeasurementFragment.MainMF, MyWebSocket.SocketCallback, NetworkControllerPicture.PictureCallback,
        NetworkControllerPicture.UpdatePicturesCallback, NetworkControllerComment.AddCommentCallback,
        NetworkControllerVoice.VoiceCallback {
    override fun addNote() {
        var json = gson.toJson(measurement)
        val intent = Intent(applicationContext, AddNoteActivity::class.java)
        intent.putExtra(MEASUREMENT_KEY, json)
        startActivityForResult(intent, NOTE_ACTIVITY)
    }

    private val REQUEST_CAMERA = 1
    private val NOTE_ACTIVITY = 99
    private val REQUEST_GALERY = 2
    private val DELETE_PHOTO = 1212
    lateinit var measurement: Measurement
    lateinit var fragmentComment: CommentsMeasurementFragment
    lateinit var fragmentPicture: MeasurementPhotoFragment
    private var isMainPage = false
    private lateinit var file: File
    private var isCommentPage = false
    private var isPicturePage = false
    private val matrix = Matrix()
    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerUpdateOneMeasurementCallback(this)
        NetworkControllerPicture.registerUpdateCallback(this)
        NetworkControllerPicture.registerPictureCallback(this)
        NetworkControllerVoice.registerVoiceCallback(this)
        NetworkControllerComment.registerCommentCallback(this)
        super.onCreate(savedInstanceState)
        setContentView(ru.nextf.measurements.R.layout.activity_one_measurement)
        setSupportActionBar(toolbarAst)
        toolbarAst.setNavigationIcon(ru.nextf.measurements.R.drawable.ic_arrow_back_white_24dp)
        toolbarAst.setNavigationOnClickListener {
            finish()
        }
        if (!intent.hasExtra(MEASUREMENT_EXPANDED)) {
            getSavedMeasurement()
            title = String.format("Замер %05d", measurement.deal)
            mainPage()
        } else {
            bottomNavigation.visibility = View.INVISIBLE
            val fragment = LoadFragment()
            supportFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.measurementContainerLayout, fragment).commitAllowingStateLoss()
            title = String.format("Замер %05d", intent.getIntExtra(DEAL_ID, 0))
            NetworkController.getOneMeasurement(intent.getIntExtra(ID_KEY, 0).toString())
        }
        onItemClick()
    }

    private fun mainPage() {
        val bundle = Bundle()
        bundle.putString(MEASUREMENT_KEY, gson.toJson(measurement))
        val fragment = MainMeasurementFragment()
        fragment.registerMainMF(this)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.measurementContainerLayout, fragment).commit()
    }

    private fun commentPage() {
        if (!isCommentPage) {
            supportFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.measurementContainerLayout, EmptyFragment()).commitAllowingStateLoss()
            fragmentComment = CommentsMeasurementFragment()
            val bundle = Bundle()
            bundle.putString(MEASUREMENT_KEY, gson.toJson(measurement))
            fragmentComment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.measurementContainerLayout, fragmentComment).commitAllowingStateLoss()
        }
    }

    private fun picturePage() {
        if (!isPicturePage) {
            fragmentPicture = MeasurementPhotoFragment()
            val json = gson.toJson(measurement)
            val bundle = Bundle()
            bundle.putString(MEASUREMENT_KEY, json)
            fragmentPicture.arguments = bundle
            supportFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.measurementContainerLayout, fragmentPicture).commitAllowingStateLoss()
        }
    }

    private fun onItemClick() {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                ru.nextf.measurements.R.id.mainMeasurement -> {
                    mainPage()
                    isMainPage = true
                    isCommentPage = false
                    isPicturePage = false
                }
                ru.nextf.measurements.R.id.commentsMeasurement -> {
                    commentPage()
                    isMainPage = false
                    isCommentPage = true
                    isPicturePage = false
                }
                ru.nextf.measurements.R.id.picturesMeasurement -> {
                    picturePage()
                    isMainPage = false
                    isCommentPage = false
                    isPicturePage = true
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        NetworkControllerPicture.registerUpdateCallback(this)
        NetworkControllerPicture.registerPictureCallback(this)
        myWebSocket.registerSocketCallback(this)
    }

    override fun resultUpdate(measurement: Measurement?) {
        if (measurement != null) {
            onPostResume()
            bottomNavigation.visibility = View.VISIBLE
            this.measurement = measurement
            mainPage()
        } else {
            supportFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.measurementContainerLayout, EmptyFragment()).commitAllowingStateLoss()
            toast(ru.nextf.measurements.R.string.error_add_photo)
        }
    }


    private fun getSavedMeasurement() {
        val json = intent.getStringExtra(MEASUREMENT_KEY)
        measurement = if (json.isNullOrEmpty()) {
            Measurement()
        } else {
            val type = object : TypeToken<Measurement>() {
            }.type
            gson.fromJson(json, type)
        }
    }


    override fun onDestroy() {
        NetworkController.registerUpdateOneMeasurementCallback(null)
        super.onDestroy()
    }

    override fun complete() {
        var json = gson.toJson(measurement)
        val intent = Intent(applicationContext, CompleteActivity::class.java)
        intent.putExtra(MEASUREMENT_KEY, json)
        intent.putExtra(ID_KEY, measurement.id.toString())
        intent.putExtra(DEAL_KEY, measurement.deal)
        startActivityForResult(intent, 33)
    }

    override fun reject() {
        val intent = Intent(applicationContext, RejectActivity::class.java)
        intent.putExtra(ID_KEY, measurement.id.toString())
        startActivityForResult(intent, 0)
    }

    override fun transfer() {
        val intent = Intent(applicationContext, TransferActivity::class.java)
        intent.putExtra(ID_KEY, measurement.id.toString())
        startActivityForResult(intent, 0)
    }

    override fun goDeal() {
        val intent = Intent(applicationContext, OneDealActivity::class.java)
        intent.putExtra(DEAL_ID, measurement.deal.toString())
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NOTE_ACTIVITY) {
            if (resultCode == 200) {
                NetworkController.getOneMeasurement(measurement.id.toString())
            }
        }
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
            fragmentPicture.galleryAddPic()
            fragmentPicture.postPictureFile(file)
        }
        if (resultCode == 200) {
            setResult(200)
        }
        if (requestCode == 33) {
            NetworkController.getOneMeasurement(measurement.id.toString())
        }
        super.onActivityResult(requestCode, resultCode, intent)
    }

    //берем картинку из глаерии/еще  откуда-либо. И отправляем из активити, так как в фрагменте приходит пустой uri, а это тупо((
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
            fragmentPicture.displayPictures(measurement)
        }
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
    //конец работы с картинкой

    override fun message(text: String) {
        val type = object : TypeToken<Event>() {}.type
        val event = gson.fromJson<Event>(text, type)
        when (event.event) {
            "on_transfer_measurement" -> {
                val type = object : TypeToken<EventTransfer>() {}.type
                val transfer = gson.fromJson<EventTransfer>(gson.toJson(event.data), type)
                if (transfer.newDate == getTodayDate()) {
                    val notificationIntent = Intent(applicationContext, MainActivity::class.java)
                    val pendingIntent = PendingIntent.getActivity(applicationContext, 0,
                            notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
                    val builder = NotificationCompat.Builder(applicationContext, "wtf")
                            .setContentTitle("Новый замер")
                            .setContentText("На сегодня новый замер")
                            .setWhen(System.currentTimeMillis())
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            .setSmallIcon(R.mipmap.icon)
                    val notificationManager = NotificationManagerCompat.from(applicationContext)
                    notificationManager.notify(1001, builder.build())
                }
                if (measurement.id == transfer.id) {
                    NetworkController.getOneMeasurement(measurement.id.toString())
                }
                if (transfer.oldDate == measurement.date || transfer.newDate == measurement.date) {
                    setResult(200)
                }
            }
            "on_create_measurement" -> {
                val type = object : TypeToken<EventCreate>() {}.type
                val create = gson.fromJson<EventCreate>(gson.toJson(event.data), type)
                if (create.date == measurement.date) {
                    setResult(200)
                }
                if (create.date == getTodayDate()) {
                    val notificationIntent = Intent(applicationContext, MainActivity::class.java)
                    val pendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
                    val builder = NotificationCompat.Builder(applicationContext, "wtf")
                            .setContentTitle("Новый замер")
                            .setContentText("На сегодня новый замер")
                            .setWhen(System.currentTimeMillis())
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            .setSmallIcon(R.mipmap.icon)
                    val notificationManager = NotificationManagerCompat.from(applicationContext)
                    notificationManager.notify(1001, builder.build())
                }
            }
            "on_complete_measurement", "on_reject_measurement", "on_take" -> {
                val type = object : TypeToken<EventUpdateList>() {}.type
                val transfer = gson.fromJson<EventUpdateList>(gson.toJson(event.data), type)
                if (measurement.id == transfer.id) {
                    NetworkController.getOneMeasurement(measurement.id.toString())
                }
                setResult(200)
            }
            "on_comment_measurement" -> {
                val type = object : TypeToken<NewCommentMeasurement>() {}.type
                val newComment = gson.fromJson<NewCommentMeasurement>(gson.toJson(event.data), type)
                val mSettings: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                if ((measurement.id == newComment.id) && (newComment.comment.user.id != mSettings.getInt(MY_ID_USER, 0))) {
                    (measurement.comments as ArrayList).add(newComment.comment)
                    if (bottomNavigation.selectedItemId == R.id.commentsMeasurement) {
                        fragmentComment.refreshComments(measurement)
                    }
                }
                setResult(200)
            }
        }
    }

    override fun addCommentResult(result: Boolean, comment: Comment?) {
        if (result) {
            if (comment != null) {
                (measurement.comments as ArrayList).add(comment)
            }
            if (bottomNavigation.selectedItemId == R.id.commentsMeasurement) {
                fragmentComment.refreshComments(measurement)
            }
            setResult(200)
        } else {
            toast(R.string.error_add_comment)
        }
    }

    override fun resultVoiceAdd(result: Boolean, comment: Comment?) {
        if (result) {
            if (comment != null) {
                (measurement.comments as ArrayList).add(comment)
            }
            if (bottomNavigation.selectedItemId == R.id.commentsMeasurement) {
                fragmentComment.refreshComments(measurement)
            }
            setResult(200)
        } else {
            toast(R.string.error_add_comment)
        }
    }
}