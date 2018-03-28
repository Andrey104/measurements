package ru.nextf.measurements.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.installers_mount_fragment.view.*
import ru.nextf.measurements.MOUNT_KEY
import ru.nextf.measurements.adapters.InstallerAdapter
import ru.nextf.measurements.gson
import ru.nextf.measurements.modelAPI.Installers
import ru.nextf.measurements.modelAPI.Mount

/**
 * Created by left0ver on 28.03.18.
 */
class InstallersMountFragment : Fragment() {
    private var mountStr = ""
    private lateinit var mount:Mount
    private lateinit var mView: View
    private lateinit var adapter: InstallerAdapter
    var emptyList: ArrayList<Installers> = ArrayList(emptyList())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mountStr = arguments.getString(MOUNT_KEY)
        getSavedMeasurement()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mView = inflater?.inflate(ru.nextf.measurements.R.layout.installers_mount_fragment, container, false)
                ?: View(context)
        println(mountStr)
        //исправить, когда Саня сделает
        adapter = InstallerAdapter(mount.stages!![0].installers ?: emptyList)
        mView.recyclerViewInstallers.adapter = adapter
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mView.recyclerViewInstallers.layoutManager = layoutManager
        return mView
    }

    private fun getSavedMeasurement() {
        mount = if (mountStr.isNullOrEmpty()) {
            Mount()
        } else {
            val type = object : TypeToken<Mount>() {
            }.type
            gson.fromJson(mountStr, type)
        }
    }
}