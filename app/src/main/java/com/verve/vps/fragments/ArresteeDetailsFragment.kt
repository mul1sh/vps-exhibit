package com.verve.vps.fragments

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

import com.google.android.material.tabs.TabLayout

import com.verve.vps.R
import com.verve.vps.helpers.ApiServiceHelper
import com.verve.vps.helpers.AccessTokenHelper.getAccessToken
import com.verve.vps.helpers.Constants.ARRESTEE_CONSTITUENCY
import com.verve.vps.helpers.Constants.ARRESTEE_CONSTITUENCY_SELECTED_POSITION
import com.verve.vps.helpers.Constants.ARRESTEE_COUNTY
import com.verve.vps.helpers.Constants.ARRESTEE_COUNTY_ID
import com.verve.vps.helpers.Constants.ARRESTEE_COUNTY_SELECTED_POSITION
import com.verve.vps.helpers.Constants.ARRESTEE_FORM_FILLED
import com.verve.vps.helpers.Constants.ARRESTEE_GENDER
import com.verve.vps.helpers.Constants.ARRESTEE_GENDER_SELECTED_POSITION
import com.verve.vps.helpers.Constants.ARRESTEE_ID_PASSPORT_NUMBER
import com.verve.vps.helpers.Constants.ARRESTEE_KIN_NAME
import com.verve.vps.helpers.Constants.ARRESTEE_KIN_PHONE_NUMBER
import com.verve.vps.helpers.Constants.ARRESTEE_KIN_RELATIONSHIP
import com.verve.vps.helpers.Constants.ARRESTEE_KIN_RELATIONSHIP_ID
import com.verve.vps.helpers.Constants.ARRESTEE_NAME
import com.verve.vps.helpers.Constants.ARRESTEE_NATIONALITY
import com.verve.vps.helpers.Constants.ARRESTEE_PHONE_NUMBER
import com.verve.vps.helpers.Constants.ARRESTEE_RELATIONSHIP_SELECTED_POSITION
import com.verve.vps.helpers.Constants.ARRESTEE_SUB_COUNTY_ID
import com.verve.vps.helpers.Constants.GO_BACK_TO_ARRESTEE_FORM
import com.verve.vps.helpers.PreferencesHelper.sharedPreferences
import com.verve.vps.models.Person
import com.verve.vps.models.SearchPersonResult
import com.verve.vps.utils.Utils

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.arrestee_details_layout.*
import kotlinx.android.synthetic.main.arrestee_layout.*
import kotlinx.android.synthetic.main.next_of_kin_layout.*

import timber.log.Timber

class ArresteeDetailsFragment : Fragment() {

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
        return inflater.inflate(R.layout.arrestee_layout, container, false)
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
        initArresteeDetails()
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
        spinner_nationality.adapter = ArrayAdapter<String>(
            parentActivity,
            android.R.layout.simple_spinner_dropdown_item,
            Utils.getArrayListFromJsonArray(Utils.getJsonArrayFromFile(mContext, "nationalities.json"), "")
        )

        spinner_gender.adapter = ArrayAdapter<String>(
            parentActivity,
            android.R.layout.simple_spinner_dropdown_item,
            Utils.getArrayListFromJsonArray(Utils.getJsonArrayFromFile(mContext, "genders.json"), "type")
        )

        spinner_county.adapter = ArrayAdapter<String>(
            parentActivity,
            android.R.layout.simple_spinner_dropdown_item,
            Utils.getArrayListFromJsonArray(Utils.getJsonArrayFromFile(mContext, "counties.json"), "name")
        )

        spinner_constituency.adapter = ArrayAdapter<String>(
            parentActivity,
            android.R.layout.simple_spinner_dropdown_item,
            Utils.getArrayListFromJsonArray(Utils.getJsonArrayFromFile(mContext, "constituencies.json"), "name")
        )

        spinner_next_of_kin_relationship.adapter = ArrayAdapter<String>(
            parentActivity,
            android.R.layout.simple_spinner_dropdown_item,
            Utils.getArrayListFromJsonArray(Utils.getJsonArrayFromFile(mContext, "relationships.json"), "title")
        )

        // add the listeners
        spinner_nationality.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sharedPreferences.edit().apply {
                    putString(ARRESTEE_NATIONALITY , spinner_nationality.selectedItem.toString())
                    apply()
                }

            }
        }

        spinner_gender.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // save the selected position
                sharedPreferences.edit().apply {
                    putInt(ARRESTEE_GENDER_SELECTED_POSITION , position)
                    apply()
                }

            }

        }

        spinner_county.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


                val adapter = ArrayAdapter<String>(parentActivity, android.R.layout.simple_spinner_dropdown_item,Utils.loadCountyConstituencies(mContext,position))
                adapter.notifyDataSetChanged()
                spinner_constituency.adapter = adapter


                // save the selected position
                sharedPreferences.edit().apply {
                    putInt(ARRESTEE_COUNTY_SELECTED_POSITION , position)
                    putString(ARRESTEE_COUNTY, spinner_county.selectedItem.toString())
                    apply()
                }

                sharedPreferences.apply {
                    val selectedConstPosition = getInt(ARRESTEE_CONSTITUENCY_SELECTED_POSITION,0)

                    if(adapter.count > selectedConstPosition) {
                        spinner_constituency.setSelection(selectedConstPosition, true)
                    } else {
                        spinner_constituency.setSelection(0, true)
                    }

                }

            }

        }

        spinner_constituency.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                // save the selected constituency position
                sharedPreferences.edit().apply {
                    putInt(ARRESTEE_CONSTITUENCY_SELECTED_POSITION, spinner_constituency.selectedItemPosition)
                    putString(ARRESTEE_CONSTITUENCY, spinner_constituency.selectedItem.toString())
                    apply()
                }

            }

        }

        spinner_next_of_kin_relationship.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                sharedPreferences.edit().apply {
                    putString(ARRESTEE_KIN_RELATIONSHIP, spinner_next_of_kin_relationship.selectedItem.toString())
                    putInt(ARRESTEE_RELATIONSHIP_SELECTED_POSITION , position)
                    apply()
                }

            }

        }

        // set the default selected items
        spinner_nationality.setSelection(93, true)
        spinner_gender.setSelection(0, true)
        spinner_county.setSelection(46, true)
        spinner_constituency.setSelection(0, true)
        spinner_next_of_kin_relationship.setSelection(0, true)


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


            val userIdNumber = txt_arrestee_id.text.toString()
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
            showLoadingButtonSuccess()
        }

        if(searchPersonResult.message == "User not found") {
            Utils.showSnackbar(parentActivity,"User not found, please enter their details below!!", R.drawable.ic_warning_yellow)
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

        if(person !== null && populate){
            arrestee_name.setText(person.name)
            arrestee_phonenumber.setText(person.phoneNumber)

            if(person.gender.equals("male",true)) {
                spinner_gender.setSelection(0, true)
            } else {
                spinner_gender.setSelection(1, true)
            }
            //set the next of kin details
            next_of_kin_name.setText(person.kinName)
            next_of_kin_phonenumber.setText(person.kinPhoneNumber)

            if(!person.kinRelationId.isNullOrEmpty()) {
                spinner_next_of_kin_relationship.setSelection(person.kinRelationId!!.toInt() - 1)
            }

        } else {

            // reset the fields in case of a search failure
            arrestee_name.setText("")
            arrestee_phonenumber.setText("")
            next_of_kin_name.setText("")
            next_of_kin_phonenumber.setText("")

            spinner_gender.setSelection(0, true)
            spinner_next_of_kin_relationship.setSelection(0, true)

        }

    }

    private fun initFormErrors() {
        arrestee_name.error = null
        arrestee_phonenumber.error = null
        next_of_kin_name.error = null
        next_of_kin_phonenumber.error = null
    }

    private fun handleNextBtnClick() {
        initFormErrors()

        val userIdNumber = txt_arrestee_id.text.toString()
        val name = arrestee_name.text.toString()
        val phoneNumber = arrestee_phonenumber.text.toString()
        val kinName = next_of_kin_name.text.toString()
        val kinPhoneNumber = next_of_kin_phonenumber.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(userIdNumber)) {
            txt_arrestee_id.error = "Please enter a valid ID or passport number!"
            focusView = txt_arrestee_id
            cancel = true

        }

        if (TextUtils.isEmpty(name)) {
            arrestee_name.error = "Please enter a valid name!"
            focusView = arrestee_name
            cancel = true
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            arrestee_phonenumber.error = "Please enter a valid phone number!"
            focusView = arrestee_phonenumber
            cancel = true
        }

        if (TextUtils.isEmpty(kinName)) {
            next_of_kin_name.error = "Please enter a valid next of kin name!"
            focusView = next_of_kin_name
            cancel = true
        }

        if (TextUtils.isEmpty(kinPhoneNumber)) {
            next_of_kin_phonenumber.error = "Please enter a valid next of kin phone number!"
            focusView = next_of_kin_phonenumber
            cancel = true
        }

        if(cancel) {
            focusView?.requestFocus()
        }
        else {
            // and then the rest of the info
            sharedPreferences.apply {

                sharedPreferences.edit().apply {
                    putString(ARRESTEE_ID_PASSPORT_NUMBER , userIdNumber)
                    putString(ARRESTEE_NAME, name)
                    putString(ARRESTEE_PHONE_NUMBER , phoneNumber)
                    putString(ARRESTEE_GENDER, Utils.getGenderTypeFromSelectedPosition(mContext,getInt(ARRESTEE_GENDER_SELECTED_POSITION,0)))
                    putString(ARRESTEE_COUNTY_ID, Utils.getCountyIdFromSelectedPosition(mContext,getInt(ARRESTEE_COUNTY_SELECTED_POSITION,0)))
                    putString(ARRESTEE_SUB_COUNTY_ID, Utils.getCountyIdFromSelectedPosition(mContext,getInt(ARRESTEE_CONSTITUENCY_SELECTED_POSITION,0)))
                    putString(ARRESTEE_KIN_NAME, kinName)
                    putString(ARRESTEE_KIN_PHONE_NUMBER, kinPhoneNumber)
                    putString(ARRESTEE_KIN_RELATIONSHIP_ID,Utils.getRelationshipIdFromSelectedPosition(mContext, getInt(ARRESTEE_RELATIONSHIP_SELECTED_POSITION, 0)))
                    putBoolean(ARRESTEE_FORM_FILLED,true)

                    apply()

                    // go to the next tab
                    Utils.goToTab(parentActivity.findViewById(R.id.tab_layout) as TabLayout, 1)
                }

            }

        }

    }

    private fun initArresteeDetails() {
        sharedPreferences.apply {
            val userId = getString(ARRESTEE_ID_PASSPORT_NUMBER ,"")!!
            val name = getString(ARRESTEE_NAME,"")!!
            val phoneNumber = getString(ARRESTEE_PHONE_NUMBER,"")!!
            val kinName = getString(ARRESTEE_KIN_NAME,"")!!
            val kinPhoneNumber = getString(ARRESTEE_KIN_PHONE_NUMBER,"")!!
            val formFilled = getBoolean(ARRESTEE_FORM_FILLED,false)

            if(formFilled) {
                txt_arrestee_id.setText(userId)
                arrestee_name.setText(name)
                arrestee_phonenumber.setText(phoneNumber)
                next_of_kin_name.setText(kinName)
                next_of_kin_phonenumber.setText(kinPhoneNumber)

                // set up the spinners
                val selectedGenderPosition = getInt(ARRESTEE_GENDER_SELECTED_POSITION  ,0)
                val selectedCountyPosition = getInt(ARRESTEE_COUNTY_SELECTED_POSITION ,0)
                val selectedConstituencyPosition = getInt(ARRESTEE_CONSTITUENCY_SELECTED_POSITION ,0)
                val selectedRelationshipPosition = getInt(ARRESTEE_RELATIONSHIP_SELECTED_POSITION,0)

                spinner_gender.setSelection(selectedGenderPosition)
                spinner_county.setSelection(selectedCountyPosition)
                spinner_constituency.setSelection(selectedConstituencyPosition)
                spinner_next_of_kin_relationship.setSelection(selectedRelationshipPosition)


                if(getBoolean(GO_BACK_TO_ARRESTEE_FORM,false)) {
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
        fun newInstance(): ArresteeDetailsFragment = ArresteeDetailsFragment()
    }
}