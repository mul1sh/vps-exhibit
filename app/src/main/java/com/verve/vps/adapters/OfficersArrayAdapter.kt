package com.verve.vps.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.verve.vps.R
import com.verve.vps.models.Officers

class OfficersArrayAdapter( mContext: Context, mResource: Int, mResourceId: Int, private val officers: List<Officers>)
    : ArrayAdapter<Officers>(mContext,mResource, mResourceId, officers){

    private val mInflater: LayoutInflater = LayoutInflater.from(mContext)


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var cv : View? = convertView
        if(convertView == null) {
            cv = mInflater.inflate(R.layout.officer_listing,parent, false);
        }
        return rowView(cv!!, position)
    }


    override fun getView(position: Int, convertView: View?,  parent: ViewGroup): View {
        var cv : View? = convertView
        if(convertView == null) {
            cv = mInflater.inflate(R.layout.officer_listing,parent, false);
        }
        return rowView(cv!!, position)
    }

    internal fun getOfficerId(position: Int) = officers[position].id



    private fun rowView(convertView: View, position: Int): View {

        val officer = getItem(position)!!
        val holder = ViewHolder(convertView)
        val name  = officer.firstname + " " + officer.lastname + " " + officer.surname
        holder.txtOfficerName.text = name

        return convertView
    }

    private inner class ViewHolder(v: View) {
        internal var txtOfficerName = v.findViewById<View>(R.id.officerName) as TextView
    }



}