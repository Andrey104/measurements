package com.addd.measurements.activity

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import com.addd.measurements.R
import com.addd.measurements.adapters.CommentAdapter
import com.addd.measurements.gson
import com.addd.measurements.modelAPI.Comment
import com.addd.measurements.modelAPI.MyProblem
import com.addd.measurements.network.NetworkControllerComment
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_problem.*
import kotlinx.android.synthetic.main.content_one_problem.*
import android.view.View
import android.widget.EditText
import com.addd.measurements.modelAPI.CommentRequest
import com.addd.measurements.network.NetworkControllerProblem


class OneProblemActivity : AppCompatActivity(), NetworkControllerComment.AddCommentCallback, NetworkControllerProblem.OneProblem {
    override fun resultGetOneProblem(problem: MyProblem?) {
        if (problem != null) {
            this.problem = problem
            displayProblem()
        } else {
            Toast.makeText(applicationContext,"При обновлении произошла ошибка", Toast.LENGTH_SHORT).show()
        }
        progressBar2.visibility = View.GONE
    }

    override fun addCommentResult(result: Boolean) {
        if (result) {
            Toast.makeText(applicationContext, "Комментарий добавлен", Toast.LENGTH_SHORT).show()
            progressBar2.visibility = View.VISIBLE
            NetworkControllerProblem.getOneProblem(problem.id.toString())
            setResult(200)
        } else {
            Toast.makeText(applicationContext, "При добавлении комментария произошла ошибка", Toast.LENGTH_SHORT).show()
        }
    }

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

    fun displayProblem() {
        headerProblem.text = problem.title
        answerProblem.text = if (problem.status == 0) "Ожидает ответа" else "Ответ есть"
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
        if (intent != null && intent.hasExtra("problem")) {
            val json = intent.getStringExtra("problem")
            if (json.isEmpty()) {
                problem = MyProblem(0, "0", "0", "0", 0, "0", "0", 0, null)
            } else {
                val type = object : TypeToken<MyProblem>() {
                }.type
                problem = gson.fromJson(json, type)
            }
        }
    }

    private fun showAlert() {
        val alert = AlertDialog.Builder(this)

        alert.setTitle("Введите комментарий")

// Set an EditText view to get user input
        val input = EditText(this)
        alert.setView(input)

        alert.setPositiveButton("Ok") { dialog, whichButton ->
            NetworkControllerComment.addComment(CommentRequest(input.text.toString()), problem.id.toString())
        }

        alert.setNegativeButton("Cancel") { dialog, whichButton ->
            // Canceled.
        }

        alert.show()
    }

    override fun onStop() {
        NetworkControllerComment.registerProblemPagination(null)
        NetworkControllerProblem.registerGetOneProblemCallback(null)
        super.onStop()
    }
}
