package ru.nextf.measurements.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.comments_measurement_fragment.view.*
import ru.nextf.measurements.MEASUREMENT_KEY
import ru.nextf.measurements.R
import ru.nextf.measurements.adapters.CommentAdapter
import ru.nextf.measurements.gson
import ru.nextf.measurements.modelAPI.Comment
import ru.nextf.measurements.modelAPI.CommentRequest
import ru.nextf.measurements.modelAPI.Measurement
import ru.nextf.measurements.network.NetworkController
import ru.nextf.measurements.network.NetworkControllerComment
import ru.nextf.measurements.network.NetworkControllerVoice
import ru.nextf.measurements.toast
import java.io.File
import java.io.IOException


/**
 * Created by addd on 02.02.2018.
 */
class CommentsMeasurementFragment : Fragment(),
        NetworkController.CallbackUpdateOneMeasurementFragment{
    private lateinit var mView: View
    private lateinit var measurement: Measurement
    private lateinit var bundle: Bundle
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CommentAdapter
    private lateinit var commentRequest: CommentRequest
    private val handler = Handler()
    var mMediaRecorder = MediaRecorder()
    private var mAudioFile: File? = null
    private var isRecording = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        NetworkController.registerOneMeasurementCallbackFragment(this)
        mView = inflater?.inflate(ru.nextf.measurements.R.layout.comments_measurement_fragment, container, false) ?: View(context)
        bundle = this.arguments
        getPermissionRecord()
        getSavedMeasurement()
        displayComments()
        if (measurement.comments?.isNotEmpty() == true) {
            recycler.scrollToPosition(recycler.adapter.itemCount - 1)
        }
        recycler.addOnLayoutChangeListener({ _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                recycler.post({
                    if (measurement.comments?.isNotEmpty() == true) {
                        recycler.scrollToPosition(recycler.adapter.itemCount - 1)
                    }
                })
            }
        })
        mView.imageButtonSend.setOnClickListener { sendComment() }
        mView.refresh.setOnRefreshListener {
            mView.refresh.isRefreshing = true
            adapter = CommentAdapter(emptyList(), activity)
            mView.recyclerViewComments.adapter = adapter
            NetworkController.getOneMeasurementFragment(measurement.id.toString())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mView.refresh.setColorSchemeColors(resources.getColor(R.color.colorAccent, context.theme),
                    resources.getColor(R.color.colorPrimary, context.theme),
                    resources.getColor(R.color.colorPrimaryDark, context.theme))
        } else {
            mView.refresh.setColorSchemeColors(resources.getColor(R.color.colorAccent),
                    resources.getColor(R.color.colorPrimary),
                    resources.getColor(R.color.colorPrimaryDark))
        }

        mView.voice.setOnClickListener { getVoiceRecord() }
        mView.stop.setOnClickListener { stopRecord() }

        return mView
    }

    private fun getVoiceRecord() {
        try {
            mView.chronometer2.visibility = View.VISIBLE
            mView.chronometer2.base = SystemClock.elapsedRealtime()
            mView.chronometer2.start()
            isRecording = true
            mView.voice.visibility = View.GONE
            mView.stop.visibility = View.VISIBLE

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        0)

            } else {
                startRecording()
            }
        } catch (e: Exception) {
            toast(R.string.error)
        }
    }

    private fun stopRecord() {
        isRecording = false
        mView.chronometer2.stop()
        mView.voice.visibility = View.VISIBLE
        mView.stop.visibility = View.GONE
        mView.chronometer2.visibility = View.GONE
        stopRecording()
//        processAudioFile()
        NetworkControllerVoice.addVoiceFileMeasurement(measurement.id.toString(), mAudioFile)
    }


    override fun resultUpdate(measurement: Measurement?) {
        mView.refresh.isRefreshing = false
        if (measurement != null) {
            this.measurement = measurement
            displayComments()
        }
    }

    private fun getPermissionRecord() {
        val permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    0)
        }
    }


    private fun startRecording() {
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        if (mAudioFile == null) {
            val sampleDir = Environment.getExternalStorageDirectory()

            try {
                mAudioFile = File.createTempFile("meow", ".aac", sampleDir)
            } catch (e: IOException) {
                toast(R.string.error)
                return
            }

        }
        mMediaRecorder.setOutputFile(mAudioFile?.absolutePath)

        mMediaRecorder.prepare()
        mMediaRecorder.start()
    }

    private fun stopRecording() {
        mMediaRecorder.stop()
        println(mAudioFile)
//        mMediaRecorder.release()
    }

    private fun displayComments() {
        adapter = CommentAdapter(measurement.comments as ArrayList<Comment>, activity)
        recycler = mView.recyclerViewComments
        mView.recyclerViewComments.adapter = adapter

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mView.recyclerViewComments.layoutManager = layoutManager

    }

    private fun sendComment() {
        if (mView.editTextCommentProblem.text.isEmpty()) {
            toast(ru.nextf.measurements.R.string.enter_comment)
        } else {
            commentRequest = CommentRequest(mView.editTextCommentProblem.text.toString(), 1)
            NetworkControllerComment.addComment(commentRequest, measurement.id.toString()) // изменить
            mView.editTextCommentProblem.setText(ru.nextf.measurements.R.string.empty)
        }
    }

    private fun getSavedMeasurement(): Measurement {
        val json = bundle.getString(MEASUREMENT_KEY)
        measurement = if (json.isNullOrEmpty()) {
            Measurement()
        } else {
            val type = object : TypeToken<Measurement>() {
            }.type
            gson.fromJson(json, type)
        }
        return measurement
    }

    fun refreshComments(measurement: Measurement) {
        handler.post {
            adapter.closePlayer()
            adapter = CommentAdapter(measurement.comments as ArrayList<Comment>, activity)
            recycler = mView.recyclerViewComments
            mView.recyclerViewComments.adapter = adapter
            adapter.notifyDataSetChanged()

            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            mView.recyclerViewComments.layoutManager = layoutManager
            recycler.scrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun onDestroyView() {
        NetworkControllerComment.registerCommentCallback(null)
        if (isRecording) {
            mMediaRecorder.stop()
            mAudioFile?.delete()
            mMediaRecorder.release()
        }
        adapter.closePlayer()
        super.onDestroyView()
    }

}