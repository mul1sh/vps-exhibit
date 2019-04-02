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
import com.verve.vps.helpers.Constants.OFFICER_NAME
import com.verve.vps.helpers.Constants.OFFICER_RANK
import com.verve.vps.helpers.Constants.OFFICER_SERVICE_NO
import com.verve.vps.helpers.OBReportHelper.viewOBOccurrenceId
import com.verve.vps.helpers.PreferencesHelper.sharedPreferences
import com.verve.vps.models.OccurrenceReportDetailsResult
import com.verve.vps.utils.Utils

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.officer_details_layout.*
import kotlinx.android.synthetic.main.reporter_summary_layout.*
import kotlinx.android.synthetic.main.summary_reporter_fragment_layout.*

import timber.log.Timber

class OBReportSummaryReporterFragment: androidx.fragment.app.Fragment() {

    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context
    private val apiService  = ApiServiceHelper.apiService!!
    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.summary_reporter_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext
        initForm()

        // load the occurrence details
        loadOccurrenceDetails()
    }

    private fun loadOccurrenceDetails() {
        fetching_reporter_details.visibility = View.VISIBLE
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
            fetching_reporter_details.visibility = View.GONE
            val ocDetails = oc.occurrenceReport
            val person = oc.occurrenceReporterDetails
            rd_id_number.setText(person.idNumber)
            rd_full_name.setText(person.name)
            rd_phone_number.setText(person.phoneNumber)
            rd_gender.setText(person.gender)
            rd_county.setText(ocDetails.county)
            rd_constituency.setText(ocDetails.constituency)


            sharedPreferences.apply {
                // set the text fields
                ofd_service_number.setText(getString(OFFICER_SERVICE_NO,""))
                ofd_rank.setText(getString(OFFICER_RANK ,""))
                ofd_full_name.setText(getString(OFFICER_NAME ,""))
            }

        }
    }

    private fun handleApiError(e: Throwable) {
        Timber.e(e)
        fetching_reporter_details.visibility = View.GONE
        if(e.localizedMessage == "HTTP 404 Not Found") {
            Utils.showSnackbar(parentActivity,"Sorry, OB details not found!!", R.drawable.ic_warning_yellow)
            parentActivity.supportFragmentManager.popBackStack()

        } else {
            Utils.showSnackbar(parentActivity,"Sorry, unable to load OB details, please check your internet connection!!", R.drawable.ic_warning_yellow)
            parentActivity.supportFragmentManager.popBackStack()
        }


    }
    private fun initForm() {
        // disable them
        // first for officer details
        ofd_service_number.isEnabled = false
        ofd_rank.isEnabled = false
        ofd_full_name.isEnabled = false

        // then the reporter details
        rd_id_number.isEnabled = false
        rd_full_name.isEnabled = false
        rd_phone_number.isEnabled = false
        rd_gender.isEnabled = false
        rd_county.isEnabled = false
        rd_constituency.isEnabled = false
    }



    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }


    companion object {
        fun newInstance(): OBReportSummaryReporterFragment = OBReportSummaryReporterFragment()
    }
}