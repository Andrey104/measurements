package ru.nextf.measurements.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import ru.nextf.measurements.modelAPI.Mount
import ru.nextf.measurements.newInstanceComment
import ru.nextf.measurements.newInstanceMount


/**
 * Created by left0ver on 28.03.18.
 */
class FragmentPagerAdapter(fm: FragmentManager, mountStr: String) : FragmentPagerAdapter(fm) {
    private val mTabTitles = arrayOf("Монтажники", "Комментарии")
    private val mount: String = mountStr

    override fun getCount(): Int {
        return mTabTitles.size
    }

    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            return newInstanceMount(mount)
        }
        return newInstanceComment(mount)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mTabTitles[position]
    }
}