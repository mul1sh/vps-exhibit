package com.verve.vps.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DividerItemDecoration

import com.verve.vps.R
import com.verve.vps.adapters.ViewOBActionsAdapter
import com.verve.vps.helpers.AccessTokenHelper.getAccessToken
import com.verve.vps.helpers.ApiServiceHelper
import com.verve.vps.helpers.Constants.OCCURRENCE_NO
import com.verve.vps.helpers.OBActionsHelper.relatedExhibits
import com.verve.vps.helpers.OBActionsHelper.relatedOccurrences

import com.verve.vps.helpers.OBReportHelper.viewOBOccurrenceId
import com.verve.vps.helpers.PreferencesHelper.sharedPreferences
import com.verve.vps.helpers.RecyclerItemClickListener
import com.verve.vps.models.ExhibitListResult
import com.verve.vps.models.OccurrencesListResult
import com.verve.vps.models.RelatedOccurrencesResult
import com.verve.vps.utils.Utils

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_ob_actions_item_layout.view.*

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber
import java.io.File


@Suppress("PLUGIN_WARNING")
class ViewOBActionsFragment : Fragment() {

    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context
    private lateinit var mRecyclerView: RecyclerView
    private val apiService  = ApiServiceHelper.apiService!!
    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var currentView: View
    private var currentIndex: Int? = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.view_ob_actions, container, false)
    }

    override fun onResume() {
        super.onResume()

        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = parentActivity.findViewById(R.id.ob_actions_recycler_view) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView.adapter = ViewOBActionsAdapter(arrayOf(getString(R.string.generate_abstract_txt), getString(R.string.email_ob_txt), getString(R.string.resolve_txt), getString(R.string.assign_officer_txt),
            getString(R.string.forward_ob_txt), getString(R.string.link_occurrence_txt), getString(R.string.link_exhibit_txt), getString(R.string.related_exhibits_txt),
            getString(R.string.related_occurrences_txt)))
        mRecyclerView.addItemDecoration(DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL))
        mRecyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(mContext,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, i: Int) {
                        handleOBSummaryActions(view, i)
                    }
                })
        )

    }

    private fun handleOBSummaryActions(view: View, action:Int) {
        currentView = view
        currentIndex = action
        currentView.isEnabled = false

        if(action == 0){
            downloadAbstract()
        }

        if(action == 1) {
            emailOB()
        }

        if(action == 2) {
            resolveOccurrence()
        }

        if(action == 3) {
            assignOfficers()
        }

        if(action == 4) {
            forwardToDCIO()
        }

        if(action == 5) {
            linkOccurrences()
        }

        if(action == 6) {
            linkExhibits()
        }

        if(action == 7) {
            viewRelatedExhibits()
        }

        if(action == 8) {
            viewRelatedOccurrences()
        }

        if(currentView.progress_circular !== null){
            currentView.progress_circular.visibility = View.VISIBLE
        }

    }


    private fun emailOB() {
        val builder = AlertDialog.Builder(parentActivity)
        builder.setView(layoutInflater.inflate(R.layout.email_ob_layout, null))
            // Add action buttons
        .setPositiveButton(getString(R.string.email_ob_action)
        ) {dialog, _ ->

            val dialogObj = Dialog::class.java.cast(dialog)
            val txtEmailOb = dialogObj!!.findViewById<EditText>(R.id.txtEmailOb)
            emailAbstract(txtEmailOb.text.toString())
        }
        .setNegativeButton(R.string.btn_cancel
        ) { dialog, _ ->
            if(currentView.progress_circular !== null) currentView.progress_circular.visibility = View.GONE
            dialog.cancel()
        }
        builder.create().setCanceledOnTouchOutside(false)
        builder.setCancelable(false)
        builder.show()
    }

    private fun downloadAbstract() {

        apiService
            .downloadAbstract(getAccessToken(),viewOBOccurrenceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleApiResult,::handleApiError )
            .addTo(mCompositeDisposable)
    }

    private fun emailAbstract(email: String) {

        apiService
            .emailAbstract(getAccessToken(), email, viewOBOccurrenceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleApiResult,::handleApiError )
            .addTo(mCompositeDisposable)
    }

    private fun resolveOccurrence() {
        apiService
            .resolveOccurrence(getAccessToken(),viewOBOccurrenceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleApiResult,::handleApiError )
            .addTo(mCompositeDisposable)
    }


    private fun assignOfficers() {
        apiService
            .getOfficersListingAsJson(getAccessToken())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleApiResult,::handleApiError )
            .addTo(mCompositeDisposable)
    }

    private fun forwardToDCIO() {
        apiService
            .forwardToDCIO(getAccessToken(),viewOBOccurrenceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleApiResult,::handleApiError )
            .addTo(mCompositeDisposable)
    }

    private fun linkOccurrences() {
        val args = Bundle()
        val newFragment = ViewOBFragment()
        args.putString("title", "Link Occurrences")
        args.putString("action", "link")
        newFragment.arguments = args
        Utils.goToFragment(newFragment,parentActivity)
    }

    private fun linkExhibits() {

        val args = Bundle()
        val newFragment = ExhibitListingFragment()
        args.putString("title", "Link Exhibits")
        args.putString("action", "link")
        newFragment.arguments = args
        Utils.goToFragment(newFragment,parentActivity)

    }

    private fun viewRelatedOccurrences() {
        apiService
            .getRelatedOccurrences(getAccessToken(), viewOBOccurrenceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleRelatedOccurrencesResult,::handleOccurrencesListingApiError )
            .addTo(mCompositeDisposable)
    }

    private fun handleRelatedOccurrencesResult(ro: RelatedOccurrencesResult) {

        if (ro.status) {
            relatedOccurrences = ro.related

            if(relatedOccurrences.size > 0) {
                val args = Bundle()
                args.putString("title", "Related Occurrences")
                args.putString("action", "related")
                val newFragment = ViewOBFragment()
                newFragment.arguments = args
                Utils.goToFragment(newFragment,parentActivity)
            } else {
                Utils.showSnackbar(parentActivity,"Sorry, no related occurrences found for this occurrence!", R.drawable.ic_error_red)
            }

        }
    }

    private fun handleOccurrencesListingApiError(e: Throwable) {
        Timber.e(e)
        Utils.showSnackbar(parentActivity, "Sorry, no related occurrences found for this occurrence!", R.drawable.ic_error_red)
        if(currentView.progress_circular !== null){
            currentView.progress_circular.visibility = View.GONE
        }
        currentView.isEnabled = true
    }


    private fun viewRelatedExhibits() {

        apiService
            .getRelatedExhibits(getAccessToken(), viewOBOccurrenceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleRelatedExhibitsResult,::handleExhibitListingApiError )
            .addTo(mCompositeDisposable)

    }

    private fun handleRelatedExhibitsResult(exhibitResult: ExhibitListResult) {

        if (exhibitResult.status) {
            relatedExhibits = exhibitResult.exhibits

            if(relatedExhibits.size > 0) {

                val args = Bundle()
                args.putString("title", "Related Exhibits")
                args.putString("action", "related")
                val newFragment = ExhibitListingFragment()
                newFragment.arguments = args
                Utils.goToFragment(newFragment,parentActivity)
            } else {
                Utils.showSnackbar(parentActivity,"Sorry, no related exhibits found for this occurrence!", R.drawable.ic_error_red)
            }

        }
    }

    private fun handleExhibitListingApiError(e: Throwable) {
        Timber.e(e)
        Utils.showSnackbar(parentActivity, "Sorry, no related exhibits found for this occurrence!", R.drawable.ic_error_red)
        if(currentView.progress_circular !== null){
            currentView.progress_circular.visibility = View.GONE
        }
        currentView.isEnabled = true
    }


    private fun handleApiResult(response: Response<ResponseBody>) {
        if(response.isSuccessful){

            if(currentIndex == 0) {
                var abstractName = "abstract.pdf"
                var obNumber = ""
                sharedPreferences.apply {
                    obNumber = getString(OCCURRENCE_NO,"")!!
                }
                if(obNumber.isNotEmpty()) {
                    abstractName = "${obNumber.replace('/', '-')}.pdf"
                }
                if(Utils.writeResponseBodyToDisk(response.body()!!,abstractName,mContext, null)){
                    val intent = Intent(Intent.ACTION_VIEW)
                    val file = File(mContext.getExternalFilesDir(null), abstractName)
                    val fileURI = FileProvider.getUriForFile(mContext, "${mContext.applicationContext.packageName}.com.verve.vps.provider", file)
                    intent.setDataAndType(fileURI, "application/pdf")
                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    startActivity(intent)
                }
            }

            if(currentIndex == 1) {
                Utils.showSnackbar(parentActivity,"OB Number and Abstract sent to email successfully", R.drawable.ic_check_circle)
            }

            if(currentIndex == 2) {
                val status = JSONObject(response.body()?.string().toString()).getBoolean("status")
                if(status){
                    Utils.showSnackbar(parentActivity,"Occurrence resolved successfully", R.drawable.ic_check_circle)
                }
                else {
                    Utils.showSnackbar(parentActivity,"Occurrence already resolved", R.drawable.ic_error_red)
                }

            }

            if(currentIndex == 3) {
                val args = Bundle()
                val officersArray = JSONObject(response.body()?.string().toString()).getJSONArray("officers")
                val newFragment = OfficerListingFragment()
                args.putString("officersArray", officersArray.toString())
                newFragment.arguments = args
                Utils.goToFragment(newFragment,parentActivity)
            }

            if(currentIndex == 4) {

                val status = JSONObject(response.body()?.string().toString()).getBoolean("status")
                if(status){
                    Utils.showSnackbar(parentActivity,"Occurrence forwarded to DCIO successfully", R.drawable.ic_check_circle)
                }
                else {
                    Utils.showSnackbar(parentActivity,"Unable to forward occurrence, please try again later", R.drawable.ic_error_red)
                }

            }

            if(currentView.progress_circular !== null){
                currentView.progress_circular.visibility = View.GONE
            }


        }

        currentView.isEnabled = true
    }



    private fun handleApiError(e: Throwable) {
        Timber.e(e)
        Utils.showSnackbar(parentActivity, "OB Action ${currentView.txtOBActionName.text} failed!!", R.drawable.ic_error_red)

        if(currentView.progress_circular !== null){
            currentView.progress_circular.visibility = View.GONE
        }

        currentView.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        mCompositeDisposable.clear()
    }


    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

    companion object {
        fun newInstance(): ViewOBActionsFragment = ViewOBActionsFragment()
    }

}