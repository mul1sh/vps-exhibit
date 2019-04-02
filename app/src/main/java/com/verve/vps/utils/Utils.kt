package com.verve.vps.utils

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.os.Build
import com.google.android.material.tabs.TabLayout
import android.widget.LinearLayout

import org.json.JSONArray
import org.json.JSONException

import com.androidadvance.topsnackbar.TSnackbar

import com.verve.vps.R
import com.verve.vps.helpers.Constants.AUTH_TOKEN
import com.verve.vps.helpers.Constants.OCCURRENCE_HEADING
import com.verve.vps.helpers.Constants.OCCURRENCE_NARRATIVE
import com.verve.vps.helpers.Constants.REPORTER_COUNTY_ID
import com.verve.vps.helpers.Constants.REPORTER_FORM_FILLED
import com.verve.vps.helpers.Constants.REPORTER_GENDER
import com.verve.vps.helpers.Constants.REPORTER_ID_PASSPORT_NUMBER
import com.verve.vps.helpers.Constants.REPORTER_PHONE_NUMBER
import com.verve.vps.helpers.Constants.REPORTER_SUB_COUNTY_ID
import com.verve.vps.helpers.Constants.GO_BACK_TO_REPORTER_FORM
import com.verve.vps.helpers.Constants.LOGIN_TOKEN
import com.verve.vps.helpers.Constants.LOGIN_USER_DATA
import com.verve.vps.helpers.Constants.OCCURRENCE_LATITUDE
import com.verve.vps.helpers.Constants.OCCURRENCE_LONGITUDE
import com.verve.vps.helpers.Constants.OCCURRENCE_PLACE_NAME
import com.verve.vps.helpers.Constants.OFFICER_NAME
import com.verve.vps.helpers.Constants.OFFICER_RANK
import com.verve.vps.helpers.Constants.OFFICER_SERVICE_NO
import com.verve.vps.helpers.Constants.REPORTER_CONSTITUENCY_SELECTED_POSITION
import com.verve.vps.helpers.Constants.REPORTER_COUNTY_SELECTED_POSITION
import com.verve.vps.helpers.Constants.REPORTER_GENDER_SELECTED_POSITION
import com.verve.vps.helpers.Constants.REPORTER_NAME
import com.verve.vps.helpers.Constants.USER_LOGGED_IN
import com.verve.vps.helpers.PreferencesHelper.sharedPreferences

import timber.log.Timber
import okhttp3.ResponseBody
import java.io.*
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import at.grabner.circleprogress.CircleProgressView
import kotlinx.android.synthetic.main.photo_viewer_layout.view.*


object Utils {

     // function navigates to a given fragment and adds it to the back stack
     @JvmStatic
    internal fun goToFragment(newFragment: Fragment, parentActivity: AppCompatActivity) {

        parentActivity.supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, newFragment)
            addToBackStack(null)
            commit()
        }

    }

    @JvmStatic
    internal fun replaceFragment(newFragment: Fragment, parentActivity: AppCompatActivity) {

        parentActivity.supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, newFragment)
            commit()
        }
    }
    @JvmStatic
    internal fun getJsonArrayFromFile(context: Context, fileName: String) =
        JSONArray(context.assets.open(fileName).bufferedReader().use(BufferedReader::readText))

    @JvmStatic
    internal fun getArrayListFromJsonArray(jsonArray: JSONArray, objectProperty: String): ArrayList<String> {
        val jsonResult = ArrayList<String>()

        try {
            (0..(jsonArray.length()-1)).forEach { it ->
                jsonResult += if(objectProperty.isEmpty()) jsonArray.getString(it) // dealing with an array of strings
                else jsonArray.getJSONObject(it).getString(objectProperty)   // dealing with an array of json objects
            }
        }
        catch (e:  JSONException) {
            Timber.e(e)
            e.printStackTrace()
        }

        return jsonResult
    }

    @JvmStatic
    internal fun getRelationshipIdFromSelectedPosition(
        context: Context,
        selectedPosition: Int
    ) = getJsonArrayFromFile(context,"relationships.json").getJSONObject(selectedPosition).getString("id")

    @JvmStatic
    internal fun getCountyIdFromSelectedPosition(
        context: Context,
        selectedPosition: Int
    ) = getJsonArrayFromFile(context,"counties.json").getJSONObject(selectedPosition).getString("id")

    @JvmStatic
    internal fun getConstituencyIdFromSelectedPosition(
        context: Context,
        selectedPosition: Int
    ) = getJsonArrayFromFile(context,"constituencies.json").getJSONObject(selectedPosition).getString("id")

    @JvmStatic
    internal fun getGenderTypeFromSelectedPosition(
        context: Context,
        selectedPosition: Int
    ) = getJsonArrayFromFile(context,"genders.json").getJSONObject(selectedPosition).getString("type")

    @JvmStatic
    internal fun loadCountyConstituencies(context: Context, selectedPosition: Int) : ArrayList<String> {
        val filteredConstituencies = ArrayList<String>()
        val constituenciesArray = getJsonArrayFromFile(context,"constituencies.json")
        val countyId: String = getCountyIdFromSelectedPosition(context,selectedPosition)

        try {
            (0..(constituenciesArray.length()-1)).forEach { it ->

                val constituency = constituenciesArray.getJSONObject(it)

                if(constituency.length() > 0 && constituency.getString("county_id") == countyId) {
                    filteredConstituencies += constituency.getString("name")
                }
            }
        }
        catch (e: JSONException) {
            Timber.e(e);
            e.printStackTrace()
        }

        return filteredConstituencies
    }

    @JvmStatic
    internal fun hideKeyboard(view: View?, context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    @JvmStatic
    internal fun showSnackbar(activity: Activity, message:String, icon: Int) {
        val snackbar = TSnackbar.make(activity.findViewById(android.R.id.content), message, TSnackbar.LENGTH_LONG)
            snackbar.setIconLeft(icon, 24f)
            snackbar.setIconPadding(8)
            snackbar.setMaxWidth(3000)
        val snackbarView = snackbar.view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            snackbarView.setBackgroundColor(activity.resources.getColor(R.color.bg_color_dark,activity.theme))
        }
        else {
            @Suppress("DEPRECATION")
            snackbarView.setBackgroundColor(activity.resources.getColor(R.color.bg_color_dark))
        }
        snackbar.show()

    }

    @JvmStatic
    internal fun goToTab(tabLayout: TabLayout, tabIndex: Int) {
        val tab = tabLayout.getTabAt(tabIndex)
        tab!!.select()
    }

    @JvmStatic
    internal fun makeReporterStartingForm(reporterIsStartingForm: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(GO_BACK_TO_REPORTER_FORM,reporterIsStartingForm)
            apply()
        }
    }

    @JvmStatic
    internal fun clearOBReportFields(){
        sharedPreferences.edit().apply {
            // reporter details
            putString(REPORTER_ID_PASSPORT_NUMBER, "")
            putString(REPORTER_NAME, "")
            putString(REPORTER_PHONE_NUMBER, "")
            putString(REPORTER_GENDER, "")
            putString(REPORTER_COUNTY_ID, "")
            putString(REPORTER_SUB_COUNTY_ID, "")
            putBoolean(REPORTER_FORM_FILLED,true)
            putInt(REPORTER_GENDER_SELECTED_POSITION, 0)
            putInt(REPORTER_COUNTY_SELECTED_POSITION, 46)
            putInt(REPORTER_CONSTITUENCY_SELECTED_POSITION, 0)

            // occurrence details
            putString(OCCURRENCE_HEADING,"")
            putString(OCCURRENCE_NARRATIVE, "")

            // occurrence location details
            putString(OCCURRENCE_LATITUDE, "")
            putString(OCCURRENCE_LONGITUDE, "")
            putString(OCCURRENCE_PLACE_NAME, "")

            // ob form details
            putBoolean(GO_BACK_TO_REPORTER_FORM,true)

            apply()
        }
    }


    @JvmStatic
    internal fun clearCachedFields(){

        // first of all clear the ob report fields
        clearOBReportFields()

        // and then the cached ones
        sharedPreferences.edit().apply {

            //  login details
            putBoolean(USER_LOGGED_IN, false)
            putString(AUTH_TOKEN, "")
            putString(LOGIN_USER_DATA, "")
            putString(LOGIN_TOKEN, "")
            putString(OFFICER_NAME, "")
            putString(OFFICER_SERVICE_NO, "")
            putString(OFFICER_RANK, "")

            apply()
        }
    }

    @JvmStatic
    internal fun disableTabs(tabLayout: TabLayout) {
        val tabStrip = tabLayout.getChildAt(0) as LinearLayout
        tabStrip.isEnabled = false
        (0 until tabStrip.childCount).forEach { i ->
            tabStrip.getChildAt(i).isClickable = false
        }
    }

    @JvmStatic
    private fun enableTabs(tabLayout: TabLayout) {
        val tabStrip = tabLayout.getChildAt(0) as LinearLayout
        tabStrip.isEnabled = true
        (0 until tabStrip.childCount).forEach { i ->
            tabStrip.getChildAt(i).isClickable = true
        }
    }

    @JvmStatic
    internal fun writeResponseBodyToDisk(body: ResponseBody, fileName: String, mContext: Context, cpv: CircleProgressView?): Boolean {

        try {

            val futureStudioIconFile = File(mContext.getExternalFilesDir(null), fileName)
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0

                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)

                while (true) {
                    val read = inputStream!!.read(fileReader)

                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()

                    val percentageDownloaded = (fileSizeDownloaded.toFloat() / fileSize.toFloat()) * 100

                    if(cpv !== null){
                        cpv.setValueAnimated(percentageDownloaded)
                        cpv.setText(percentageDownloaded.toString())
                    }

                    Timber.tag("FILE DOWNLOAD").i( "file download: $fileSizeDownloaded of $fileSize")
                }

                outputStream.flush()

                return true
            } catch (e: IOException) {
                Timber.tag("FILE DOWNLOAD").e(e)
                return false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            return false
        }

    }

    @JvmStatic
    internal fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        val bitmap: Bitmap?

        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        bitmap = if (drawable!!.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }

        val canvas = Canvas(bitmap!!)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    @JvmStatic
    internal fun getExhibitItemCategoryFromId(context: Context, id: String): String  {
        var categoryName = ""
        val categoriesArray = getJsonArrayFromFile(context, "item_categories.json")

        try {
            (0..(categoriesArray.length()-1)).forEach {
                val category =categoriesArray.getJSONObject(it)
                if(category.length() > 0 && category.getString("id") == id) {
                    categoryName = category.getString("name")
                }
            }
        }
        catch (e: JSONException) {
            Timber.e(e);
            e.printStackTrace()
        }
        return categoryName
    }

    @JvmStatic
    internal fun zoomIntoImage(activity: AppCompatActivity, bitmap: Bitmap, title: String) {
        val view = LayoutInflater.from(activity.applicationContext).inflate(R.layout.photo_viewer_layout, null)
        view.view_img.setImageBitmap(bitmap)
        val builder = AlertDialog.Builder(activity)
        builder.setView(view)
        builder.setTitle(title)
        builder.create().setCanceledOnTouchOutside(true)
        builder.setCancelable(true)
        builder.show()
    }

}