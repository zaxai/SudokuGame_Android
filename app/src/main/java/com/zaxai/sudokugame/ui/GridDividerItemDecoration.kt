package com.zaxai.sudokugame.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zaxai.sudokugame.R
import kotlin.math.roundToInt

class GridDividerItemDecoration(context: Context):RecyclerView.ItemDecoration(){
    private var divider: Drawable?
    private var bounds= Rect()

    companion object{
        const val TAG = "GridDividerItem"
    }

    init {
        divider=AppCompatResources.getDrawable(context,R.drawable.ic_grid_divider)
        if(divider==null){
            Log.w(TAG,"Drawable is null")
        }
    }

    fun setDrawable(drawable: Drawable){
        divider=drawable
    }

    fun getDrawable()=divider

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if(parent.layoutManager==null||divider==null){
            return
        }
        drawGrid(c,parent)
    }

    private fun drawGrid(c: Canvas, parent: RecyclerView){
        c.save()
        var left=0
        var right=0
        var top=0
        var bottom=0
        val layoutManager=parent.layoutManager as GridLayoutManager
        for(i in 0 until parent.childCount){
            val child=parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child,bounds)
            right=bounds.right+child.translationX.roundToInt()
            left=right-divider!!.intrinsicWidth
            top=bounds.top
            bottom=bounds.bottom
            divider!!.setBounds(left, top, right, bottom)
            divider!!.draw(c)
            left=bounds.left
            right=bounds.right
            bottom=bounds.bottom+ child.translationY.roundToInt()
            top=bottom-divider!!.intrinsicHeight
            divider!!.setBounds(left, top, right, bottom)
            divider!!.draw(c)
            if(i%layoutManager.spanCount==0){
                left=bounds.left+child.translationX.roundToInt()
                right=left+divider!!.intrinsicWidth
                top=bounds.top
                bottom=bounds.bottom
                divider!!.setBounds(left, top, right, bottom)
                divider!!.draw(c)
            }
            if(i<layoutManager.spanCount){
                left=bounds.left
                right=bounds.right
                top=bounds.top+ child.translationY.roundToInt()
                bottom=top+divider!!.intrinsicHeight
                divider!!.setBounds(left, top, right, bottom)
                divider!!.draw(c)
            }
        }
        c.restore()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if(divider==null){
            outRect.set(0,0,0,0)
        }else{
            val layoutManager=parent.layoutManager as GridLayoutManager
            val position=parent.getChildAdapterPosition(view)
            outRect.set(0, 0, divider!!.intrinsicWidth, divider!!.intrinsicHeight)
            if(position%layoutManager.spanCount==0){
                outRect.left=divider!!.intrinsicWidth
            }
            if(position<layoutManager.spanCount){
                outRect.top=divider!!.intrinsicHeight
            }
        }
    }
}