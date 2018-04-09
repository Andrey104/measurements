package ru.nextf.measurements.fragments

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.*
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.nextf.measurements.adapters.CommentAdapter
import ru.nextf.measurements.network.NetworkControllerComment
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_measurement.*
import kotlinx.android.synthetic.main.comments_measurement_fragment.view.*
import ru.nextf.measurements.*
import ru.nextf.measurements.modelAPI.*
import ru.nextf.measurements.network.NetworkControllerDeals
import ru.nextf.measurements.network.NetworkControllerVoice
import java.io.File
import java.io.IOException

/**
 * Created by addd on 08.02.2018.
 */
class CommentsDealFragment : Fragment(), MyWebSocket.SocketCallback,
        NetworkControllerComment.AddCommentCallback, NetworkControllerVoice.VoiceCallback  {
    private lateinit var mView: View
    private lateinit var deal: Deal
    private lateinit var bundle: Bundle
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CommentAdapter
    private lateinit var commentRequest: CommentRequest
    private var isSending = false
    var mMediaRecorder = MediaRecorder()
    private var mAudioFile: File? = null
    private var isRecording = false

    private val handler = Handler()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        NetworkControllerComment.registerCommentCallback(this)
        mView = inflater?.inflate(ru.nextf.measurements.R.layout.comments_measurement_fragment, container, false) ?: View(context)
        bundle = this.arguments
        NetworkControllerVoice.registerVoiceCallback(this)
        myWebSocket.registerSocketCallback(this)
        getSavedDeal()
        getPermissionRecord()
        displayComments()
        if (deal.comments?.isNotEmpty() == true) {
            recycler.scrollToPosition(recycler.adapter.itemCount - 1)
        }
        recycler.addOnLayoutChangeListener({ _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                recycler.post({
                    if (deal.comments?.isNotEmpty() == true) {
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
            NetworkControllerDeals.getOneDeal(deal.id.toString())
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
        NetworkControllerVoice.addVoiceFileDeal(deal.id.toString(), mAudioFile)
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
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        if (mAudioFile == null) {
            val sampleDir = Environment.getExternalStorageDirectory()

            try {
                mAudioFile = File.createTempFile("meow", ".mpeg4", sampleDir)
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

    private fun refreshComments() {
        handler.post {
            adapter = CommentAdapter(deal.comments as ArrayList<Comment>, activity)
            recycler = mView.recyclerViewComments
            mView.recyclerViewComments.adapter = adapter
            adapter.notifyDataSetChanged()

            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            mView.recyclerViewComments.layoutManager = layoutManager
            recycler.scrollToPosition(adapter.itemCount - 1)
        }
    }


    override fun onResume() {
        super.onResume()
        myWebSocket.registerSocketCallback(this)
    }

    override fun message(text: String) {
        val type = object : TypeToken<Event>() {}.type
        val event = gson.fromJson<Event>(text, type)
        if (event.event == "on_comment_deal") {
            val type = object : TypeToken<NewCommentDeal>() {}.type
            val newComment = gson.fromJson<NewCommentDeal>(gson.toJson(event.data), type)
            val mSettings: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            if ((deal.id == newComment.id) &&
                    (newComment.comment.user.id != mSettings.getInt(MY_ID_USER, 0))) {
                handler.post {
                    adapter.add(newComment.comment)
                    recycler.smoothScrollToPosition(adapter.itemCount - 1)
                }
                activity.setResult(200)
            }
        }
    }

    override fun addCommentResult(result: Boolean, comment: Comment?) {
        if (result) {
            if (comment != null) {
                (deal.comments as ArrayList).add(comment)
            }
            refreshComments()
        } else {
            toast(R.string.error_add_comment)
        }
    }

    override fun resultVoiceAdd(result: Boolean, comment: Comment?) {
        if (result) {
            if (comment != null) {
                (deal.comments as ArrayList).add(comment)
            }
                refreshComments()
        } else {
            toast(R.string.error_add_comment)
        }
    }

    private fun displayComments() {
        adapter = CommentAdapter(deal.comments as ArrayList<Comment>, activity)
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
            NetworkControllerComment.addCommentDeal(commentRequest, deal.id.toString())
            mView.editTextCommentProblem.setText(ru.nextf.measurements.R.string.empty)
        }
    }

    private fun getSavedDeal(): Deal {
        val json = bundle.getString(DEAL_KEY)
        deal = if (json.isNullOrEmpty()) {
            Deal()
        } else {
            val type = object : TypeToken<Deal>() {
            }.type
            gson.fromJson(json, type)
        }
        return deal
    }


    override fun onDestroyView() {
        NetworkControllerComment.registerCommentCallback(null)
        if (isRecording) {
            mMediaRecorder.stop()
            mAudioFile?.delete()
            mMediaRecorder.release()
        }
        NetworkControllerVoice.registerVoiceCallback(null)
        super.onDestroyView()
    }

}