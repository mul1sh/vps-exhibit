package com.verve.vps.adapters

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.verve.vps.R
import com.verve.vps.helpers.OBReportHelper.assignedOBOfficers
import org.json.JSONArray


class OfficersListingAdapter(private val officersList: JSONArray) : RecyclerView.Adapter<OfficersListingAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val officerFirstName = officersList.getJSONObject(i).getString("firstname")
        val officerLastName = officersList.getJSONObject(i).getString("lastname")
        val officerSurname = officersList.getJSONObject(i).getString("surname")
        val officerTitle = officersList.getJSONObject(i).getString("role")
        holder.txtOfficerName.text = "$officerTitle, $officerFirstName $officerLastName $officerSurname"
        holder.txtOfficerServiceNo.text = officersList.getJSONObject(i).getString("serviceno")

        // bind the item position
        holder.bind(i)
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): OfficersListingAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.assign_officers_item_layout, parent, false))
    }

    override fun getItemCount() = officersList.length()

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), CompoundButton.OnCheckedChangeListener{

        val txtOfficerName = v.findViewById(R.id.txtOfficerName) as TextView
        val txtOfficerServiceNo = v.findViewById(R.id.txtOfficerServiceNo) as TextView

        private val mCheckedTextView = v.findViewById(R.id.checkbox_select_officer) as CheckBox
        private val itemStateArray = SparseBooleanArray()

        init {
            mCheckedTextView.setOnCheckedChangeListener(this)
        }


        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            val adapterPosition = adapterPosition

            if(!itemStateArray.get(adapterPosition, false) && isChecked) {
                itemStateArray.put(adapterPosition, true)
                assignedOBOfficers.add(officersList.getJSONObject(adapterPosition).getInt("id"))
            } else {
                itemStateArray.put(adapterPosition, false)
                assignedOBOfficers.remove(officersList.getJSONObject(adapterPosition).getInt("id"))
            }
        }

        fun bind(position: Int) {
            // use the sparse boolean array to check
            mCheckedTextView.isChecked = itemStateArray.get(position, false)
        }

    }

}