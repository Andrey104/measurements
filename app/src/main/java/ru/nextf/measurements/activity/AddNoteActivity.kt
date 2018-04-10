package ru.nextf.measurements.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.add_note_activity.*
import ru.nextf.measurements.MEASUREMENT_KEY
import ru.nextf.measurements.R
import ru.nextf.measurements.gson
import ru.nextf.measurements.modelAPI.Measurement
import ru.nextf.measurements.network.NetworkNote
import ru.nextf.measurements.toast

/**
 * Created by left0ver on 10.04.18.
 */
class AddNoteActivity : AppCompatActivity(), NetworkNote.NoteEditCallback {
    override fun resultNoteEdit(result: Boolean) {
        if (result) {
            setResult(200)
            toast(R.string.note_added)
            finish()
        } else {
            toast(R.string.error)
        }
    }

    private lateinit var measurement: Measurement
    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkNote.registerNoteEditCallback(this)
        super.onCreate(savedInstanceState)
        setContentView(ru.nextf.measurements.R.layout.add_note_activity)
        getSavedMeasurement()
        if (measurement.note != null) {
            editComment.setText(measurement.note)
        }
        buttonOk.setOnClickListener { editComment() }

    }


    private fun editComment() {
        NetworkNote.editNote(measurement.id.toString(), editComment.text.toString())
    }

    private fun getSavedMeasurement() {
        val json = intent?.getStringExtra(MEASUREMENT_KEY)
        measurement = if (json.isNullOrEmpty()) {
            Measurement()
        } else {
            val type = object : TypeToken<Measurement>() {
            }.type
            gson.fromJson(json, type)
        }
    }
}