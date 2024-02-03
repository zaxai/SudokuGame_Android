package com.zaxai.sudokugame.ui

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zaxai.sudokugame.R
import com.zaxai.sudokugame.logic.NumKeyboard

class NumKeyboardDialogFragment(private val size:Int,private val origin: Point):DialogFragment() {
    private var onItemClickListener:(number: Int)->Unit={}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.run {
            val view=layoutInflater.inflate(R.layout.fragment_numkeyboard,null)
            initView(view)
            val dialog=AlertDialog.Builder(context).setView(view).create()
            dialog.window?.apply {
                val rect= Rect()
                window.decorView.getWindowVisibleDisplayFrame(rect)
                val layoutParams=attributes
                layoutParams.width=size+decorView.paddingStart+decorView.paddingEnd
                layoutParams.height=size+decorView.paddingTop+decorView.paddingBottom
                layoutParams.gravity=Gravity.START or Gravity.TOP
                layoutParams.x=origin.x
                layoutParams.y=origin.y-rect.top
                layoutParams.alpha=0.8F
                attributes=layoutParams
            }
            dialog
        }?:super.onCreateDialog(savedInstanceState)
    }

    private fun initView(view: View){
        val numKeyboardRecyclerView:RecyclerView=view.findViewById(R.id.numKeyboardRecyclerView)
        val adapter=NumKeyboardAdapter(NumKeyboard.numList)
        adapter.setOnRecyclerItemClickListener {
            onItemClickListener(it)
            dismiss()
        }
        numKeyboardRecyclerView.layoutManager=GridLayoutManager(context,3)
        numKeyboardRecyclerView.adapter=adapter
        numKeyboardRecyclerView.addItemDecoration(GridDividerItemDecoration(requireContext()))
    }

    fun setOnItemClickListener(l:(number: Int)->Unit):NumKeyboardDialogFragment{
        onItemClickListener=l
        return this
    }

    fun show(fragment:FragmentManager){
        show(fragment,toString())
    }
}