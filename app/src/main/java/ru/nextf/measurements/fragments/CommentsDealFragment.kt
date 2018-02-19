package ru.nextf.measurements.fragments

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.nextf.measurements.adapters.CommentAdapter
import ru.nextf.measurements.network.NetworkControllerComment
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.comments_measurement_fragment.view.*
import ru.nextf.measurements.*
import ru.nextf.measurements.modelAPI.*
import ru.nextf.measurements.network.NetworkController
import ru.nextf.measurements.network.NetworkControllerDeals

/**
 * Created by addd on 08.02.2018.
 */
class CommentsDealFragment : Fragment(), MyWebSocket.SocketCallback {
    private lateinit var mView: View
    private lateinit var deal: Deal
    private lateinit var bundle: Bundle
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CommentAdapter
    private lateinit var commentRequest: CommentRequest
    private var isSending = false
    private val handler = Handler()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater?.inflate(ru.nextf.measurements.R.layout.comments_measurement_fragment, container, false) ?: View(context)
        bundle = this.arguments
        myWebSocket.registerSocketCallback(this)
        getSavedDeal()
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
        myWebSocket.registerSocketCallback(this)
        mView.imageButtonSend.setOnClickListener { sendComment() }
        mView.refresh.setOnRefreshListener {
            mView.refresh.isRefreshing = true
            adapter = CommentAdapter(emptyList())
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
        return mView
    }

    override fun message(text: String) {
        val type = object : TypeToken<Event>() {}.type
        val event = gson.fromJson<Event>(text, type)
        if (event.event == "on_comment_deal") {
            val type = object : TypeToken<NewCommentDeal>() {}.type
            val newComment = gson.fromJson<NewCommentDeal>(gson.toJson(event.data), type)
            if (deal.id == newComment.id) {
                handler.post {
                    adapter.add(newComment.comment)
                    recycler.smoothScrollToPosition(adapter.itemCount - 1)
                }
                activity.setResult(200)
            }
        }
    }

    private fun displayComments() {
        adapter = CommentAdapter(deal.comments as ArrayList<Comment>)
        recycler = mView.recyclerViewComments
        mView.recyclerViewComments.adapter = adapter

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mView.recyclerViewComments.layoutManager = layoutManager

    }

    private fun sendComment() {
        if (mView.editTextCommentProblem.text.isEmpty()) {
            toast(ru.nextf.measurements.R.string.enter_comment)
        } else {
                commentRequest = CommentRequest(mView.editTextCommentProblem.text.toString())
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
        NetworkControllerComment.registerProblemPagination(null)
        super.onDestroyView()
    }

}