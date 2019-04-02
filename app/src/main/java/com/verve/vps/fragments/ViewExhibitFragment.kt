package com.verve.vps.fragments

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

import com.verve.vps.GlideApp
import com.verve.vps.R
import com.verve.vps.helpers.AccessTokenHelper.getAccessToken
import com.verve.vps.helpers.ApiServiceHelper
import com.verve.vps.helpers.OBExhibitHelper.selectedExhibitIdFromList
import com.verve.vps.models.ExhibitDetailsResult
import com.verve.vps.utils.Utils

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.exhibit_details_layout.*
import timber.log.Timber

class ViewExhibitFragment: Fragment() {

    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context
    private val apiService  = ApiServiceHelper.apiService!!
    private val mCompositeDisposable = CompositeDisposable()
    private var imageName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.exhibit_details, container, false)
    }

    override fun onResume() {
        super.onResume()

        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext
        parentActivity.supportActionBar!!.title = "Exhibit Details"

        loadExhibitDetails()
    }


    private fun loadExhibitDetails() {
        fetching_exhibit_details.visibility = View.VISIBLE
        // load the occurrence details first
        apiService
            .getExhibitDetails(getAccessToken(),selectedExhibitIdFromList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleExhibitDetailsResult, ::handleApiError )
            .addTo(mCompositeDisposable)

    }

    private fun handleExhibitDetailsResult(exhibit: ExhibitDetailsResult) {
        if(exhibit.status) {
            val ex = exhibit.details
            var status =  ""
            if(ex.flag == "0") {
                status = "Retained"
            }
            if(ex.flag == "1") {
                status = "Transferred"
            }
            if(ex.flag == "2") {
                status = "Disposed"
            }
            exhibit_category.setText(ex.exhibitCategory)
            exhibit_status.setText(status)
            item_category.setText(ex.category)
            item_type.setText(ex.type)
            serial.setText(ex.serialNumber)
            quantity.setText(ex.quantity)
            unit.setText(ex.unit)
            officer_in_charge.setText(ex.userName)
            exhibit_desc.setText(ex.description)


            // then load the exhibit image
            imageName = ex.imagePath
            if(imageName !== null)  {
                loadExhibitImage(imageName!!)
            }
        }

        fetching_exhibit_details.visibility = View.GONE
    }

    private fun loadExhibitImage(imagePath:String) {
        loading_exhibit_progress.visibility = View.VISIBLE
        val uri = Uri.parse("https://vps.or.ke/storage/exhibit/$imagePath")

        GlideApp
        .with(this)
        .load(uri)
        .listener(object: RequestListener<Drawable>{
            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                loading_exhibit_progress.visibility = View.GONE
                return false
            }

            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                loading_exhibit_progress.visibility = View.GONE
                return false
            }

        }).into(exhibit_img).clearOnDetach()

        exhibit_img.setOnClickListener { Utils.zoomIntoImage(parentActivity,(exhibit_img.drawable as BitmapDrawable).bitmap, "Exhibit Image") }

    }


    private fun handleApiError(e: Throwable) {
        Timber.e(e)
        fetching_exhibit_details.visibility = View.GONE
        Utils.showSnackbar(parentActivity,"Unable to load the exhibit details, please check your internet connection!!", R.drawable.ic_warning_yellow)
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
        fun newInstance(): ViewExhibitFragment = ViewExhibitFragment()
    }


}