package com.verve.vps.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.verve.vps.R

class ExhibitActionsAdapter(private val ExhibitActions: Array<String>) : RecyclerView.Adapter<ExhibitActionsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        holder.txtExhibitActionName.text = ExhibitActions[i]
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ExhibitActionsAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.exhibit_actions_item_layout, parent, false))
    }


    override fun getItemCount() = ExhibitActions.size


    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val txtExhibitActionName = v.findViewById(R.id.txtExhibitActionName) as TextView
    }

}