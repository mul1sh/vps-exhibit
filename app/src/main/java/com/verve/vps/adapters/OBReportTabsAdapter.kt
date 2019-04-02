package com.verve.vps.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import com.verve.vps.R
import com.verve.vps.fragments.*

class OBReportTabsAdapter(fm: FragmentManager,
                          private val caller: String,
                          private val context: Context) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null

        when {
            caller == context.getString(R.string.tab_fragment_report_caller) && position == 0 -> fragment =  OBReportReporterFragment.newInstance()
        }

        when {
            caller == context.getString(R.string.tab_fragment_report_caller) && position == 1 -> fragment = OBReportOccurrenceFragment.newInstance()
        }

        when {
            caller == context.getString(R.string.tab_fragment_summary_caller) && position == 0 -> fragment = OBReportSummaryReporterFragment.newInstance()
        }

        when {
            caller == context.getString(R.string.tab_fragment_summary_caller) && position == 1 -> fragment = OBReportSummaryOccurrenceFragment.newInstance()
        }

        when {
            caller == context.getString(R.string.tab_fragment_summary_caller) && position == 2 -> fragment = ViewOBActionsFragment.newInstance()
        }


        return fragment!!
    }



    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> context.getString(R.string.tab_header_reporter)
        1 -> context.getString(R.string.tab_header_occurrence)
        2 -> context.getString(R.string.ob_actions)
        else -> ""
    }

    override fun getCount(): Int {
        var tabsCount = 2
        when (caller) {
            context.getString(R.string.tab_fragment_report_caller) -> tabsCount =  2
        }
        when (caller) {
            context.getString(R.string.tab_fragment_summary_caller) -> tabsCount =  3
        }

        return tabsCount
    }

}