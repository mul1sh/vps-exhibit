package com.verve.vps.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonElement
import com.google.gson.JsonObject

import com.verve.vps.R
import com.verve.vps.adapters.OfficersListingAdapter
import com.verve.vps.helpers.*
import com.verve.vps.helpers.AccessTokenHelper.getAccessToken
import com.verve.vps.helpers.Constants.OCCURRENCE_NO
import com.verve.vps.helpers.OBReportHelper.assignedOBOfficers
import com.verve.vps.helpers.OBReportHelper.viewOBOccurrenceId
import com.verve.vps.helpers.PreferencesHelper.sharedPreferences
import com.verve.vps.models.AssignOfficers
import com.verve.vps.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.assign_officers_layout.*
import kotlinx.android.synthetic.main.view_ob_actions_item_layout.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber

class OfficerListingFragment: Fragment() {
    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context
    private lateinit var mRecyclerView: RecyclerView
    private val apiService  = ApiServiceHelper.apiService!!
    private val mCompositeDisposable = CompositeDisposable()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.assign_officers_layout, container, false)
    }

    override fun onResume() {
        super.onResume()

        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext

        parentActivity.supportActionBar!!.title = "Assign Officer"

        initButtons()
        initRecyclerView()
        initSearchView()
    }

    private fun initRecyclerView() {
        val args = arguments
        mRecyclerView = parentActivity.findViewById(R.id.assign_officer_list) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView.adapter = OfficersListingAdapter(JSONArray(args!!.getString("officersArray")) )
        mRecyclerView.addItemDecoration(DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL))
    }

    private fun initButtons() {

        var obNumber = ""
        sharedPreferences.apply {
            obNumber = getString(OCCURRENCE_NO,"")!!
        }
        if(obNumber.isNotEmpty()) {
            btnAssignOccurrenceToOfficer.text = "ASSIGN TO OCCURRENCE - $obNumber"
        }

        btnAssignOccurrenceToOfficer.setOnClickListener {
            assignOfficersToOccurrence()
        }
    }


    private fun assignOfficersToOccurrence() {
        btnAssignOccurrenceToOfficer.startAnimation()

        val assignedOfficers = AssignOfficers().apply {
            officers = assignedOBOfficers
        }

        apiService
            .assignOfficersToOccurrence(getAccessToken(), assignedOfficers, viewOBOccurrenceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleApiResult,::handleApiError )
            .addTo(mCompositeDisposable)
    }

    private fun showLoadingButtonSuccess() {
        btnAssignOccurrenceToOfficer.doneLoadingAnimation(R.color.btnGreen, Utils.getBitmapFromVectorDrawable(mContext,R.drawable.ic_check_circle))
        btnAssignOccurrenceToOfficer.revertAnimation()
    }

    private fun showLoadingButtonError() {
        btnAssignOccurrenceToOfficer.revertAnimation()
    }


    private fun handleApiResult(response: Response<ResponseBody>) {
        if (response.isSuccessful) {
            val status = JSONObject(response.body()?.string().toString()).getBoolean("status")
            if(status){
                Utils.showSnackbar(parentActivity,"Officer(s) assigned to occurrence successfully", R.drawable.ic_check_circle)
                showLoadingButtonSuccess()
            } else {
                Utils.showSnackbar(parentActivity,"Officer(s) already assigned to occurrence!!!", R.drawable.ic_error_red)
                showLoadingButtonError()
            }

        }
        else {
            Utils.showSnackbar(parentActivity,"Officer(s) not assigned to occurrence!!!", R.drawable.ic_error_red)
        }
        // clear the selection
        assignedOBOfficers.clear()
        // then go back
        parentActivity.supportFragmentManager.popBackStack()
    }

    private fun handleApiError(e: Throwable) {
        Timber.e(e)
        // clear the selection
        assignedOBOfficers.clear()
        // then go back
        parentActivity.supportFragmentManager.popBackStack()
        showLoadingButtonError()
        Utils.showSnackbar(parentActivity, "Assigning officers to occurrence failed, please check your internet connection!!", R.drawable.ic_error_red)
    }

    private fun initSearchView() {

        search_officer_by_number.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchReport(s.toString())
            }

        })
    }

    private fun searchReport(searchString: String){
        val args = arguments
        val officersArray = JSONArray(args!!.getString("officersArray"))
        val filteredOfficersList = JSONArray()

        officersArray.iterator().forEach {
            val serviceNo = it.getString("serviceno")
            if(serviceNo.contains(searchString,true)){
                filteredOfficersList.put(it)
            }
        }

        val adapter = OfficersListingAdapter(filteredOfficersList)
        mRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

    }

    operator fun JSONArray.iterator() = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()

    override fun onPause() {
        super.onPause()
        mCompositeDisposable.clear()
    }


    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

}