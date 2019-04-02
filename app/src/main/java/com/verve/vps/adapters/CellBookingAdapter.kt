package com.verve.vps.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import com.verve.vps.fragments.*

class CellBookingAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = ArresteeDetailsFragment.newInstance()
            1 -> fragment = ArresteeBiometricFragment.newInstance()
            2 -> fragment = ArresteeDetailsFragment.newInstance()
        }
        return fragment!!
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Arrestee Details"
        1 -> "Biometric"
        2 -> "Occurrence"
        else -> ""
    }

    override fun getCount()  = 3

}