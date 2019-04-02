package com.verve.vps.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import com.verve.vps.fragments.*

class ExhibitTabsAdapter(fm: FragmentManager, private val caller: String) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {

        var fragment: Fragment? = null
        when {
            caller == "main" && position == 0 -> fragment =  AddExhibitFragment.newInstance()
        }

        when {
            caller == "main" && position == 1 -> fragment = ExhibitListingFragment.newInstance()
        }

        when {
            caller == "details" && position == 0 -> fragment = ViewExhibitFragment.newInstance()
        }

        when {
            caller == "details" && position == 1 -> fragment = ExhibitActionsFragment.newInstance()
        }

        return fragment!!
    }

    override fun getPageTitle(position: Int): CharSequence = when {
        caller == "main" && position == 0 -> "Add"
        caller == "main" && position == 1  -> "List"
        caller == "details" && position == 0 -> "Details"
        caller == "details" && position == 1  -> "Actions"

        else -> ""
    }

    override fun getCount()  = 2

}