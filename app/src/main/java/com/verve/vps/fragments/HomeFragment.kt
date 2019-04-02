package com.verve.vps.fragments

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.GridLayoutManager

import com.google.gson.Gson

import com.verve.vps.adapters.GridviewAdapter
import com.verve.vps.helpers.Constants.LOGIN_USER_DATA
import com.verve.vps.helpers.PreferencesHelper.sharedPreferences
import com.verve.vps.helpers.RecyclerItemClickListener
import com.verve.vps.models.UserData
import com.verve.vps.utils.Utils
import com.verve.vps.R


class HomeFragment : androidx.fragment.app.Fragment() {

    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context
    private lateinit var newFragment: Fragment


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.recycler_gridview, container, false)
    }

    override fun onResume() {
        super.onResume()

        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext
        sharedPreferences.apply{
            val userData = Gson().fromJson<UserData>(getString(LOGIN_USER_DATA,""), UserData::class.java)
            parentActivity.supportActionBar!!.title = "Station: ${userData.station}"
        }


        initRecyclerViews()
    }

    private fun initRecyclerViews() {
        val mRecyclerView = parentActivity.findViewById(R.id.grid_recycler_view) as androidx.recyclerview.widget.RecyclerView
        val columnCount = resources.getInteger(R.integer.gridview_columns)
        mRecyclerView.layoutManager = GridLayoutManager(mContext, columnCount)
        mRecyclerView.setHasFixedSize(false)
        mRecyclerView.adapter = GridviewAdapter(getGridViewImages(), getGridViewText())

        mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(mContext,
            object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, i: Int) {
                    var fragmentSelected = true
                    when (i) {
                        0 -> newFragment = OBFragment()
                        1 -> newFragment = ExhibitFragment()
                        else -> fragmentSelected = false
                    }

                    if (::newFragment.isInitialized && fragmentSelected) {
                        val args = Bundle()
                        args.putString("title", getGridViewText()[i])
                        newFragment.arguments = args
                        Utils.goToFragment(newFragment,parentActivity)
                    }
                }
            }))

    }


    // get the gridview images
    private fun getGridViewImages():Array<Int> = arrayOf(
        R.drawable.ic_occurrence_book,
        R.drawable.ic_exhibit,
    //    R.drawable.ic_registers,
        R.drawable.ic_case_manager,

        R.drawable.ic_crime_db,
        R.drawable.ic_watchlist,
        R.drawable.ic_dashboard
    )

    // get the gridview text
    private fun getGridViewText():Array<String> = arrayOf(
        parentActivity.resources.getString(R.string.occurence_book),
        parentActivity.resources.getString(R.string.exhibit),
        "Prosecution",
        "Crime DB",
        "Police Watchlist",
        "Analytics"

    )

}

