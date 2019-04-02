package com.verve.vps.fragments

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.verve.vps.R
import com.verve.vps.adapters.ViewOBAdapter
import com.verve.vps.helpers.AccessTokenHelper.getAccessToken
import com.verve.vps.helpers.ApiServiceHelper
import com.verve.vps.helpers.OBActionsHelper.relatedOccurrences
import com.verve.vps.helpers.OBExhibitHelper
import com.verve.vps.helpers.OBExhibitHelper.selectedExhibitIdFromList
import com.verve.vps.helpers.OBReportHelper.linkedExhibitId
import com.verve.vps.helpers.OBReportHelper.linkedOBNumber
import com.verve.vps.helpers.OBReportHelper.linkedOccurrenceId
import com.verve.vps.helpers.OBReportHelper.linkedOccurrences
import com.verve.vps.helpers.OBReportHelper.viewOBOccurrenceId
import com.verve.vps.helpers.RecyclerItemClickListener
import com.verve.vps.models.OccurrencesListResult
import com.verve.vps.models.LinkOccurrences
import com.verve.vps.models.Occurrence
import com.verve.vps.utils.Utils

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_ob_item_layout.view.*

import kotlinx.android.synthetic.main.view_ob_layout.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber

class ViewOBFragment : Fragment() {

    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context
    private lateinit var mRecyclerView: RecyclerView
    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var occurrencesList: MutableList<Occurrence>



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.view_ob_layout, container, false)
    }

    override fun onResume() {
        super.onResume()

        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext
        val args = arguments
        parentActivity.supportActionBar!!.title = args!!.getString("title")

        initRecyclerViews()
        initSearchView()
        initButtons()

        if( args.getString("action").equals("related",true) ){
            occurrencesList = relatedOccurrences
            val adapter = ViewOBAdapter(occurrencesList, parentActivity, args.getString("action")!!)
            mRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
            report_filters.visibility = View.GONE

        } else {
            fetchOccurrences()
        }


    }

    private fun initRecyclerViews() {
        mRecyclerView = parentActivity.findViewById(R.id.view_ob_recycler_view) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView.addItemDecoration(DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL))

    }

    private fun initButtons() {
        val args = arguments!!
        if(args.getString("action").equals("view",true)
            || args.getString("action").equals("related",true) ){
            btnLinkOccurrences.visibility = View.GONE
        }
        if(args.getString("action").equals("link",true)){
            btnLinkOccurrences.visibility = View.VISIBLE
            btnLinkOccurrences.text = "Link Occurrence's"
            btnLinkOccurrences.setOnClickListener {
                linkOccurrences()
            }
        }
        if(args.getString("action").equals("link_exhibit",true)){
            btnLinkOccurrences.visibility = View.VISIBLE
            btnLinkOccurrences.text = "Link Exhibit to Occurrence"
            btnLinkOccurrences.setOnClickListener {
                linkExhibitToOccurrence()
            }
        }

    }

    private fun fetchOccurrences() {
        showLoadingUi(true)
        ApiServiceHelper.apiService!!
            .getOccurrences(getAccessToken())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleApiResult,::handleApiError)
            .addTo(mCompositeDisposable)

    }

    private fun handleApiResult(occurrencesListResult: OccurrencesListResult) {
        if(occurrencesListResult.status) {
            occurrencesList = occurrencesListResult.occurrences
            initSpinners()
            showLoadingUi(false)
        }
        else {
            showLoadingUi(false)
            txtFetchingOccurrences.visibility = View.VISIBLE
            report_filters.visibility = View.GONE
            txtFetchingOccurrences.text = occurrencesListResult.message
        }
    }

    private fun linkOccurrences() {

        btnLinkOccurrences.startAnimation()

        val linkOccs = LinkOccurrences()
        linkOccs.related = linkedOccurrences

        ApiServiceHelper.apiService!!
            .linkOccurrences(getAccessToken(),linkOccs, viewOBOccurrenceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleLinkOccurrenceResult,::handleLinkOccurrenceApiError)
            .addTo(mCompositeDisposable)

    }

    private fun handleLinkOccurrenceResult(response: Response<ResponseBody>) {
        val status = JSONObject(response.body()?.string().toString()).getBoolean("status")
        if(status){
            Utils.showSnackbar(parentActivity,"Occurrence's linked successfully", R.drawable.ic_check_circle)
            showLoadingButtonSuccess()
        } else {
            Utils.showSnackbar(parentActivity,"Occurrence's already linked!!!", R.drawable.ic_error_red)
            showLoadingButtonError()
        }

        // clear the selection
        linkedOccurrences.clear()
        // then go back
        parentActivity.supportFragmentManager.popBackStack()
    }

    private fun handleLinkOccurrenceApiError(e: Throwable) {
        Timber.tag("OB List Error").e(e)
        showLoadingButtonError()
        // clear the selection
        linkedOccurrences.clear()
        // then go back
        parentActivity.supportFragmentManager.popBackStack()

    }

    private fun linkExhibitToOccurrence() {
        if(linkedOccurrenceId.isNotEmpty()) {
            btnLinkOccurrences.startAnimation()
            ApiServiceHelper.apiService!!
                .linkExhibitToOccurrence(getAccessToken(), selectedExhibitIdFromList, linkedOccurrenceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::handleLinkExhibitResult,::handleLinkExhibitError)
                .addTo(mCompositeDisposable)
        }

    }

    private fun handleLinkExhibitResult(response: Response<ResponseBody>) {
        val status = JSONObject(response.body()?.string().toString()).getBoolean("status")
        if(status){
            Utils.showSnackbar(parentActivity,"Exhibit linked successfully to " + linkedOBNumber, R.drawable.ic_check_circle)
            showLoadingButtonSuccess()
        } else {
            Utils.showSnackbar(parentActivity,"Exhibit already linked to "  + linkedOBNumber, R.drawable.ic_error_red)
            showLoadingButtonError()
        }
        // then go back
        parentActivity.supportFragmentManager.popBackStack()
    }
    private fun handleLinkExhibitError(e: Throwable) {
        Timber.tag("Exhibit List Error").e(e)
        // then go back
        parentActivity.supportFragmentManager.popBackStack()
    }




    private fun showLoadingButtonSuccess() {
        btnLinkOccurrences.doneLoadingAnimation(R.color.btnGreen, Utils.getBitmapFromVectorDrawable(mContext,R.drawable.ic_check_circle))
        btnLinkOccurrences.revertAnimation()
    }

    private fun showLoadingButtonError() {
        btnLinkOccurrences.revertAnimation()
    }


    private fun handleApiError(e: Throwable) {
        Timber.tag("OB List Error").e(e)
        Utils.showSnackbar(parentActivity, "Failed to fetch occurrence listing, please try again later!!", R.drawable.ic_error_red)
        showLoadingUi(false)
    }

    private fun showLoadingUi(fetchingOccurrences: Boolean){

        if(fetchingOccurrences) {
            fetch_occurrences.visibility = View.VISIBLE
            txtFetchingOccurrences.visibility = View.VISIBLE
            txtFetchingOccurrences.text = getString(R.string.fetching_list_msg)
            report_filters.visibility = View.GONE
        } else {
            fetch_occurrences.visibility = View.GONE
            txtFetchingOccurrences.visibility = View.GONE
            txtFetchingOccurrences.text = ""
            report_filters.visibility = View.VISIBLE
        }

    }

    private fun initSpinners() {

        // populate the spinners
        spinner_ob_report_filter.adapter = ArrayAdapter<String>(
            parentActivity,
            android.R.layout.simple_spinner_dropdown_item,
            Utils.getArrayListFromJsonArray(Utils.getJsonArrayFromFile(mContext, "report_filters.json"), "")
        )

        spinner_ob_report_filter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (parent!!.getChildAt(0) as TextView).setTextColor(parentActivity.resources.getColor(R.color.colorAccent))
                filterReport(spinner_ob_report_filter.selectedItem.toString())
            }
        }

        spinner_ob_report_filter.setSelection(0, true)
    }

    private fun initSearchView() {

        search_ob_number.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchReport(s.toString())
            }

        })
    }

    private fun filterReport(reportType: String) {
        var occList: List<Occurrence>? = null
        if (reportType.equals("All",true)){
            occList = occurrencesList
        }

        if (reportType.equals("Arrest",true)){
            occList = occurrencesList.filter {
                it.occurrenceType.equals("Arrest",false)
            }

        }

        if (reportType.equals("Report",true)){
            occList = occurrencesList.filter {
                it.occurrenceType.equals("Report",false)
            }
        }

        val args = arguments
        val adapter = ViewOBAdapter(occList as MutableList<Occurrence>, parentActivity, args!!.getString("action")!!)
        mRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

    }

    private fun searchReport(searchString: String){

        val occList= when {
            searchString.isNotEmpty() -> occurrencesList.filter { it.occurrenceNo.contains(searchString,true) }
            else -> occurrencesList
        }
        val args = arguments
        val adapter = ViewOBAdapter(occList as MutableList<Occurrence>, parentActivity, args!!.getString("action")!!)
        mRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

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