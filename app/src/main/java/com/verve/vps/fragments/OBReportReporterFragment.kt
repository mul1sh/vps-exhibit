package com.verve.vps.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter

import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity

import com.verve.vps.R
import com.verve.vps.helpers.ApiServiceHelper
import com.verve.vps.helpers.AccessTokenHelper.getAccessToken
import com.verve.vps.helpers.Constants.GO_BACK_TO_REPORTER_FORM
import com.verve.vps.helpers.Constants.REPORTER_CONSTITUENCY
import com.verve.vps.helpers.Constants.REPORTER_CONSTITUENCY_SELECTED_POSITION
import com.verve.vps.helpers.Constants.REPORTER_COUNTY
import com.verve.vps.helpers.Constants.REPORTER_COUNTY_ID
import com.verve.vps.helpers.Constants.REPORTER_COUNTY_SELECTED_POSITION
import com.verve.vps.helpers.Constants.REPORTER_FORM_FILLED
import com.verve.vps.helpers.Constants.REPORTER_GENDER
import com.verve.vps.helpers.Constants.REPORTER_GENDER_SELECTED_POSITION
import com.verve.vps.helpers.Constants.REPORTER_ID_PASSPORT_NUMBER
import com.verve.vps.helpers.Constants.REPORTER_NAME
import com.verve.vps.helpers.Constants.REPORTER_NATIONALITY
import com.verve.vps.helpers.Constants.REPORTER_PHONE_NUMBER
import com.verve.vps.helpers.Constants.REPORTER_SUB_COUNTY_ID
import com.verve.vps.helpers.PreferencesHelper.sharedPreferences
import com.verve.vps.models.Person
import com.verve.vps.models.SearchPersonResult
import com.verve.vps.utils.Utils
import com.google.android.material.tabs.TabLayout

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.personal_details_layout.*
import kotlinx.android.synthetic.main.place_from_layout.*
import kotlinx.android.synthetic.main.report_reporter_fragment_layout.*

import timber.log.Timber


class OBReportReporterFragment : Fragment() {

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
        return inflater.inflate(R.layout.report_reporter_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init the public vars
        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext
    }

    override fun onResume() {
        super.onResume()

        initSpinners()
        initButtons()
        initReporterDetails()

    }

    override fun onPause() {
        super.onPause()
        mCompositeDisposable.clear()
    }


    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

    private fun initSpinners() {

        // populate the spinners
        spinner_nationalities.adapter = ArrayAdapter<String>(
            parentActivity,
            android.R.layout.simple_spinner_dropdown_item,
            Utils.getArrayListFromJsonArray(Utils.getJsonArrayFromFile(mContext, "nationalities.json"), "")
        )

        spinner_gender.adapter = ArrayAdapter<String>(
            parentActivity,
            android.R.layout.simple_spinner_dropdown_item,
            Utils.getArrayListFromJsonArray(Utils.getJsonArrayFromFile(mContext, "genders.json"), "type")
        )

        spinner_counties.adapter = ArrayAdapter<String>(
            parentActivity,
            android.R.layout.simple_spinner_dropdown_item,
            Utils.getArrayListFromJsonArray(Utils.getJsonArrayFromFile(mContext, "counties.json"), "name")
        )



        // add the listeners
        spinner_nationalities.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                sharedPreferences.edit().apply {
                    putString(REPORTER_NATIONALITY, spinner_nationalities.selectedItem.toString())
                    apply()
                }

            }

        }

        spinner_gender.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                // save the selected position
               sharedPreferences.edit().apply {
                    putInt(REPORTER_GENDER_SELECTED_POSITION, position)
                    apply()
               }
            }

        }

        spinner_counties.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


                val adapter = ArrayAdapter<String>(parentActivity, android.R.layout.simple_spinner_dropdown_item,Utils.loadCountyConstituencies(mContext,position))
                adapter.notifyDataSetChanged()
                spinner_constituencies.adapter = adapter


                // save the selected position
                sharedPreferences.edit().apply {
                    putInt(REPORTER_COUNTY_SELECTED_POSITION, position)
                    putString(REPORTER_COUNTY, spinner_counties.selectedItem.toString())
                    apply()
                }

                sharedPreferences.apply {
                    val selectedConstPosition = getInt(REPORTER_CONSTITUENCY_SELECTED_POSITION ,0)

                    if(adapter.count > selectedConstPosition) {
                        spinner_constituencies.setSelection(selectedConstPosition, true)
                    } else {
                        spinner_constituencies.setSelection(0, true)
                    }

                }


            }

        }

        spinner_constituencies.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // save the selected constituency position
                sharedPreferences.edit().apply {
                    putInt(REPORTER_CONSTITUENCY_SELECTED_POSITION, spinner_constituencies.selectedItemPosition)
                    putString(REPORTER_CONSTITUENCY, spinner_constituencies.selectedItem.toString())
                    apply()
                }
            }

        }

        // set the default selected items
        spinner_nationalities.setSelection(0, true)
        spinner_gender.setSelection(0, true)
        spinner_counties.setSelection(46, true)
        spinner_constituencies.setSelection(0, true)


    }

    private fun initButtons() {
        // on next button click
        btnNext.setOnClickListener {
            handleNextBtnClick()
        }

        //handle id search
        btnSearchIdNumber.setOnClickListener {
            // hide the keyboard
            Utils.hideKeyboard( btnSearchIdNumber,mContext)


            val userIdNumber = txt_user_id.text.toString()
            if(userIdNumber.isNotEmpty() && userIdNumber.length > 6) {
                // show loading ui
                btnSearchIdNumber.startAnimation()


                apiService
                    .searchPerson(getAccessToken(),userIdNumber)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe (this::handleApiResult, this::handleApiError )
                    .addTo(mCompositeDisposable)
            } else {
                Utils.showSnackbar(parentActivity,getString(R.string.invalid_id_number), R.drawable.ic_error_red)
            }

        }
    }

    private fun initFormErrors() {
        txt_user_id.error = null
        pd_name.error = null
        pd_phonenumber.error = null
    }


    private fun showLoadingButtonSuccess() {
        btnSearchIdNumber.doneLoadingAnimation(R.color.btnGreen, Utils.getBitmapFromVectorDrawable(mContext,R.drawable.ic_check_circle))
        Handler().postDelayed({  btnSearchIdNumber.revertAnimation() }, 2000)
    }

    private fun showLoadingButtonError() {
        btnSearchIdNumber.revertAnimation()
    }


    private fun handleApiResult(searchPersonResult: SearchPersonResult) {

        if(searchPersonResult.message == "User found") {
            populatePerson(searchPersonResult.person, true)
        }

        if(searchPersonResult.message == "User not found") {
            Utils.showSnackbar(parentActivity,"User not found!!", R.drawable.ic_warning_yellow)
            populatePerson(null, false)
            // show button loading error
            showLoadingButtonError()
        }

    }

    private fun handleApiError(e: Throwable) {
        Timber.e(e)
        Utils.showSnackbar(parentActivity,"Unable to load person details, please try searching again!!", R.drawable.ic_error_red)
        populatePerson(null, false)
    }

    private fun populatePerson(person: Person?, populate: Boolean) {

        if(populate && person != null) {

            initFormErrors()
            txt_user_id.setText(person.idNumber)
            pd_name.setText(person.name)
            pd_phonenumber.setText(person.phoneNumber)

            if(person.gender.equals("male",true)) {
                spinner_gender.setSelection(0, true)
            } else {
                spinner_gender.setSelection(1, true)
            }

            showLoadingButtonSuccess()
        }
        else {
            pd_name.setText("")
            pd_phonenumber.setText("")

        }

    }

    private fun handleNextBtnClick() {
        initFormErrors()

        // Store values at the time of the login attempt.
        val userId = txt_user_id.text.toString()
        val name = pd_name.text.toString()
        val phoneNumber = pd_phonenumber.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(userId)) {
            txt_user_id.error = "Please enter a valid ID or passport number!"
            focusView = txt_user_id
            cancel = true
        }

        if (TextUtils.isEmpty(name)) {
            pd_name.error = "Please enter a valid name!"
            focusView = pd_name
            cancel = true
        }


        if(cancel) {
            focusView?.requestFocus()
        }
        else {
            // and then the rest of the info
            sharedPreferences.apply {

                sharedPreferences.edit().apply {
                    putString(REPORTER_ID_PASSPORT_NUMBER, userId)
                    putString(REPORTER_NAME, name)
                    putString(REPORTER_PHONE_NUMBER, phoneNumber)
                    putString(REPORTER_GENDER, Utils.getGenderTypeFromSelectedPosition(mContext,getInt(REPORTER_GENDER_SELECTED_POSITION,0)))
                    putString(REPORTER_COUNTY_ID, Utils.getCountyIdFromSelectedPosition(mContext,getInt(REPORTER_COUNTY_SELECTED_POSITION,0)))
                    putString(REPORTER_SUB_COUNTY_ID, Utils.getConstituencyIdFromSelectedPosition(mContext,getInt(REPORTER_CONSTITUENCY_SELECTED_POSITION ,0)))
                    putBoolean(REPORTER_FORM_FILLED,true)

                    apply()

                    // go to the next tab
                    Utils.goToTab(parentActivity.findViewById(R.id.tab_layout) as TabLayout, 1)
                }

            }

        }

    }

    private fun initReporterDetails() {
        sharedPreferences.apply {
            val rUserId = getString(REPORTER_ID_PASSPORT_NUMBER,"")!!
            val rName = getString(REPORTER_NAME,"")!!
            val rPhoneNumber = getString(REPORTER_PHONE_NUMBER,"")!!


            val rFormFilled = getBoolean(REPORTER_FORM_FILLED,false)

            if(rFormFilled) {
                txt_user_id.setText(rUserId)
                pd_name.setText(rName)
                pd_phonenumber.setText(rPhoneNumber)
                val selectedGenderPosition = getInt(REPORTER_GENDER_SELECTED_POSITION,0)
                val selectedCountyPosition = getInt(REPORTER_COUNTY_SELECTED_POSITION,0)

                // set up the spinners
                spinner_gender.setSelection(selectedGenderPosition,true)
                spinner_counties.setSelection(selectedCountyPosition,true)

                if(getBoolean(GO_BACK_TO_REPORTER_FORM,false)) {
                    // go to the first tab
                    Utils.goToTab(parentActivity.findViewById(R.id.tab_layout) as TabLayout, 0)

                } else {
                    // go to the second tab
                    Utils.goToTab(parentActivity.findViewById(R.id.tab_layout) as TabLayout, 1)
                }

            }

        }
    }

    companion object {
        fun newInstance(): OBReportReporterFragment = OBReportReporterFragment()
    }
}