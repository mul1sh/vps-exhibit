package com.verve.vps.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.verve.vps.R
import com.verve.vps.adapters.ExhibitListingAdapter
import com.verve.vps.helpers.AccessTokenHelper.getAccessToken
import com.verve.vps.helpers.ApiServiceHelper
import com.verve.vps.helpers.OBActionsHelper.relatedExhibits
import com.verve.vps.helpers.OBReportHelper
import com.verve.vps.helpers.OBReportHelper.linkedExhibitId
import com.verve.vps.helpers.OBReportHelper.viewOBOccurrenceId
import com.verve.vps.helpers.RecyclerItemClickListener
import com.verve.vps.models.ExhibitListResult
import com.verve.vps.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.exhibit_list_layout.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber

class ExhibitListingFragment: Fragment() {

    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context
    private lateinit var mRecyclerView: RecyclerView
    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var adapter: ExhibitListingAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.exhibit_list_layout, container, false)
    }

    override fun onResume() {
        super.onResume()

        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext
        val args = arguments
        parentActivity.supportActionBar!!.title = args!!.getString("title")

        initRecyclerViews()
        initButtons()


        if(args.getString("action").equals("view",true)
            || args.getString("action").equals("link",true) ){
            fetchExhibits()
            initSwipeRefresh()
        } else {
            val adapter = ExhibitListingAdapter(relatedExhibits, parentActivity, args.getString("action")!!)
            mRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }

    }

    private fun initRecyclerViews() {
        mRecyclerView = parentActivity.findViewById(R.id.exhibit_recycler_view) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView.addItemDecoration(DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL))


    }

    private fun initSwipeRefresh() {
        swiperefresh.setOnRefreshListener {

            fetchExhibits()
            swiperefresh.isRefreshing = false
        }
    }

    private fun initButtons() {
        val args = arguments
        if(args!!.getString("action").equals("view",true)
            || args.getString("action").equals("related",true) ){
            btnLinkExhibits.visibility = View.GONE
        }
        if(args.getString("action").equals("link",true)){
            btnLinkExhibits.visibility = View.VISIBLE
            btnLinkExhibits.setOnClickListener {
                linkExhibits()
            }
        }

    }

    private fun fetchExhibits() {
        showLoadingUi(true)
        ApiServiceHelper.apiService!!
            .getExhibits(getAccessToken())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleApiResult,::handleApiError)
            .addTo(mCompositeDisposable)
    }

    private fun handleApiResult(exhibitResult: ExhibitListResult) {

        if(exhibitResult.status) {
            val args = arguments
            adapter = ExhibitListingAdapter(exhibitResult.exhibits, parentActivity, args!!.getString("action")!!)
            mRecyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

        }

        showLoadingUi(false)

    }

    private fun handleApiError(e: Throwable) {
        Timber.tag("Exhibit List Error").e(e)
        Utils.showSnackbar(parentActivity, "Failed to fetch exhibit listing, please try again later!!", R.drawable.ic_error_red)
        showLoadingUi(false)
    }


    private fun linkExhibits() {

        if(linkedExhibitId.isNotEmpty()){
            btnLinkExhibits.startAnimation()

            ApiServiceHelper.apiService!!
                .linkExhibitToOccurrence(getAccessToken(),linkedExhibitId, viewOBOccurrenceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::handleLinkExhibitResult,::handleLinkExhibitError)
                .addTo(mCompositeDisposable)
        }
        else {
            Utils.showSnackbar(parentActivity,"Please select an exhibit to link to this occurrence", R.drawable.ic_error_red)
        }


    }

    private fun handleLinkExhibitResult(response: Response<ResponseBody>) {
        val status = JSONObject(response.body()?.string().toString()).getBoolean("status")
        if(status){
            Utils.showSnackbar(parentActivity,"Exhibit linked successfully", R.drawable.ic_check_circle)
            showLoadingButtonSuccess()
        } else {
            Utils.showSnackbar(parentActivity,"Exhibit already linked!!!", R.drawable.ic_error_red)
            showLoadingButtonError()
        }

        // then go back
        parentActivity.supportFragmentManager.popBackStack()
    }
    private fun handleLinkExhibitError(e: Throwable) {
        Timber.tag("Exhibit List Error").e(e)
        showLoadingButtonError()

        // then go back
        parentActivity.supportFragmentManager.popBackStack()

    }

    private fun showLoadingButtonSuccess() {
        btnLinkExhibits.doneLoadingAnimation(R.color.btnGreen, Utils.getBitmapFromVectorDrawable(mContext,R.drawable.ic_check_circle))
        btnLinkExhibits.revertAnimation()
    }

    private fun showLoadingButtonError() {
        btnLinkExhibits.revertAnimation()
    }


    private fun showLoadingUi(fetchingExhibits: Boolean){

        if(fetchingExhibits) {

            if(fetch_exhibits != null) {
                fetch_exhibits.visibility = View.VISIBLE
                txtFetchingExhibits.text = "Fetching Exhibits"
            }


        } else {
            if(fetch_exhibits != null) {
                fetch_exhibits.visibility = View.GONE
                txtFetchingExhibits.visibility = View.GONE
            }
        }

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
        fun newInstance(): ExhibitListingFragment {
            val args = Bundle()
            val newFragment = ExhibitListingFragment()
            args.putString("title", "Exhibits")
            args.putString("action", "view")
            newFragment.arguments = args
            return newFragment
        }


    }

}