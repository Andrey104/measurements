package ru.nextf.measurements.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.method.ScrollingMovementMethod
import ru.nextf.measurements.adapters.StageAdapter
import ru.nextf.measurements.modelAPI.Mount
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_mount.*
import ru.nextf.measurements.*

class OneMountActivity : AppCompatActivity(), StageAdapter.CustomAdapterCallback {
    private lateinit var mount: Mount
    private var adapter: StageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ru.nextf.measurements.R.layout.activity_one_mount)
        setSupportActionBar(toolbarAst)
        toolbarAst.setNavigationIcon(ru.nextf.measurements.R.drawable.ic_arrow_back_white_24dp)
        toolbarAst.setNavigationOnClickListener {
            finish()
        }
        getSaveMount()
        supportActionBar?.title = String.format("Монтаж %05d", mount.deal)
        displayMount()
    }

    private fun displayMount() {
        textViewCommentStage.movementMethod = ScrollingMovementMethod()
        adapter = mount.stages?.let { StageAdapter(it, this) }

        if (mount.date == null) {
            textViewDateStage.text = getString(ru.nextf.measurements.R.string.mount_for_phone)
        } else {
            textViewDateStage.text = formatDate(mount.date ?: "2000-20-20")
        }

        when (mount.status) {
            0 -> textViewStatusMount.text = getString(ru.nextf.measurements.R.string.not_processed)
            1 -> textViewStatusMount.text = getString(ru.nextf.measurements.R.string.stage_added)
            2 -> textViewStatusMount.text = getString(ru.nextf.measurements.R.string.closed_successful)
            3 -> textViewStatusMount.text = getString(ru.nextf.measurements.R.string.closed_not_successful)
            else -> textViewStatusMount.text = getString(ru.nextf.measurements.R.string.not_processed)
        }
        textViewCommentStage.movementMethod = ScrollingMovementMethod()
        if (!mount.comment.isNullOrEmpty()) {
            textViewCommentStage.text = mount.comment
        } else {
            textViewCommentStage.text = getString(ru.nextf.measurements.R.string.comment_empty)
        }

        recyclerViewInstallers.adapter = adapter
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerViewInstallers.layoutManager = layoutManager
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
            toast(ru.nextf.measurements.R.string.error)
            finish()
        }
    }
}
