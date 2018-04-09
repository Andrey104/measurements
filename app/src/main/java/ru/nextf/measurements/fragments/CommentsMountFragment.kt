package ru.nextf.measurements.fragments

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.comments_mount_fragment.view.*
import ru.nextf.measurements.*
import ru.nextf.measurements.adapters.CommentAdapter
import ru.nextf.measurements.modelAPI.*
import ru.nextf.measurements.network.NetworkControllerComment
import ru.nextf.measurements.network.NetworkControllerDeals


/**
 * Created by addd on 02.02.2018.
 */
class CommentsMountFragment : Fragment(), MyWebSocket.SocketCallback,
        NetworkControllerDeals.MountCallback, NetworkControllerComment.AddCommentCallback {
    private var mountStr = ""
    private lateinit var mount: Mount
    private lateinit var mView: View
    private lateinit var adapter: CommentAdapter
    private lateinit var commentRequest: CommentRequest
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mountStr = arguments.getString(MOUNT_KEY)
        getSavedMeasurement()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        NetworkControllerDeals.registerMountCallback(this)
        NetworkControllerComment.registerCommentCallback(this)
        mView = inflater.inflate(R.layout.comments_mount_fragment, container, false)
        displayComments()
        if (mount.comments?.isNotEmpty() == true) {
            mView.recyclerViewComments.scrollToPosition(mView.recyclerViewComments.adapter.itemCount - 1)
        }
        mView.recyclerViewComments.addOnLayoutChangeListener({ _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                mView.recyclerViewComments.post({
                    if (mount.comments?.isNotEmpty() == true) {
                        mView.recyclerViewComments.scrollToPosition(mView.recyclerViewComments.adapter.itemCount - 1)
                    }
                })
            }
        })
        mView.imageButtonSend.setOnClickListener { sendComment() }
        mView.refresh.setOnRefreshListener {
            mView.refresh.isRefreshing = true
            adapter = CommentAdapter(emptyList(), activity)
            mView.recyclerViewComments.adapter = adapter
            NetworkControllerDeals.getMount(mount.id.toString())
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
        return mView
    }

    override fun onResume() {
        super.onResume()
        myWebSocket.registerSocketCallback(this)
    }

    override fun resultMount(mount: Mount, boolean: Boolean) {
        mView.refresh.isRefreshing = false
        if (boolean) {
            this.mount = mount
            displayComments()
        }
    }

    override fun message(text: String) {
        val type = object : TypeToken<Event>() {}.type
        val event = gson.fromJson<Event>(text, type)
        if (event.event == "on_comment_mount") {
            val type = object : TypeToken<NewCommentMount>() {}.type
            val newComment = gson.fromJson<NewCommentMount>(gson.toJson(event.data), type)
            val mSettings: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            if ((mount.id == newComment.id) &&
                    (newComment.comment.user.id != mSettings.getInt(MY_ID_USER, 0))) {
                handler.post {
                    adapter.add(newComment.comment)
                    mView.recyclerViewComments.smoothScrollToPosition(adapter.itemCount - 1)
                }
                activity.setResult(200)
            }
        }
    }

    private fun displayComments() {
        adapter = CommentAdapter(mount.comments as ArrayList<Comment>, activity)
        mView.recyclerViewComments.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mView.recyclerViewComments.layoutManager = layoutManager

    }

    private fun sendComment() {
        if (mView.editTextCommentProblem.text.isEmpty()) {
            toast(ru.nextf.measurements.R.string.enter_comment)
        } else {
            commentRequest = CommentRequest(mView.editTextCommentProblem.text.toString())
            NetworkControllerComment.addCommentMount(commentRequest, mount.id.toString())
            mView.editTextCommentProblem.setText(ru.nextf.measurements.R.string.empty)
        }
    }

    private fun refreshComments() {
        handler.post {
            adapter = CommentAdapter(mount.comments as ArrayList<Comment>, activity)
            mView.recyclerViewComments.adapter = adapter
            adapter.notifyDataSetChanged()

            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            mView.recyclerViewComments.layoutManager = layoutManager
            mView.recyclerViewComments.scrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun addCommentResult(result: Boolean, comment: Comment?) {
        if (result) {
            if (comment != null) {
                (mount.comments as ArrayList).add(comment)
            }
            refreshComments()
        } else {
            toast(R.string.error_add_comment)
        }
    }

    private fun getSavedMeasurement() {
        mount = if (mountStr.isNullOrEmpty()) {
            Mount()
        } else {
            val type = object : TypeToken<Mount>() {
            }.type
            gson.fromJson(mountStr, type)
        }
    }
}