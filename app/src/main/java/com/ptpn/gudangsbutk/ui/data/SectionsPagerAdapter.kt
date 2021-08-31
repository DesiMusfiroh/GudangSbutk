@file:Suppress("DEPRECATION")

package com.ptpn.gudangsbutk.ui.data

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ptpn.gudangsbutk.R

class SectionsPagerAdapter(private val mContext: DataFragment, fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.data_saya, R.string.data_all)
    }

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> DataUserFragment()
            1 -> DataAllFragment()
            else -> Fragment()
        }

    override fun getPageTitle(position: Int): CharSequence = mContext.resources.getString(TAB_TITLES[position])

    override fun getCount(): Int = 2

}