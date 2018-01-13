package com.addd.measurements.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.method.ScrollingMovementMethod
import com.addd.measurements.*
import com.addd.measurements.adapters.StageAdapter
import com.addd.measurements.modelAPI.Mount
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_mount.*

class OneMountActivity : AppCompatActivity(), StageAdapter.CustomAdapterCallback {
    private lateinit var mount: Mount
    private var adapter: StageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_mount)
        setSupportActionBar(toolbarAst)
        getSaveMount()
        supportActionBar?.title = String.format("Монтаж %05d", mount.deal)
        displayMount()
    }

    private fun displayMount() {
        textViewCommentStage.movementMethod = ScrollingMovementMethod()
        adapter = mount.stages?.let { StageAdapter(it, this) }

        if (mount.date == null) {
            textViewDateStage.text = getString(R.string.mount_for_phone)
        } else {
            val strBuilder = StringBuilder(mount.date)
            strBuilder.replace(10, 11, " ")
            strBuilder.delete(16, strBuilder.length)
            val newStrBuilder = StringBuilder()
            newStrBuilder.append(" ")
            for (i in 0..10) {
                newStrBuilder.append(strBuilder[i])
            }
            textViewDateStage.text = newStrBuilder.toString()
        }

        when (mount.status) {
            0 -> textViewStatusMount.text = getString(R.string.not_processed)
            1 -> textViewStatusMount.text = getString(R.string.stage_added)
            2 -> textViewStatusMount.text = getString(R.string.closed_successful)
            3 -> textViewStatusMount.text = getString(R.string.closed_not_successful)
            else -> textViewStatusMount.text = getString(R.string.not_processed)
        }
        textViewCommentStage.movementMethod = ScrollingMovementMethod()
        if (!mount.comment.isNullOrEmpty()) {
            textViewCommentStage.text = mount.comment
        } else {
            textViewCommentStage.text = getString(R.string.comment_empty)
        }

        recyclerViewInstallers.adapter = adapter
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerViewInstallers.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(recyclerViewInstallers.context, layoutManager.orientation)
        recyclerViewInstallers.addItemDecoration(dividerItemDecoration)
    }

    override fun onItemClick(pos: Int) {
        val intent = Intent(applicationContext, OneStageActivity::class.java)
        val json = gson.toJson(mount.stages?.get(pos))
        intent.putExtra(STAGE_KEY, json)
        intent.putExtra(STAGE_COUNT, (pos + 1).toString())
        startActivityForResult(intent, 1)
    }

    private fun getSaveMount() {
        if (intent.hasExtra(MOUNT_NAME)) {
            val json = intent.getStringExtra(MOUNT_NAME)
            mount = if (json.isNullOrEmpty()) {
                Mount()
            } else {
                val type = object : TypeToken<Mount>() {
                }.type
                gson.fromJson(json, type)
            }
        } else {
            toast(R.string.error)
            finish()
        }
    }
}
