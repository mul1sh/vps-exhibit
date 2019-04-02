package com.verve.vps.adapters

import android.graphics.Color
import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import com.verve.vps.R
import com.verve.vps.fragments.OBReportSummaryFragment
import com.verve.vps.helpers.OBReportHelper.linkedOBNumber
import com.verve.vps.helpers.OBReportHelper.linkedOccurrenceId
import com.verve.vps.helpers.OBReportHelper.linkedOccurrences
import com.verve.vps.helpers.OBReportHelper.viewOBOccurrenceId
import com.verve.vps.models.Occurrence
import com.verve.vps.utils.Utils

class ViewOBAdapter(private val OBReportItems: MutableList<Occurrence>,
                    private val activity: AppCompatActivity,
                    private val action: String) : RecyclerView.Adapter<ViewOBAdapter.ViewHolder>() {

    private var selectedPosition = -1// no selection by default

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        holder.txtOBId.text = OBReportItems[i].id
        holder.txtOBDesc.text = OBReportItems[i].description
        holder.txtOBNumber.text = OBReportItems[i].occurrenceNo


        if(action != "related") {
            val obType = OBReportItems[i].occurrenceType
            holder.txtOBType.text = obType

            if(obType.trim().equals("Report",true)) {
                holder.txtOBType.setTextColor(ContextCompat.getColor(activity, R.color.obTypeReportColor))
            } else {
                holder.txtOBType.setTextColor(Color.parseColor("#1d9b52"))
            }

        }

        if(action.equals("link_exhibit",true)){
            holder.mCheckedTextView.isChecked = selectedPosition == i
            holder.mCheckedTextView.setOnClickListener {

                if( i == selectedPosition) {
                    holder.mCheckedTextView.isChecked = false
                    selectedPosition = -1
                } else {
                    selectedPosition = i
                    notifyDataSetChanged()
                    linkedOccurrenceId = OBReportItems[i].id
                    linkedOBNumber = OBReportItems[i].occurrenceNo
                }

            }

        } else {
            holder.bind(i)
        }


    }
    
    override fun onCreateViewHolder(parent: ViewGroup, i: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_ob_item_layout, parent, false))

    override fun getItemCount(): Int = OBReportItems.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener{
        
        val txtOBId = view.findViewById(R.id.txtOBId) as TextView
        val txtOBNumber = view.findViewById(R.id.txtOBNumber) as TextView
        val txtOBDesc = view.findViewById(R.id.txtOBDesc) as TextView
        val txtOBType = view.findViewById(R.id.txtOBType) as TextView

        val mCheckedTextView = view.findViewById(R.id.checkbox_select_ob) as CheckBox
        private val itemStateArray = SparseBooleanArray()


        init {

            if(action.equals("link",true)){
                mCheckedTextView.visibility = View.VISIBLE
                mCheckedTextView.setOnCheckedChangeListener(this)
                view.isClickable = false
            }


            if(action.equals("link_exhibit",true)){
                mCheckedTextView.visibility = View.VISIBLE
                view.isClickable = false
            }

            if(action.equals("view",true)){
                mCheckedTextView.visibility = View.GONE
                view.setOnClickListener(this)
            }

            if(action.equals("related",true)){
                mCheckedTextView.visibility = View.GONE
                view.isClickable = false
            }

        }

        override fun onClick(v: View?) {

            viewOBOccurrenceId =  txtOBId.text.toString()
            // handle list item clicks
            val args = Bundle()
            val newFragment = OBReportSummaryFragment()
            args.putString("title", "OB Summary")
            newFragment.arguments = args
            Utils.goToFragment(newFragment, activity)

        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            val adapterPosition = adapterPosition

            if(action.equals("link",true)) {

                if (!itemStateArray.get(adapterPosition, false) && isChecked) {
                    itemStateArray.put(adapterPosition, true)
                    linkedOccurrences.add(OBReportItems[adapterPosition].id.toInt())
                } else {
                    itemStateArray.put(adapterPosition, false)
                    linkedOccurrences.remove(OBReportItems[adapterPosition].id.toInt())
                }
            }

        }

        fun bind(position: Int) {
            if(action.equals("link",true)) {
                // use the sparse boolean array to check
                mCheckedTextView.isChecked = itemStateArray.get(position, false)
            }
        }

    }

}