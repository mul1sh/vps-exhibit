package com.verve.vps.helpers

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class RecyclerItemClickListener(context: Context, private val mListener: OnItemClickListener?) :
    androidx.recyclerview.widget.RecyclerView.OnItemTouchListener {

    private var mGestureDetector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
        }
    })

    interface OnItemClickListener {
        fun onItemClick(view: View, i: Int)
    }

    override fun onInterceptTouchEvent(v: androidx.recyclerview.widget.RecyclerView, e: MotionEvent): Boolean {
        val mChildView = v.findChildViewUnder(e.x, e.y)
        if (mChildView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(mChildView, v.getChildPosition(mChildView))
            return true
        }
        return false
    }

    override fun onTouchEvent(view: androidx.recyclerview.widget.RecyclerView, event: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }
}