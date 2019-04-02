package com.verve.vps.fragments

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.verve.vps.R
import com.verve.vps.helpers.AccessTokenHelper.getAccessToken
import com.verve.vps.helpers.ApiServiceHelper
import com.verve.vps.helpers.Constants.OCCURRENCE_HEADING
import com.verve.vps.helpers.Constants.OCCURRENCE_LATITUDE
import com.verve.vps.helpers.Constants.OCCURRENCE_LONGITUDE
import com.verve.vps.helpers.Constants.OCCURRENCE_NARRATIVE
import com.verve.vps.helpers.Constants.OCCURRENCE_PLACE_NAME
import com.verve.vps.helpers.Constants.REPORTER_CONSTITUENCY
import com.verve.vps.helpers.Constants.REPORTER_COUNTY
import com.verve.vps.helpers.Constants.REPORTER_COUNTY_ID
import com.verve.vps.helpers.Constants.REPORTER_GENDER
import com.verve.vps.helpers.Constants.REPORTER_ID_PASSPORT_NUMBER
import com.verve.vps.helpers.Constants.REPORTER_NAME
import com.verve.vps.helpers.Constants.REPORTER_NATIONALITY
import com.verve.vps.helpers.Constants.REPORTER_PHONE_NUMBER
import com.verve.vps.helpers.Constants.REPORTER_SUB_COUNTY_ID
import com.verve.vps.helpers.PreferencesHelper.sharedPreferences
import com.verve.vps.models.SaveOccurrenceResult
import com.verve.vps.utils.Utils

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.ob_report_view_layout.*
import kotlinx.android.synthetic.main.occurrence_details_layout.*
import kotlinx.android.synthetic.main.reporter_details_layout.*

class OBReportViewFragment  : Fragment() {

    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context
    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ob_report_view_layout, container, false)
    }

    override fun onResume() {
        super.onResume()

        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext

        val args = arguments
        parentActivity.supportActionBar!!.title = "View OB"

        initButtons()
        initOBReportForm()
    }

    private fun initButtons() {
        od_reported_crime_area.visibility= View.VISIBLE


        btnCancel.setOnClickListener {
            Utils.makeReporterStartingForm(true)
            parentActivity.supportFragmentManager.popBackStackImmediate()
        }

        btnSubmit.setOnClickListener {
            btnSubmit.startAnimation()
            saveOccurrence()
        }
    }

    private fun showLoadingButtonSuccess() {
        btnSubmit.doneLoadingAnimation(R.color.btnGreen, Utils.getBitmapFromVectorDrawable(mContext,R.drawable.ic_check_circle))
    }

    private fun showLoadingButtonError() {
        btnSubmit.revertAnimation()
    }

    private fun initOBReportForm() {
        sharedPreferences.apply {

            // set the occurrence details
            od_heading.setText(getString(OCCURRENCE_HEADING,""))
            od_narrative.setText(getString(OCCURRENCE_NARRATIVE,""))
            od_reported_crime_area_txt.setText(getString(OCCURRENCE_PLACE_NAME,""))

            // set the reporter details
            val rName = getString(REPORTER_NAME,"")!!.trim()

            rd_id_number.setText(getString(REPORTER_ID_PASSPORT_NUMBER,"")!!.trim())
            rd_full_name.setText(rName)
            rd_phone_number.setText(getString(REPORTER_PHONE_NUMBER,"")!!.trim())
            rd_gender.setText(getString(REPORTER_GENDER,"")!!.trim())
            rd_nationality.setText(getString(REPORTER_NATIONALITY, "")!!.trim())
            rd_county.setText(getString(REPORTER_COUNTY,"")!!.trim())
            rd_constituency.setText(getString(REPORTER_CONSTITUENCY, "")!!.trim())

            // finally disable the form controls
            od_heading.isEnabled = false
            od_narrative.isEnabled = false
            od_reported_crime_area_txt.isEnabled = false

            rd_id_number.isEnabled = false
            rd_full_name.isEnabled = false
            rd_phone_number.isEnabled = false
            rd_gender.isEnabled = false
            rd_nationality.isEnabled = false
            rd_county.isEnabled = false
            rd_constituency.isEnabled = false

        }

    }

    private fun saveOccurrence() {

        sharedPreferences.apply {
            ApiServiceHelper.apiService!!
            .saveOccurrence(
                getAccessToken(),
                getString(REPORTER_ID_PASSPORT_NUMBER,"")!!.trim(),
                getString(REPORTER_NAME,"")!!.trim(),
                getString(REPORTER_PHONE_NUMBER,"")!!.trim(),
                getString(REPORTER_COUNTY_ID,"")!!.trim(),
                getString(REPORTER_SUB_COUNTY_ID,"")!!.trim(),
                getString(REPORTER_GENDER,"")!!.trim(),
                getString(OCCURRENCE_HEADING,"")!!.trim(),
                getString(OCCURRENCE_NARRATIVE,"")!!.trim(),
                getString(OCCURRENCE_PLACE_NAME,"")!!.trim(),
                getString(OCCURRENCE_LATITUDE,"")!!.trim(),
                getString(OCCURRENCE_LONGITUDE,"")!!.trim()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleApiResult,::handleApiError)
            .addTo(mCompositeDisposable)
        }
    }

    private fun handleApiResult(occurrenceResult: SaveOccurrenceResult) {

        if(occurrenceResult.message == "Report added") {
            // if the report has been added then we can clear out the saved fields
            Utils.clearOBReportFields()
            // show success message
            Utils.showSnackbar(parentActivity, "Occurrence saved successfully.", R.drawable.ic_check_circle)
            // then go to the main page
            parentActivity.supportFragmentManager.popBackStack()
            parentActivity.supportFragmentManager.popBackStack()

            showLoadingButtonSuccess()
        }

    }

    private fun handleApiError(e: Throwable) {
        showLoadingButtonError()
        Utils.showSnackbar(parentActivity, "Occurrence save failed, please make sure our internet connection is working well!!", R.drawable.ic_error_red)
    }

    override fun onPause() {
        super.onPause()
        mCompositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

}