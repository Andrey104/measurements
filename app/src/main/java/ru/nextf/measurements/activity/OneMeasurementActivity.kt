package ru.nextf.measurements.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import ru.nextf.measurements.fragments.*
import ru.nextf.measurements.network.NetworkController
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_measurement.*
import ru.nextf.measurements.*
import ru.nextf.measurements.modelAPI.*


class OneMeasurementActivity : AppCompatActivity(), NetworkController.CallbackUpdateOneMeasurement,
        MainMeasurementFragment.MainMF, MyWebSocket.SocketCallback {
    lateinit var measurement: Measurement
    lateinit var fragmentComment: CommentsMeasurementFragment
    lateinit var fragmentPicture: MeasurementPhotoFragment
    private var isMainPage = false
    private var isResume = false
    private var isCommentPage = false
    private var isPicturePage = false
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
            supportFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.measurementContainerLayout, fragment).commit()
            title = String.format("Замер %05d", intent.getIntExtra(DEAL_ID, 0))
            NetworkController.getOneMeasurement(intent.getIntExtra(ID_KEY, 0).toString())
        }
        onItemClick()
    }

    private fun mainPage() {
        if (!isMainPage) {
            val bundle = Bundle()
            bundle.putString(MEASUREMENT_KEY, gson.toJson(measurement))
            val fragment = MainMeasurementFragment()
            fragment.registerMainMF(this)
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.measurementContainerLayout, fragment).commit()
        }
    }

    private fun commentPage() {
        if (!isCommentPage) {
            supportFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.measurementContainerLayout, EmptyFragment()).commit()
            fragmentComment = CommentsMeasurementFragment()
            val bundle = Bundle()
            bundle.putString(MEASUREMENT_KEY, gson.toJson(measurement))
            fragmentComment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.measurementContainerLayout, fragmentComment).commit()
        }
    }

    private fun picturePage() {
        if (!isPicturePage) {
            fragmentPicture = MeasurementPhotoFragment()
            val json = gson.toJson(measurement)
            val bundle = Bundle()
            bundle.putString(MEASUREMENT_KEY, json)
            fragmentPicture.arguments = bundle
            supportFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.measurementContainerLayout, fragmentPicture).commit()
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
            bottomNavigation.visibility = View.VISIBLE
            this.measurement = measurement
            mainPage()
        } else {
            supportFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.measurementContainerLayout, EmptyFragment()).commit()
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
            val fragment = LoadFragment()
            supportFragmentManager.beginTransaction().replace(ru.nextf.measurements.R.id.measurementContainerLayout, fragment).commit()
            NetworkController.getOneMeasurement(measurement.id.toString())
            setResult(200)
        }
    }

    override fun message(text: String) {
        val type = object : TypeToken<Event>() {}.type
        val event = gson.fromJson<Event>(text, type)
        when (event.event) {
            "on_create_measurement",
            "on_complete_measurement", "on_reject_measurement", "on_take",
            "on_transfer_measurement" -> {
                val type = object : TypeToken<EventUpdateList>() {}.type
                val event = gson.fromJson<EventUpdateList>(gson.toJson(event.data), type)
                NetworkController.getOneMeasurement(measurement.id.toString())
                setResult(200)
            }

            "on_comment_measurement" -> {
                setResult(200)
                val type = object : TypeToken<NewCommentMeasurement>() {}.type
                val newComment = gson.fromJson<NewCommentMeasurement>(gson.toJson(event.data), type)
                if (measurement.id == newComment.id) {
                    (measurement.comments as ArrayList).add(newComment.comment)
                    if (bottomNavigation.selectedItemId == R.id.commentsMeasurement) {
                        fragmentComment.refreshComments(measurement)
                    }
                }
            }
        }
    }
}