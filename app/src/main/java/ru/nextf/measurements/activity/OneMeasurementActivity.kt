package ru.nextf.measurements.activity

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import ru.nextf.measurements.fragments.*
import ru.nextf.measurements.network.NetworkController
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_measurement.*
import ru.nextf.measurements.*
import ru.nextf.measurements.modelAPI.*
import android.os.SystemClock
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat


class OneMeasurementActivity : AppCompatActivity(), NetworkController.CallbackUpdateOneMeasurement,
        MainMeasurementFragment.MainMF, MyWebSocket.SocketCallback {
    lateinit var measurement: Measurement
    lateinit var fragmentComment: CommentsMeasurementFragment
    lateinit var fragmentPicture: MeasurementPhotoFragment
    private var isMainPage = false
    private var isResume = false
    private var isCommentPage = false
    private var isPicturePage = false
    private var handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkController.registerUpdateOneMeasurementCallback(this)
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
        val intent = Intent(applicationContext, CompleteActivity::class.java)
        intent.putExtra(ID_KEY, measurement.id.toString())
        intent.putExtra(DEAL_KEY, measurement.deal)
        startActivityForResult(intent, 0)
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
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == 200) {
            setResult(200)
        }
    }

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
                if (measurement.id == newComment.id) {
                    (measurement.comments as ArrayList).add(newComment.comment)
                    if (bottomNavigation.selectedItemId == R.id.commentsMeasurement) {
                        fragmentComment.refreshComments(measurement)
                    }
                }
                setResult(200)
            }
        }
    }
}