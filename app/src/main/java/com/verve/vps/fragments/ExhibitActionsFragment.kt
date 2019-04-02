package com.verve.vps.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.verve.vps.R
import com.verve.vps.adapters.ExhibitActionsAdapter
import com.verve.vps.helpers.AccessTokenHelper.getAccessToken
import com.verve.vps.helpers.ApiServiceHelper
import com.verve.vps.helpers.OBExhibitHelper.selectedExhibitIdFromList
import com.verve.vps.helpers.RecyclerItemClickListener
import com.verve.vps.utils.Utils

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.exhibit_actions_item_layout.view.*

import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber

@Suppress("PLUGIN_WARNING")
class ExhibitActionsFragment : Fragment() {


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
        return inflater.inflate(R.layout.exhibit_actions_layout, container, false)
    }

    override fun onResume() {
        super.onResume()

        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = parentActivity.findViewById(R.id.exhibit_actions_recycler_view) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView.adapter = ExhibitActionsAdapter(arrayOf("Link to Occurrence","Transfer","Dispose","Re-Admit"))
        mRecyclerView.addItemDecoration(DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL))
        mRecyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(mContext,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, i: Int) {
                        handleExhibitActions(view, i)
                    }
                })
        )

    }

    private fun handleExhibitActions(view: View, action:Int) {
        currentView = view
        currentIndex = action
        currentView.isEnabled = false


        if(currentView.exhibit_progress_circular !== null){
            currentView.exhibit_progress_circular.visibility = View.VISIBLE
        }

        if(action == 0) {
            linkOccurrence()
        }


        if(action == 1){
            transferExhibit()
        }

        if(action == 2) {

            Utils.showSnackbar(parentActivity, "Dispose Exhibit Api not found!!", R.drawable.ic_error_red)

            if(currentView.exhibit_progress_circular !== null){
                currentView.exhibit_progress_circular.visibility = View.GONE
            }

            currentView.isEnabled = true
        }


        if(action == 3){
            reAdmitExhibit()
        }


    }

    private fun linkOccurrence() {
        val args = Bundle()
        val newFragment = ViewOBFragment()
        args.putString("title", "Link Exhibit to Occurrence")
        args.putString("action", "link_exhibit")
        newFragment.arguments = args
        Utils.goToFragment(newFragment,parentActivity)

        if(currentView.exhibit_progress_circular !== null){
            currentView.exhibit_progress_circular.visibility = View.GONE
        }

        currentView.isEnabled = true
    }

    private fun linkExhibits() {
        val args = Bundle()
        val newFragment = ExhibitListingFragment()
        args.putString("title", "Link Exhibits")
        args.putString("action", "link")
        newFragment.arguments = args
        Utils.goToFragment(newFragment,parentActivity)
    }

    private fun transferExhibit() {
        val builder = AlertDialog.Builder(parentActivity)
        builder.setView(layoutInflater.inflate(R.layout.exhibit_transfer_layout, null))
            // Add action buttons
            .setPositiveButton("Transfer"
            ) {dialog, _ ->

                val dialogObj = Dialog::class.java.cast(dialog)
                val txtExhibitTransferDestination = dialogObj!!.findViewById<EditText>(R.id.txtExhibitTransferDestination)
                val txtExhibitTransferRemarks = dialogObj.findViewById<EditText>(R.id.txtExhibitTransferRemarks)

                transferExhibit(txtExhibitTransferDestination.text.toString(), txtExhibitTransferRemarks.text.toString())

            }
            .setNegativeButton(R.string.btn_cancel
            ) { dialog, _ ->
                if(currentView.exhibit_progress_circular !== null) currentView.exhibit_progress_circular.visibility = View.GONE
                dialog.cancel()
            }
        builder.create().setCanceledOnTouchOutside(false)
        builder.setCancelable(false)
        builder.show()
    }

    private fun transferExhibit(destination: String, remarks: String) {
        apiService
            .transferExhibit(getAccessToken(),destination,remarks, selectedExhibitIdFromList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleApiResult,::handleApiError )
            .addTo(mCompositeDisposable)
    }

    private fun reAdmitExhibit() {

        apiService
            .reAdmitExhibit(getAccessToken(), selectedExhibitIdFromList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleApiResult,::handleApiError )
            .addTo(mCompositeDisposable)

    }

    private fun handleApiResult(response: Response<ResponseBody>) {
        if (response.isSuccessful) {

            if(currentIndex == 1) {
                val status = JSONObject(response.body()?.string().toString()).getBoolean("status")

                if(status) {
                    Utils.showSnackbar(parentActivity,"Exhibit transferred successfully", R.drawable.ic_check_circle)
                } else {
                    Utils.showSnackbar(parentActivity,"Exhibit already transferred!!", R.drawable.ic_error_red)
                }
            }

            if(currentIndex == 3) {
                val status = JSONObject(response.body()?.string().toString()).getBoolean("status")

                if(status) {
                    Utils.showSnackbar(parentActivity,"Exhibit re-admitted successfully", R.drawable.ic_check_circle)
                } else {
                    Utils.showSnackbar(parentActivity,"There was a problem in re-admitting the exhibit, please try again later!!", R.drawable.ic_error_red)
                }
            }
        }



        if(currentView.exhibit_progress_circular !== null){
            currentView.exhibit_progress_circular.visibility = View.GONE
        }

        currentView.isEnabled = true
    }


    private fun handleApiError(e: Throwable) {
        Timber.e(e)
        Utils.showSnackbar(parentActivity, "Exhibit Action ${currentView.txtExhibitActionName.text} failed!!", R.drawable.ic_error_red)

        if(currentView.exhibit_progress_circular !== null){
            currentView.exhibit_progress_circular.visibility = View.GONE
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
        fun newInstance(): ExhibitActionsFragment = ExhibitActionsFragment()
    }


}