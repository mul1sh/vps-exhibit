package com.verve.vps.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.verve.vps.R

class ViewOBActionsAdapter(private val OBActions: Array<String>) : RecyclerView.Adapter<ViewOBActionsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        holder.txtOBActionName.text = OBActions[i]
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewOBActionsAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_ob_actions_item_layout, parent, false))
    }

    override fun getItemCount() = OBActions.size

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val txtOBActionName = v.findViewById(R.id.txtOBActionName) as TextView
    }

}