package com.addd.measurements.activity

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.addd.measurements.PROBLEM_KEY
import com.addd.measurements.R
import com.addd.measurements.adapters.CommentAdapter
import com.addd.measurements.gson
import com.addd.measurements.modelAPI.Comment
import com.addd.measurements.modelAPI.CommentRequest
import com.addd.measurements.modelAPI.MyProblem
import com.addd.measurements.modelAPI.User
import com.addd.measurements.network.NetworkControllerComment
import com.addd.measurements.network.NetworkControllerProblem
import com.addd.measurements.toast
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_problem.*
import kotlinx.android.synthetic.main.content_one_problem.*


class OneProblemActivity : AppCompatActivity(),
        NetworkControllerComment.AddCommentCallback,
        TextView.OnEditorActionListener {
    private lateinit var problem: MyProblem
    private lateinit var commentRequest: CommentRequest
    private lateinit var adapter: CommentAdapter
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkControllerComment.registerProblemPagination(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_problem)
        setSupportActionBar(toolbar)
        imageButtonSend.setOnClickListener { sendComment() }
        getSavedProblem()
        displayProblem()
    }

    private fun displayProblem() {
        headerProblem.text = problem.title
        answerProblem.text = if (problem.status == 0) getString(R.string.expect_answer) else getString(R.string.thereis_answer)
        if (problem.status == 0) {
            imageViewProblem.setImageResource(R.drawable.ic_access_time_black_24dp)
        } else {
            imageViewProblem.setImageResource(R.drawable.ic_check_black_24dp)
        }

        adapter = CommentAdapter(problem.comments as ArrayList<Comment>)
        recycler = recyclerViewComments
        recyclerViewComments.adapter = adapter
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerViewComments.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerViewComments.context, layoutManager.orientation)
        recyclerViewComments.addItemDecoration(dividerItemDecoration)

        editTextCommentProblem.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER -> {
                        sendComment()
                    }
                }
            }
            false
        }

        desctiptionProblem.text = problem.description
        desctiptionProblem.movementMethod = ScrollingMovementMethod()
    }

    private fun getSavedProblem() {
        val json = intent.getStringExtra(PROBLEM_KEY)
        problem = if (json.isNullOrEmpty()) {
            MyProblem(0, "0", User(), "0", 0, "0", "0", 0, null)
        } else {
            val type = object : TypeToken<MyProblem>() {
            }.type
            gson.fromJson(json, type)
        }
    }

    private fun sendComment() {
        if (editTextCommentProblem.text.isEmpty()) {
            toast(R.string.enter_comment)
        } else {
            commentRequest = CommentRequest(editTextCommentProblem.text.toString())
            NetworkControllerComment.addComment(commentRequest, problem.id.toString())
            adapter.addLoadingFooter()
            editTextCommentProblem.setText(R.string.empty)
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        commentRequest = CommentRequest(editTextCommentProblem.text.toString())
        NetworkControllerComment.addComment(commentRequest, problem.id.toString())
        adapter.addLoadingFooter()
        return true
    }


    override fun addCommentResult(result: Boolean, comment: Comment?) {
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
                setResult(200)
            }
            else -> {
                toast(R.string.error_add_comment)
                adapter.removeLoadingFooter()
            }
        }
    }


    override fun onDestroy() {
        NetworkControllerComment.registerProblemPagination(null)
        super.onDestroy()
    }
}
