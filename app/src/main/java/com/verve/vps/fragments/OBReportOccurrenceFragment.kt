package com.verve.vps.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.verve.vps.R
import com.verve.vps.activities.MapsActivity
import com.verve.vps.helpers.Constants.OCCURRENCE_HEADING
import com.verve.vps.helpers.Constants.OCCURRENCE_NARRATIVE
import com.verve.vps.helpers.PreferencesHelper.sharedPreferences
import com.verve.vps.utils.Utils

import kotlinx.android.synthetic.main.occurrence_details_layout.*
import kotlinx.android.synthetic.main.report_occurence_fragment_layout.*

class OBReportOccurrenceFragment : Fragment() {

    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.report_occurence_fragment_layout, container, false)
    }

    override fun onResume() {
        super.onResume()
        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext

        initButtons()
        initOccurrenceForm()
    }

    private fun initButtons() {
        btnNext.setOnClickListener { validateOccurrenceDetails() }
        btnSearchLocation.setOnClickListener {
            btnSearchLocation.setBackgroundColor(resources.getColor(R.color.btnGreen))
            saveOccurrenceDetails()
            startActivity(Intent(parentActivity, MapsActivity::class.java))
        }
    }

    private fun initFormErrors() {
        od_heading.error = null
        od_narrative.error = null
    }

    private fun initOccurrenceForm() {
        sharedPreferences.apply {
            od_heading.setText(getString(OCCURRENCE_HEADING,""))
            od_narrative.setText(getString(OCCURRENCE_NARRATIVE,""))

            val occurrenceArea = getString("OCCURRENCE_PLACE_NAME","")

            if(!occurrenceArea.equals(other = "", ignoreCase = true)) {
                occurrence_location_header.text = getString(R.string.occurrence_area_txt, occurrenceArea)
                btnSearchLocation.text = getString(R.string.change_oc_location)

            }
            else {
                occurrence_location_header.text = getString(R.string.oc_location_header)
                btnSearchLocation.text = getString(R.string.search_oc_location)
            }
        }
    }


    private fun validateOccurrenceDetails() {

        initFormErrors()

        val occurrenceHeading = od_heading.text.toString()
        val occurrenceNarrative = od_narrative.text.toString()
        val occurrenceArea = sharedPreferences.getString("OCCURRENCE_PLACE_NAME","")!!


        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(occurrenceHeading)) {
            od_heading.error = "Please enter a valid occurrence heading!"
            focusView = od_heading
            cancel = true
        }

        if (TextUtils.isEmpty(occurrenceNarrative)) {
            od_narrative.error = "Please enter a valid occurrence narrative!"
            focusView = od_narrative
            cancel = true
        }

        if(cancel) {
            focusView?.requestFocus()
        }
        else if(occurrenceArea.trim().equals("",true)) {
            Utils.showSnackbar(parentActivity,"Please select a valid occurrence location!", R.drawable.ic_error_red)
            btnSearchLocation.setBackgroundColor(Color.RED)
        }
        else {
            saveOccurrenceDetails()


            val args = Bundle()
            args.putString("title", "OB Report View")
            OBReportViewFragment().arguments = args
            Utils.goToFragment(OBReportViewFragment(), parentActivity)
        }

    }

    private fun saveOccurrenceDetails() {
        Utils.makeReporterStartingForm(true)
        sharedPreferences.edit().apply {
            putString(OCCURRENCE_HEADING,od_heading.text.toString())
            putString(OCCURRENCE_NARRATIVE, od_narrative.text.toString())
            apply()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
               backPressed()
                return true
            }
        }
        return false
    }

    private fun backPressed() {
        // go to the first tab
        Utils.goToTab(parentActivity.findViewById(R.id.tab_layout) as TabLayout, 0)
    }

    companion object {
        fun newInstance(): OBReportOccurrenceFragment = OBReportOccurrenceFragment()
    }
}