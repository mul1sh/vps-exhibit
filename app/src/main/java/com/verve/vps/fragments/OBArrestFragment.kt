package com.verve.vps.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

import com.verve.vps.R
import com.verve.vps.adapters.GridviewAdapter
import com.verve.vps.helpers.RecyclerItemClickListener
import com.verve.vps.utils.Utils

class OBArrestFragment : Fragment() {

    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context
    private lateinit var newFragment: androidx.fragment.app.Fragment

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

        //  init the variables
        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext

        // set the title of the fragment
        parentActivity.supportActionBar!!.title = "OB Arrest"

        initRecyclerViews()
    }

    private fun initRecyclerViews() {
        val mRecyclerView = parentActivity.findViewById(R.id.grid_recycler_view) as RecyclerView
        val columnCount = resources.getInteger(R.integer.gridview_columns)
        mRecyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(mContext, columnCount)
        mRecyclerView.adapter = GridviewAdapter(getGridViewImages(), getGridViewText())

        class RecyclerViewListener : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, i: Int) {
                var fragmentSelected = true
                when (i) {
                    0 -> newFragment = FingerPrintFragment()
                    else -> fragmentSelected = false
                }

                if (::newFragment.isInitialized && fragmentSelected) {
                    val args = Bundle()
                    args.putString("title", getGridViewText()[i])
                    newFragment.arguments = args
                    Utils.goToFragment(newFragment,parentActivity)
                }

            }
        }

        mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(mContext, RecyclerViewListener()))

    }

    // get the gridview images
    private fun getGridViewImages():Array<Int> = arrayOf(
        R.drawable.ic_cell_booking,
        R.drawable.ic_cell_manager
       )

    // get the gridview text
    private fun getGridViewText():Array<String> = arrayOf(
        "Cell Booking",
        "Cell Manager")


}