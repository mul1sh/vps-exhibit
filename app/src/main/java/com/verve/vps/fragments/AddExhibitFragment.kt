package com.verve.vps.fragments

import android.app.Activity.RESULT_OK

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment

import com.verve.vps.GlideApp


import com.verve.vps.R
import com.verve.vps.adapters.OfficersArrayAdapter
import com.verve.vps.helpers.AccessTokenHelper.getAccessToken
import com.verve.vps.helpers.ApiServiceHelper
import com.verve.vps.helpers.OBExhibitHelper.attachedExhibitUri
import com.verve.vps.helpers.OBExhibitHelper.desc
import com.verve.vps.helpers.OBExhibitHelper.imageSelectedFromCamera
import com.verve.vps.helpers.OBExhibitHelper.imageSelectedFromFile
import com.verve.vps.helpers.OBExhibitHelper.itemType
import com.verve.vps.helpers.OBExhibitHelper.quantity
import com.verve.vps.helpers.OBExhibitHelper.selectedExhibitCategory
import com.verve.vps.helpers.OBExhibitHelper.selectedItemCategory
import com.verve.vps.helpers.OBExhibitHelper.selectedOIC
import com.verve.vps.helpers.OBExhibitHelper.selectedUnit
import com.verve.vps.helpers.OBExhibitHelper.serial
import com.verve.vps.models.OfficersListing
import com.verve.vps.utils.FileUtils
import com.verve.vps.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.add_exhibit_layout.*
import kotlinx.android.synthetic.main.attach_exhibit_image.*
import kotlinx.android.synthetic.main.exhibit_form_layout.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.MediaType
import java.io.File
import java.io.IOException

import java.text.SimpleDateFormat
import java.util.*


class AddExhibitFragment: Fragment() {

    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context
    private val apiService = ApiServiceHelper.apiService!!
    private val mCompositeDisposable = CompositeDisposable()
    private val READ_REQUEST_CODE  = 42
    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var unitSelected : String
    private lateinit var category : String
    private lateinit var oic: String
    private lateinit var exhibitCategory: String
    private lateinit var officerAdapter: OfficersArrayAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.exhibit_form_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init the public vars
        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext


    }

    override fun onResume() {
        super.onResume()

        showLoading()
        loadOfficers()
        initButtons()
        initSpinners()
        showImage()
        revertForm()
    }


    private fun loadOfficers() {

        apiService
            .getOfficersListingAsObject(getAccessToken())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleLoadOfficerAPIResult,::handleLoadOfficerApiError )
            .addTo(mCompositeDisposable)

    }

    private fun handleLoadOfficerAPIResult(officersListing: OfficersListing){

        if(officersListing.status) {
            officerAdapter =  OfficersArrayAdapter(mContext,R.layout.officer_listing,R.id.officerName ,officersListing.officers)
            spinner_oic.adapter = officerAdapter

            spinner_oic.visibility = View.VISIBLE
            lbl_oic.visibility = View.VISIBLE
            fetching_officers.visibility = View.GONE
            revertForm()
        } else {
            fetching_officers.visibility = View.VISIBLE
        }
    }

    private fun handleLoadOfficerApiError(e: Throwable) {
        Timber.e(e)
        btnSaveExhibit.visibility = View.GONE
        fetching_officers.visibility = View.GONE
        Utils.showSnackbar(parentActivity, "Unable to load officers, please check your internet connection and try again", R.drawable.ic_error_red)
    }


    private fun showLoading() {
        fetching_officers.visibility = View.VISIBLE
    }

    private fun initSpinners() {

        // populate the spinners
        spinner_exhibit_category.adapter = ArrayAdapter<String>(
            parentActivity,
            android.R.layout.simple_spinner_dropdown_item,
            Utils.getArrayListFromJsonArray(Utils.getJsonArrayFromFile(mContext, "exhibit_categories.json"), "name")
        )

        spinner_unit.adapter = ArrayAdapter<String>(
            parentActivity,
            android.R.layout.simple_spinner_dropdown_item,
            Utils.getArrayListFromJsonArray(Utils.getJsonArrayFromFile(mContext, "units_of_measure.json"), "name")
        )

        spinner_item_category.adapter = ArrayAdapter<String>(
            parentActivity,
            android.R.layout.simple_spinner_dropdown_item,
            Utils.getArrayListFromJsonArray(Utils.getJsonArrayFromFile(mContext, "item_categories.json"), "name")
        )

        // add the listeners
        spinner_exhibit_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                exhibitCategory =  spinner_exhibit_category.selectedItem.toString()
                selectedExhibitCategory =  position

            }
        }

        spinner_oic.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                oic = officerAdapter.getOfficerId(position)
                selectedOIC = position

            }
        }

        spinner_unit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                unitSelected = (position + 1).toString()
                selectedUnit = position

            }
        }

        spinner_item_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                category = (position + 1).toString()
                selectedItemCategory = position

            }
        }


    }

    private fun initButtons() {
        btnAttachImageFromFile.setOnClickListener {
            cacheForm()
            imageSelectedFromCamera = false
            imageSelectedFromFile = false
            performFileSearch()
        }

        btnAttachImageFromCamera.setOnClickListener {
            cacheForm()
            imageSelectedFromCamera = false
            imageSelectedFromFile = false
            dispatchTakePictureIntent()
        }

        btnSaveExhibit.setOnClickListener {
            saveExhibit()
        }

        btnClear.setOnClickListener {
            resetForm()
        }
    }



    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir: File = parentActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
         //   mCurrentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(parentActivity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Utils.showSnackbar(parentActivity, "Unable to store picture, please make sure you have enough storage space!!", R.drawable.ic_error_red)

                    return
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    attachedExhibitUri = FileProvider.getUriForFile(mContext, "${mContext.applicationContext.packageName}.com.verve.vps.provider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, attachedExhibitUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }


    private fun performFileSearch() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Select Exhibit Picture"),READ_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            resultData?.data?.also {
                attachedExhibitUri = it
                imageSelectedFromFile = true
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageSelectedFromCamera = true
        }

    }

    private fun showImage() {

        if(imageSelectedFromFile) {

            val file = FileUtils.getFile(mContext, attachedExhibitUri)!!
            GlideApp.with(this).apply {
                load(file).centerInside().into(exhibit_img)
            }

            exhibit_img.visibility = View.VISIBLE

        }

        if(imageSelectedFromCamera) {

            GlideApp.with(this).apply {
                load(attachedExhibitUri).centerInside().into(exhibit_img)
            }
            exhibit_img.visibility = View.VISIBLE

        }

        // reset the error in the buttons if any
        showButtonSelectSuccess()

    }

    private fun showLoadingButtonSuccess() {
        btnSaveExhibit.doneLoadingAnimation(R.color.btnGreen, Utils.getBitmapFromVectorDrawable(mContext,R.drawable.ic_check_circle))
        btnSaveExhibit.revertAnimation()
    }

    private fun showLoadingButtonError() {
        btnSaveExhibit.revertAnimation()
    }


    private fun saveExhibit() {
        // show the save animation
        btnSaveExhibit.startAnimation()
        // hide the keyboard
        Utils.hideKeyboard(btnSaveExhibit,mContext)
        // clear the form errors
        resetFormErrors()

        var cancel = false
        var focusView: View? = null

        // get the txt fields
        val itemType = txtItemType.text.toString()
        val serial = txtSerial.text.toString()
        val quantity = txtQuantity.text.toString()
        val desc = txtExhibitDesc.text.toString()

        if (TextUtils.isEmpty(itemType)) {
            txtItemType.error = "Item type is required"
            focusView = txtItemType
            cancel = true
        }

        if (TextUtils.isEmpty(serial)) {
            txtSerial.error = "Serial number is required"
            focusView = txtSerial
            cancel = true
        }

        if (TextUtils.isEmpty(quantity)) {
            txtSerial.error = "Quantity is required"
            focusView = txtSerial
            cancel = true
        }

        if (TextUtils.isEmpty(desc)) {
            txtExhibitDesc.error = "Exhibit description is required"
            focusView = txtExhibitDesc
            cancel = true
        }

        if (attachedExhibitUri == null) {
            Utils.showSnackbar(parentActivity,"Please select an exhibit picture", R.drawable.ic_error_red)
            showButtonSelectErrors()
            focusView = txtExhibitDesc
            cancel = true
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()

            // show the form error
            showLoadingButtonError()

        } else {
            saveExhibitToServer()
        }
    }

    private fun saveExhibitToServer() {
        val itemType = txtItemType.text.toString()
        val serial = txtSerial.text.toString()
        val quantity = txtQuantity.text.toString()
        val desc = txtExhibitDesc.text.toString()
        val file = FileUtils.getFile(mContext, attachedExhibitUri)


        // create RequestBody instance from file
        val requestFile = RequestBody.create(MediaType.parse(activity!!.contentResolver!!.getType(attachedExhibitUri!!)!!), file!!)

        // MultipartBody.Part is used to send also the actual file name
        val exhibitImage = MultipartBody.Part.createFormData("exhibit_image", file.name, requestFile)

        val eCategory: String

        if(exhibitCategory.equals("Pending Unknown Investigation",true)){
            eCategory = "PUA"
        }
        else if(exhibitCategory.equals("Pending A Known Arrest",true)){
            eCategory = "PAKA"
        } else {
            eCategory = "other"
        }

        // add other parts within the multipart request
        val exhibitCategory = RequestBody.create(okhttp3.MultipartBody.FORM, eCategory)
        val category = RequestBody.create(okhttp3.MultipartBody.FORM, category)
        val type = RequestBody.create(okhttp3.MultipartBody.FORM, itemType)
        val itemQuantity = RequestBody.create(okhttp3.MultipartBody.FORM, quantity)
        val unit = RequestBody.create(okhttp3.MultipartBody.FORM, unitSelected)
        val serialNumber = RequestBody.create(okhttp3.MultipartBody.FORM, serial)
        val description = RequestBody.create(okhttp3.MultipartBody.FORM, desc)
        val officerInCharge = RequestBody.create(okhttp3.MultipartBody.FORM, oic)
        val stationId = RequestBody.create(okhttp3.MultipartBody.FORM, "1")

        apiService
            .saveExhibit(
                getAccessToken(),
                exhibitCategory,
                category,
                type,
                itemQuantity,
                unit,
                serialNumber,
                description ,
                officerInCharge,
                stationId,
                exhibitImage
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (::handleApiResult,::handleApiError )
            .addTo(mCompositeDisposable)

    }

    private fun resetForm() {
        // clear the form errors
        resetFormErrors()
        // clear the form
        txtItemType.setText("")
        txtSerial.setText("")
        txtQuantity.setText("")
        txtExhibitDesc.setText("")
        exhibit_img.visibility = View.GONE
        exhibit_img.setImageDrawable(null)

        imageSelectedFromCamera = false
        imageSelectedFromFile = false
        attachedExhibitUri = null


        spinner_exhibit_category.setSelection(0)
        spinner_oic.setSelection(0)
        spinner_unit.setSelection(0)
        spinner_item_category.setSelection(0)
    }

    private fun handleApiResult(response: Response<ResponseBody>) {
        if (response.isSuccessful) {
            val status = JSONObject(response.body()?.string().toString()).getBoolean("status")
            if (status) {
                showLoadingButtonSuccess()
                Utils.showSnackbar(parentActivity, "Exhibit added successfully", R.drawable.ic_check_circle)
            }
        } else {
            showLoadingButtonError()
        }

        clearExhibitSelection()
    }

    private fun handleApiError(e: Throwable) {
        Timber.e(e)
        showLoadingButtonError()
        clearExhibitSelection()
        Utils.showSnackbar(parentActivity, "Adding exhibits failed, please check your connection!!", R.drawable.ic_error_red)
    }

    private fun showButtonSelectErrors() {
        btnAttachImageFromCamera.setBackgroundColor(Color.RED)
        btnAttachImageFromFile.setBackgroundColor(Color.RED)
    }

    private fun showButtonSelectSuccess() {
        // set the buttons back to green
        btnAttachImageFromCamera.setBackgroundColor(resources.getColor(R.color.btnGreen))
        btnAttachImageFromFile.setBackgroundColor(resources.getColor(R.color.btnGreen))
    }

    private fun resetFormErrors() {
        // Reset errors.
        txtItemType.error = null
        txtSerial.error = null
        txtQuantity.error = null
        txtExhibitDesc.error = null
        showButtonSelectSuccess()
    }

    // to cache the form when doing file selections
    private fun cacheForm() {
        itemType = txtItemType.text.toString()
        serial = txtSerial.text.toString()
        quantity = txtQuantity.text.toString()
        desc = txtExhibitDesc.text.toString()
    }

    // revert form to its cached state
    private fun revertForm() {

        txtItemType.setText(if(itemType != null) itemType else "")
        txtSerial.setText(if(serial != null) serial else "")
        txtQuantity.setText(if(quantity != null) quantity else "")
        txtExhibitDesc.setText(if(desc != null) desc else "")

        spinner_oic.setSelection( if(selectedOIC != null ) selectedOIC!! else 0)
        spinner_exhibit_category.setSelection( if(selectedExhibitCategory != null )selectedExhibitCategory!! else 0)
        spinner_item_category.setSelection( if(selectedItemCategory != null ) selectedItemCategory!! else 0)
        spinner_unit.setSelection( if(selectedUnit != null ) selectedUnit!! else 0)
    }

    // clear the form selection cache
    private fun clearExhibitSelection() {
        itemType = null
        serial = null
        quantity = null
        desc = null

        selectedOIC = null
        selectedExhibitCategory = null
        selectedItemCategory = null
        selectedUnit = null

        imageSelectedFromCamera = false
        imageSelectedFromFile = false
        attachedExhibitUri = null

        //then reset the form too
        resetForm()
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
        fun newInstance(): AddExhibitFragment = AddExhibitFragment()
    }
}