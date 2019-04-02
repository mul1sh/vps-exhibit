package com.verve.vps.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

import com.verve.vps.R
import com.verve.vps.fragments.ExhibitDetailsFragment
import com.verve.vps.helpers.OBExhibitHelper.selectedExhibitIdFromList
import com.verve.vps.helpers.OBReportHelper.linkedExhibitId
import com.verve.vps.models.Exhibit
import com.verve.vps.utils.Utils

class ExhibitListingAdapter(private val ExhibitItems: MutableList<Exhibit>,
                            private val activity: AppCompatActivity,
                            private val action: String) : RecyclerView.Adapter<ExhibitListingAdapter.ViewHolder>() {

    private var selectedPosition = -1// no selection by default

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {

        holder.txtExhibitId.text = ExhibitItems[i].id
        holder.txtExhibitName.text = ExhibitItems[i].description
        holder.txtExhibitType.text = ExhibitItems[i].type
        holder.txtExhibitCategoryType.text = ExhibitItems[i].exhibitCategory

        if(action != "related") {
            holder.txtExhibitCategory.text = ExhibitItems[i].category
        } else {
            holder.txtExhibitCategory.text = Utils.getExhibitItemCategoryFromId(activity.applicationContext, ExhibitItems[i].categoryId)
        }

        holder.mCheckedTextView.isChecked = selectedPosition == i
        holder.mCheckedTextView.setOnClickListener {

            if( i == selectedPosition) {
                holder.mCheckedTextView.isChecked = false
                selectedPosition = -1
            } else {
                selectedPosition = i
                notifyDataSetChanged()
                linkedExhibitId = ExhibitItems[i].id
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exhibit_item_layout, parent, false))

    override fun getItemCount(): Int = ExhibitItems.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener{

        val txtExhibitId = view.findViewById(R.id.txtExhibitId) as TextView
        val txtExhibitName = view.findViewById(R.id.txtExhibitName) as TextView
        val txtExhibitType = view.findViewById(R.id.txtExhibitType) as TextView
        val txtExhibitCategoryType = view.findViewById(R.id.txtExhibitCategoryType) as TextView
        val txtExhibitCategory = view.findViewById(R.id.txtExhibitCategory) as TextView

        val mCheckedTextView = view.findViewById(R.id.checkbox_select_exhibit) as CheckBox

        init {

            if (action.equals("link", true)) {
                mCheckedTextView.visibility = View.VISIBLE
                view.isClickable = false
            }

            if (action.equals("view", true)) {
                mCheckedTextView.visibility = View.GONE
                view.setOnClickListener(this)
            }

            if (action.equals("related", true)) {
                mCheckedTextView.visibility = View.GONE
                view.isClickable = false
            }

        }

        override fun onClick(v: View?) {
            selectedExhibitIdFromList = txtExhibitId.text.toString()

            // handle list item clicks
            val args = Bundle()
            val newFragment = ExhibitDetailsFragment()
            newFragment.arguments = args
            Utils.goToFragment(newFragment, activity)
        }


    }
}

