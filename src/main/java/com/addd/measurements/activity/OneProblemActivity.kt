package com.addd.measurements.activity

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.EditText
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


class OneProblemActivity : AppCompatActivity(), NetworkControllerComment.AddCommentCallback, NetworkControllerProblem.OneProblem {
    private lateinit var problem: MyProblem

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkControllerComment.registerProblemPagination(this)
        NetworkControllerProblem.registerGetOneProblemCallback(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_problem)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            showAlert()
        }

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


        recyclerViewComments.adapter = CommentAdapter(problem.comments as ArrayList<Comment>)
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerViewComments.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerViewComments.context, layoutManager.orientation)
        recyclerViewComments.addItemDecoration(dividerItemDecoration)

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

    private fun showAlert() {
        val alert = AlertDialog.Builder(this)

        alert.setTitle(R.string.enter_comment)

        val input = EditText(this)
        alert.setView(input)

        alert.setPositiveButton(R.string.okay) { _, _ ->
            NetworkControllerComment.addComment(CommentRequest(input.text.toString()), problem.id.toString())
        }

        alert.setNegativeButton(R.string.cancel) { _, _ ->
            // Canceled.
        }

        alert.show()
    }

    override fun resultGetOneProblem(problem: MyProblem?) {
        if (problem != null) {
            this.problem = problem
            displayProblem()
        } else {
            toast(R.string.update_error)
        }
        progressBar2.visibility = View.GONE
    }

    override fun addCommentResult(result: Boolean) {
        if (result) {
            toast(R.string.comment_added)
            progressBar2.visibility = View.VISIBLE
            NetworkControllerProblem.getOneProblem(problem.id.toString())
            setResult(200)
        } else {
            toast(R.string.error_add_comment)
        }
    }

    override fun onDestroy() {
        NetworkControllerComment.registerProblemPagination(null)
        NetworkControllerProblem.registerGetOneProblemCallback(null)
        super.onDestroy()
    }
}
