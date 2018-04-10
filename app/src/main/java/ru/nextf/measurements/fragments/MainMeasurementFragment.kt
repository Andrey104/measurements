package ru.nextf.measurements.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.dialog_address_comment.view.*
import kotlinx.android.synthetic.main.dialog_description.view.*
import kotlinx.android.synthetic.main.dialog_time.view.*
import kotlinx.android.synthetic.main.main_measurement_fragment.view.*
import ru.nextf.measurements.MEASUREMENT_KEY
import ru.nextf.measurements.adapters.ClientAdapter
import ru.nextf.measurements.formatDate
import ru.nextf.measurements.gson
import ru.nextf.measurements.modelAPI.Measurement
import ru.nextf.measurements.network.NetworkController


/**
 * Created by addd on 30.01.2018.
 */
class MainMeasurementFragment : Fragment() {
    private lateinit var measurement: Measurement
    private lateinit var bundle: Bundle
    private lateinit var fabOpen: Animation
    private lateinit var fabOpen08: Animation
    private lateinit var fabClose: Animation
    private lateinit var textClose: Animation
    private lateinit var textOpen: Animation
    private lateinit var mView: View
    private var isFabOpen = false
    private var ONLY_DEAL = false
    private var mainMF: MainMF? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater?.inflate(ru.nextf.measurements.R.layout.main_measurement_fragment, container, false)
                ?: View(context)
        fabOpen = AnimationUtils.loadAnimation(context, ru.nextf.measurements.R.anim.fab_open)
        fabOpen08 = AnimationUtils.loadAnimation(context, ru.nextf.measurements.R.anim.fab_open_08)
        fabClose = AnimationUtils.loadAnimation(context, ru.nextf.measurements.R.anim.fab_close)
        textOpen = AnimationUtils.loadAnimation(context, ru.nextf.measurements.R.anim.text_open)
        textClose = AnimationUtils.loadAnimation(context, ru.nextf.measurements.R.anim.text_close)

        bundle = this.arguments
        measurement = if (bundle.containsKey(MEASUREMENT_KEY)) {
            getSavedMeasurement()
        } else {
            Measurement()
        }

        displayMeasurement(measurement)

        mView.fabComplete.setOnClickListener {
            hideFub()
            mainMF?.complete()
        }

        mView.fabReject.setOnClickListener {
            hideFub()
            mainMF?.reject()
        }

        mView.fabTransfer.setOnClickListener {
            hideFub()
            mainMF?.transfer()
        }

        mView.floatingActionButtonNote.setOnClickListener {
            hideFub()
            mainMF?.addNote()
        }

        mView.fabGoDeal.setOnClickListener {
            closeOnlyDealFAB()
            mainMF?.goDeal()
        }

        mView.mainConstraintLayout.setOnTouchListener { _, _ ->
            if (isFabOpen) {
                if (ONLY_DEAL) {
                    closeOnlyDealFAB()
                } else {
                    hideFub()
                }
            }
            false
        }

        mView.recycleClient.setOnTouchListener { _, _ ->
            if (isFabOpen) {
                if (ONLY_DEAL) {
                    closeOnlyDealFAB()
                } else {
                    hideFub()
                }
            }
            false
        }

        mView.fabResponsible.setOnClickListener {
            becomeResponsible()
        }

        setDialogs()

        return mView
    }

    private fun becomeResponsible() {
        val ad = android.app.AlertDialog.Builder(context)
        ad.setTitle(ru.nextf.measurements.R.string.become_response)  // заголовок
        var id = measurement.id
        ad.setPositiveButton(ru.nextf.measurements.R.string.yes) { _, _ ->
            if (id != null) {
                NetworkController.becomeResponsible(id)
            }
        }
        ad.setNegativeButton(ru.nextf.measurements.R.string.cancel) { _, _ -> }

        ad.setCancelable(true)
        ad.show()
    }

    private fun setDialogs() {
        mView.constraint_description.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val view = layoutInflater.inflate(ru.nextf.measurements.R.layout.dialog_description, null)
            view.textViewDescription.text = measurement.description
            builder.setView(view)
                    .setCancelable(true)
                    .setPositiveButton(ru.nextf.measurements.R.string.okay)
                    { dialog, _ ->
                        dialog.cancel()
                    }
            val alert = builder.create()
            alert.show()
        }
        mView.constraint_time.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val view = layoutInflater.inflate(ru.nextf.measurements.R.layout.dialog_time, null)
            view.textViewTime.text = measurement.time
            builder.setView(view)
                    .setCancelable(true)
                    .setPositiveButton(ru.nextf.measurements.R.string.okay)
                    { dialog, _ ->
                        dialog.cancel()
                    }
            val alert = builder.create()
            alert.show()
        }
        mView.constraint_address.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val view = layoutInflater.inflate(ru.nextf.measurements.R.layout.dialog_address_comment, null)
            view.textViewAddress.text = measurement.address
            if (measurement.addressComment == null || measurement.addressComment == "") {
                view.textView10.text = "Комментарий отсутствует"
            } else {
                view.textViewAddressComment.text = measurement.addressComment
            }
            view.textViewAddress.setOnClickListener {
                openNavigator()
            }
            view.open_navigator.setOnClickListener {
                openNavigator()
            }
            builder.setView(view)
                    .setCancelable(true)
                    .setPositiveButton(ru.nextf.measurements.R.string.okay)
                    { dialog, _ ->
                        dialog.cancel()
                    }
            val alert = builder.create()
            alert.show()
        }
    }

    fun openNavigator() {
        val uri = Uri.parse("yandexnavi://map_search?text=${measurement.address}")
        var intent = Intent(Intent.ACTION_VIEW, uri)
        intent.`package` = "ru.yandex.yandexnavi"

        // Проверяет, установлено ли приложение.
        val packageManager = activity.packageManager
        val activities = packageManager.queryIntentActivities(intent, 0)
        val isIntentSafe = activities.size > 0
        if (isIntentSafe) {

            //Запускает Яндекс.Навигатор.
            startActivity(intent)
        } else {

            // Открывает страницу Яндекс.Навигатора в Google Play.
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=ru.yandex.yandexnavi")
            startActivity(intent)
        }
    }


    private fun displayMeasurement(measurement: Measurement) {
        if (measurement.color == 2 && measurement.note != null && measurement.note != "") {
            mView.constraint_note.visibility = View.VISIBLE
            mView.note_text.text = measurement.note
            mView.constraint_note.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                val view = layoutInflater.inflate(ru.nextf.measurements.R.layout.dialog_description, null)
                view.textViewDescription.text = measurement.note
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
            mView.constraint_note.visibility = View.GONE
        }
        if (measurement.color != 2) {
            mView.fabMain.hide()
            mView.fabResponsible.visibility = View.VISIBLE
        }

        if (measurement.description == null || measurement.description == "") {
            mView.constraint_description.visibility = View.GONE
        } else {
            mView.description_text.text = measurement.description
        }

        val mp = ResourcesCompat.getDrawable(ru.nextf.measurements.MyApp.instance.resources, ru.nextf.measurements.R.drawable.mp, null)
        val n = ResourcesCompat.getDrawable(ru.nextf.measurements.MyApp.instance.resources, ru.nextf.measurements.R.drawable.n, null)
        val b = ResourcesCompat.getDrawable(ru.nextf.measurements.MyApp.instance.resources, ru.nextf.measurements.R.drawable.b, null)
        val unknown = ResourcesCompat.getDrawable(ru.nextf.measurements.MyApp.instance.resources, ru.nextf.measurements.R.drawable.unknown, null)

        when (measurement.company?.symbol) {
            "МП", "MP" -> mView.symbol.background = mp
            "Б", "B" -> mView.symbol.background = b
            "Н", "H" -> mView.symbol.background = n
            else -> mView.symbol.background = unknown
        }

        if (measurement.sum == null) {
            mView.constraintLayoutHide.visibility = View.GONE
        } else {
            if (measurement.prepayment == null) {
                mView.textViewSum.text = "${String.format("%,.2f", measurement.sum)}р"
            } else {
                mView.textViewSum.text = "${String.format("%,.2f", measurement.sum)}р (Предоплата: ${String.format("%,.2f", measurement.prepayment)}р)"
            }
        }



        mView.recycleClient.adapter = ClientAdapter(measurement.clients
                ?: emptyList(), layoutInflater, activity)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mView.recycleClient.layoutManager = layoutManager

        mView.address.text = measurement.address
        mView.time.text = measurement.time
        mView.date.text = formatDate(measurement.date ?: "2000-20-20")
        if (measurement.worker == null) {
            mView.worker_name.text = getString(ru.nextf.measurements.R.string.not_distributed)
        } else {
            mView.worker_name.text = measurement.worker.firstName + " " + measurement.worker.lastName
        }
        setColorWorker(measurement)
        setStatus(measurement)
        if (measurement.addressComment.isNullOrEmpty()) {
            mView.comment.visibility = View.GONE
        } else {
            mView.comment.text = measurement.addressComment.toString()
        }
    }

    private fun selectColorVersion(item: TextView, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            item.setTextColor(resources.getColor(color, context.theme))
        } else {
            item.setTextColor(resources.getColor(color))
        }
    }

    private fun setColorWorker(measurement: Measurement) {
        when (measurement.color) {
            1 -> selectColorVersion(mView.worker_name, ru.nextf.measurements.R.color.red)
            2 -> selectColorVersion(mView.worker_name, ru.nextf.measurements.R.color.green)
            3 -> selectColorVersion(mView.worker_name, ru.nextf.measurements.R.color.blue)
        }
    }

    private fun setStatus(measurement: Measurement) {
        when (measurement.status) {
            0, 1 -> {
                ONLY_DEAL = false
                mView.textViewStatus.text = getString(ru.nextf.measurements.R.string.measurement_not_closed)
                mView.fabMain.setOnClickListener { showFubs() }
                mView.fabMainClose.setOnClickListener { hideFub() }
            }
            2, 3 -> {
                ONLY_DEAL = true
                if (measurement.status == 2) {
                    mView.textViewStatus.text = getString(ru.nextf.measurements.R.string.not_contract)
                    selectColorVersion(mView.textViewStatus, ru.nextf.measurements.R.color.red)
                }
                if (measurement.status == 3) {
                    mView.textViewStatus.text = getString(ru.nextf.measurements.R.string.measurement_closed_success)
                    selectColorVersion(mView.textViewStatus, ru.nextf.measurements.R.color.green)
                }
                mView.fabMain.setOnClickListener {
                    isFabOpen = true
                    mView.fabMainClose.startAnimation(fabOpen)
                    mView.fabGoDeal.startAnimation(fabOpen08)
                    mView.textViewGoDeal.startAnimation(textOpen)
                    mView.fabMainClose.isClickable = true
                    mView.fabGoDeal.isClickable = true
                    mView.fabMain.isClickable = false
                }
                mView.fabMainClose.setOnClickListener {
                    closeOnlyDealFAB()
                }
            }
            4, 5 -> {
                ONLY_DEAL = false
                mView.textViewStatus.text = getString(ru.nextf.measurements.R.string.measurement_reject)
                selectColorVersion(mView.textViewStatus, ru.nextf.measurements.R.color.red)
                mView.fabMain.hide()
                mView.fabTransfer.hide()
                mView.fabReject.hide()
                mView.fabComplete.hide()
            }
        }
    }

    private fun closeOnlyDealFAB() {
        if (isFabOpen) {
            mView.fabMainClose.startAnimation(fabClose)
            mView.fabGoDeal.startAnimation(fabClose)
            mView.textViewGoDeal.startAnimation(textClose)
            mView.fabMain.startAnimation(fabOpen)
            mView.fabMainClose.isClickable = false
            mView.fabGoDeal.isClickable = false
            mView.fabMain.isClickable = true
        }
    }

    private fun hideFub() {
        if (isFabOpen) {
            mView.fabComplete.startAnimation(fabClose)
            mView.fabMainClose.startAnimation(fabClose)
            mView.fabReject.startAnimation(fabClose)
            mView.fabTransfer.startAnimation(fabClose)
            mView.floatingActionButtonNote.startAnimation(fabClose)
            mView.fabMain.startAnimation(fabOpen)
            mView.textViewComplete.startAnimation(textClose)
            mView.textViewReject.startAnimation(textClose)
            mView.textViewNote.startAnimation(textClose)
            mView.textViewTransfer.startAnimation(textClose)
            mView.fabComplete.isClickable = false
            mView.fabMainClose.isClickable = false
            mView.fabReject.isClickable = false
            mView.fabTransfer.isClickable = false
            mView.fabMain.isClickable = true
            isFabOpen = false
        }
    }

    private fun showFubs() {
        mView.fabMain.isClickable = false
        mView.fabMain.startAnimation(fabClose)
        mView.fabMainClose.startAnimation(fabOpen)
        mView.fabTransfer.startAnimation(fabOpen08)
        mView.fabReject.startAnimation(fabOpen08)
        mView.fabComplete.startAnimation(fabOpen08)
        mView.floatingActionButtonNote.startAnimation(fabOpen08)
        mView.textViewComplete.startAnimation(textOpen)
        mView.textViewReject.startAnimation(textOpen)
        mView.textViewNote.startAnimation(textOpen)
        mView.textViewTransfer.startAnimation(textOpen)
        mView.fabReject.isClickable = true
        mView.fabMainClose.isClickable = true
        mView.fabComplete.isClickable = true
        mView.fabTransfer.isClickable = true
        isFabOpen = true

    }

    private fun getSavedMeasurement(): Measurement {
        val json = bundle.getString(MEASUREMENT_KEY)
        measurement = if (json.isNullOrEmpty()) {
            Measurement()
        } else {
            val type = object : TypeToken<Measurement>() {
            }.type
            gson.fromJson(json, type)
        }
        return measurement
    }

    interface MainMF {
        fun addNote()
        fun complete()
        fun reject()
        fun transfer()
        fun goDeal()
    }

    fun registerMainMF(mainMF: MainMF) {
        this.mainMF = mainMF
    }

    override fun onDestroy() {
        mainMF = null
        super.onDestroy()
    }
}