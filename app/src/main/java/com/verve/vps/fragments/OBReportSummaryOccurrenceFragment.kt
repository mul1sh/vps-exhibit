package com.verve.vps.fragments

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.verve.vps.R
import com.verve.vps.helpers.AccessTokenHelper.getAccessToken
import com.verve.vps.helpers.ApiServiceHelper
import com.verve.vps.helpers.Constants.OCCURRENCE_NO
import com.verve.vps.helpers.OBReportHelper.viewOBOccurrenceId
import com.verve.vps.helpers.PreferencesHelper.sharedPreferences

import com.verve.vps.models.OccurrenceReportDetailsResult
import com.verve.vps.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.occurrence_details_layout.*
import kotlinx.android.synthetic.main.summary_occurence_fragment_layout.*
import timber.log.Timber


class OBReportSummaryOccurrenceFragment : androidx.fragment.app.Fragment() {

    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context
    private val apiService  = ApiServiceHelper.apiService!!
    private val mCompositeDisposable = CompositeDisposable()


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
        return inflater.inflate(R.layout.summary_occurence_fragment_layout, container, false)
    }

    override fun onResume() {
        super.onResume()

        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext

        // initialize the form actions
        initViews()
        loadOccurrenceDetails()
    }

    private fun initViews(){
        od_ob_number.visibility = View.VISIBLE
    }


    private fun loadOccurrenceDetails() {
        fetching_occurrence_details.visibility = View.VISIBLE
        // load the occurrence details first
        apiService
            .getOccurrenceDetails(getAccessToken(),viewOBOccurrenceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleOccurrenceDetailsResult, ::handleApiError )
            .addTo(mCompositeDisposable)

    }

    private fun handleOccurrenceDetailsResult(oc: OccurrenceReportDetailsResult) {
        if(oc.status) {

            val ocDetails = oc.occurrenceReport

            ob_number.setText(ocDetails.occurrenceNo)
            od_heading.setText(ocDetails.description)
            od_narrative.setText(ocDetails.narrative)

            // then disable these fields
            ob_number.isEnabled = false
            od_heading.isEnabled = false
            od_narrative.isEnabled = false

            // also set the ob number
            sharedPreferences.edit().apply {
               putString(OCCURRENCE_NO, ocDetails.occurrenceNo)
               apply()
            }

            if(fetching_occurrence_details !== null){
                fetching_occurrence_details.visibility = View.GONE
            }
        }
    }

    private fun handleApiError(e: Throwable) {
        Timber.e(e)
        if(fetching_occurrence_details !== null){
            fetching_occurrence_details.visibility = View.GONE
        }
        if(e.localizedMessage == "HTTP 404 Not Found") {
            Utils.showSnackbar(parentActivity,"Sorry, OB details not found!!", R.drawable.ic_warning_yellow)
            parentActivity.supportFragmentManager.popBackStack()

        } else {
            Utils.showSnackbar(parentActivity,"Sorry, unable to load OB details, please check your internet connection!!", R.drawable.ic_warning_yellow)
            parentActivity.supportFragmentManager.popBackStack()
        }


    }

    companion object {
        fun newInstance(): OBReportSummaryOccurrenceFragment = OBReportSummaryOccurrenceFragment()
    }
}