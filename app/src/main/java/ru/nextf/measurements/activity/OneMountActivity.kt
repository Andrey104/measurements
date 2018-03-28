package ru.nextf.measurements.activity

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_one_mount.*
import kotlinx.android.synthetic.main.dialog_description.view.*
import ru.nextf.measurements.MOUNT_NAME
import ru.nextf.measurements.adapters.FragmentPagerAdapter
import ru.nextf.measurements.adapters.StageAdapter
import ru.nextf.measurements.formatDate
import ru.nextf.measurements.gson
import ru.nextf.measurements.modelAPI.Event
import ru.nextf.measurements.modelAPI.Mount
import ru.nextf.measurements.modelAPI.NewCommentMount
import ru.nextf.measurements.toast


class OneMountActivity : AppCompatActivity() {
    private lateinit var mount: Mount

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

        viewPager.adapter = FragmentPagerAdapter(supportFragmentManager, gson.toJson(mount))
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun displayMount() {

        if (mount.date == null) {
            textViewDateStage.text = getString(ru.nextf.measurements.R.string.mount_for_phone)
        } else {
            textViewDateStage.text = formatDate(mount.date ?: "2000-20-20")
        }

        when (mount.status) {
            0 -> textViewStatusMount.text = getString(ru.nextf.measurements.R.string.added)
            1 -> textViewStatusMount.text = getString(ru.nextf.measurements.R.string.in_progress)
            2 -> textViewStatusMount.text = getString(ru.nextf.measurements.R.string.closed_successful)
            3 -> textViewStatusMount.text = getString(ru.nextf.measurements.R.string.closed_not_successful)
            else -> textViewStatusMount.text = getString(ru.nextf.measurements.R.string.not_processed)
        }
        if (!mount.description.isNullOrEmpty()) {
            textViewCommentMount.text = mount.description
            textViewCommentMount.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                val view = layoutInflater.inflate(ru.nextf.measurements.R.layout.dialog_description, null)
                view.textViewDescription.text = mount.description
                builder.setView(view)
                        .setCancelable(true)
                        .setPositiveButton(ru.nextf.measurements.R.string.okay)
                        { dialog, _ ->
                            dialog.cancel()
                        }
                val alert = builder.create()
                alert.show()
            }
        } else {
            textViewCommentMount.text = getString(ru.nextf.measurements.R.string.comment_empty)
        }

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
