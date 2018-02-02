package com.addd.measurements.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.addd.measurements.MEASUREMENT_KEY
import com.addd.measurements.R
import com.addd.measurements.adapters.CommentAdapter
import com.addd.measurements.gson
import com.addd.measurements.modelAPI.Comment
import com.addd.measurements.modelAPI.CommentRequest
import com.addd.measurements.modelAPI.Measurement
import com.addd.measurements.network.NetworkControllerComment
import com.addd.measurements.toast
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.comments_measurement_fragment.view.*


/**
 * Created by addd on 02.02.2018.
 */
class CommentsMeasurementFragment : Fragment(), NetworkControllerComment.AddCommentCallback {
    private lateinit var mView: View
    private lateinit var measurement: Measurement
    private lateinit var bundle: Bundle
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CommentAdapter
    private lateinit var commentRequest: CommentRequest
    private lateinit var commentCallback: CommentCallback
    private var isSending = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        NetworkControllerComment.registerProblemPagination(this)
        mView = inflater?.inflate(R.layout.comments_measurement_fragment, container, false) ?: View(context)
        bundle = this.arguments
        getSavedMeasurement()
        displayComments()
        if (measurement.comments?.isNotEmpty() == true) {
            recycler.smoothScrollToPosition(
                    recycler.adapter.itemCount - 1)
        }
        recycler.addOnLayoutChangeListener({ _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                recycler.postDelayed({
                    if (measurement.comments?.isNotEmpty() == true) {
                        recycler.smoothScrollToPosition(
                                recycler.adapter.itemCount - 1)

                    }
                }, 100)
            }
        })
        mView.imageButtonSend.setOnClickListener { sendComment() }
        return mView
    }

    private fun displayComments() {
        adapter = CommentAdapter(measurement.comments as ArrayList<Comment>)
        recycler = mView.recyclerViewComments
        mView.recyclerViewComments.adapter = adapter

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mView.recyclerViewComments.layoutManager = layoutManager

        mView.editTextCommentProblem.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER -> {
                        sendComment()
                    }
                }
            }
            true
        }
    }

    private fun sendComment() {
        if (mView.editTextCommentProblem.text.isEmpty()) {
            toast(R.string.enter_comment)
        } else {
            if (!isSending) {
                isSending = true
                commentRequest = CommentRequest(mView.editTextCommentProblem.text.toString())
                NetworkControllerComment.addComment(commentRequest, measurement.id.toString()) // изменить
                adapter.addLoadingFooter()
                mView.editTextCommentProblem.setText(R.string.empty)
            }
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

    override fun addCommentResult(result: Boolean, comment: Comment?) {
        isSending = false
        when {
            comment == null -> {
                adapter.removeLoadingFooter()
                toast(R.string.error_add_comment)
            }
            result -> {
                toast(R.string.comment_added)
                adapter.removeLoadingFooter()
                adapter.add(comment)
                recycler.smoothScrollToPosition(adapter.itemCount - 1)
                activity.setResult(200)
            }
            else -> {
                toast(R.string.error_add_comment)
                adapter.removeLoadingFooter()
            }
        }
    }

    override fun onDestroyView() {
        commentCallback.getMeasurementComment(measurement)
        NetworkControllerComment.registerProblemPagination(null)
        super.onDestroyView()
    }

    interface CommentCallback {
        fun getMeasurementComment(measurement: Measurement)
    }

    fun registerCommentCallback(commentCallback1: CommentCallback) {
        this.commentCallback = commentCallback1
    }
}