package com.zaxai.sudokugame.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zaxai.sudokugame.R

class NumKeyboardAdapter(private val numList: List<Int>):RecyclerView.Adapter<NumKeyboardAdapter.ViewHolder>() {
    private var onRecyclerItemClickListener:(number: Int)->Unit={}

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val numKeyboardText:TextView=view.findViewById(R.id.numKeyboardText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.numkeyboard_item,parent,false)
        val viewHolder=ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            onRecyclerItemClickListener(numList[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val num=numList[position]
        holder.numKeyboardText.text=num.toString()
        holder.numKeyboardText.setBackgroundResource(R.color.white)
    }

    override fun getItemCount()=numList.size

    fun setOnRecyclerItemClickListener(l:(number: Int)->Unit){
        onRecyclerItemClickListener=l
    }
}