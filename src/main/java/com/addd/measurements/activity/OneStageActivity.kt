package com.addd.measurements.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.method.ScrollingMovementMethod
import com.addd.measurements.*
import com.addd.measurements.adapters.InstallerAdapter
import com.addd.measurements.modelAPI.Installers
import com.addd.measurements.modelAPI.Stage
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_stage.*

class OneStageActivity : AppCompatActivity() {
    private lateinit var stage: Stage
    private lateinit var countStage: String
    private lateinit var adapter: InstallerAdapter
    var emptyList: ArrayList<Installers> = ArrayList(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_stage)
        setSupportActionBar(toolbarAst)
        toolbarAst.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbarAst.setNavigationOnClickListener {
            finish()
        }
        textViewCommentStage.movementMethod = ScrollingMovementMethod()
        countStage = intent.getStringExtra(STAGE_COUNT)
        getSaveStage()
        supportActionBar?.title = "Этап $countStage"
        displayStage()

    }

    private fun getSaveStage() {
        countStage = intent?.getStringExtra(STAGE_COUNT) ?: getString(R.string.stage)
        val json = intent?.getStringExtra(STAGE_KEY)
        stage = if (json.isNullOrEmpty()) {
            Stage()
        } else {
            val type = object : TypeToken<Stage>() {
            }.type
            gson.fromJson(json, type)
        }
    }

    private fun displayStage() {
        textViewDateStage.text = formatDate(stage.date ?: "2000-20-20")

        if (!stage.comment.isNullOrEmpty()) {
            textViewCommentStage.text = stage.comment
        } else {
            textViewCommentStage.text = getString(R.string.comment_empty)
        }
        textViewStatusStage.text = if (stage.status == 0) getString(R.string.in_proccess) else getString(R.string.stage_completed)
        adapter = InstallerAdapter(stage.installers ?: emptyList)
        recyclerViewInstallers.adapter = adapter
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerViewInstallers.layoutManager = layoutManager

    }
}
