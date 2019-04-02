package com.verve.vps.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.verve.vps.R

class GridviewAdapter(private val mThumbIds: Array<Int>, private val mThumbText: Array<String>) : RecyclerView.Adapter<GridviewAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: GridviewAdapter.ViewHolder, i: Int) {

        holder.textView.text = mThumbText[i]
        holder.imageView.setImageResource(mThumbIds[i])
    }

    override fun onCreateViewHolder(vGroup: ViewGroup, i: Int): GridviewAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(vGroup.context).inflate(R.layout.recycler_gridview_details, vGroup, false))
    }

    override fun getItemCount(): Int {
        return mThumbIds.size
    }

    inner class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        val textView: TextView = v.findViewById(R.id.text) as TextView
        val imageView: ImageView = v.findViewById(R.id.image) as ImageView

    }

}