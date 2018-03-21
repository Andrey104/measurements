package ru.nextf.measurements.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.method.ScrollingMovementMethod
import ru.nextf.measurements.adapters.InstallerAdapter
import ru.nextf.measurements.modelAPI.Installers
import ru.nextf.measurements.modelAPI.Stage
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_stage.*
import ru.nextf.measurements.STAGE_COUNT
import ru.nextf.measurements.STAGE_KEY
import ru.nextf.measurements.formatDate
import ru.nextf.measurements.gson

class OneStageActivity : AppCompatActivity() {
    private lateinit var stage: Stage
    private lateinit var countStage: String
    private lateinit var adapter: InstallerAdapter
    var emptyList: ArrayList<Installers> = ArrayList(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ru.nextf.measurements.R.layout.activity_one_stage)
        setSupportActionBar(toolbarAst)
        toolbarAst.setNavigationIcon(ru.nextf.measurements.R.drawable.ic_arrow_back_white_24dp)
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
        countStage = intent?.getStringExtra(STAGE_COUNT) ?: getString(ru.nextf.measurements.R.string.stage)
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

        if (!stage.description.isNullOrEmpty()) {
            textViewCommentStage.text = stage.description
        } else {
            textViewCommentStage.text = getString(ru.nextf.measurements.R.string.comment_empty)
        }
        textViewStatusStage.text = if (stage.status == 0) getString(ru.nextf.measurements.R.string.in_proccess) else getString(ru.nextf.measurements.R.string.stage_completed)
        adapter = InstallerAdapter(stage.installers ?: emptyList)
        recyclerViewInstallers.adapter = adapter
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerViewInstallers.layoutManager = layoutManager

    }
}
