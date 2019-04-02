package com.verve.vps.fragments

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.google.android.material.tabs.TabLayout
import com.verve.vps.R
import com.verve.vps.adapters.OBReportTabsAdapter
import com.verve.vps.helpers.Constants.customViewPager
import com.verve.vps.utils.Utils


class OBReportFragment : Fragment() {

    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tab_layout, container, false)
    }

    override fun onResume(){
        super.onResume()

        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext

        val args = arguments
        parentActivity.supportActionBar!!.title = args!!.getString("title", "")

        val tabLayout: TabLayout = parentActivity.findViewById(R.id.tab_layout)
        customViewPager = parentActivity.findViewById(R.id.view_pager)
        customViewPager.isPagingEnabled = false
        val adapter = OBReportTabsAdapter(childFragmentManager,"report",mContext)

        customViewPager.adapter = adapter
        tabLayout.setupWithViewPager(customViewPager)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        // disable the tabs
        Utils.disableTabs(tabLayout)

    }


}