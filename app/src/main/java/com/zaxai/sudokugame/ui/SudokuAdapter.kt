package com.zaxai.sudokugame.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zaxai.sudokugame.R
import com.zaxai.sudokugame.logic.Sudoku

class SudokuAdapter(private val fragment: SudokuFragment,private val sudoku: Sudoku):
    RecyclerView.Adapter<SudokuAdapter.ViewHolder>(){
    private var onRecyclerItemClickListener:(position: Int,rect:Rect)->Unit={_,_->}
    private var onRecyclerItemLongClickListener:(position:Int)->Boolean={false}

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val sudokuItemText:TextView=view.findViewById(R.id.sudokuItemText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.sudoku_item,parent,false)
        val viewHolder=ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            fragment.viewModel.sudokuPosSelChange(viewHolder.adapterPosition){
                for (i in it){
                    notifyItemChanged(i)
                }
            }
            val rect=Rect()
            viewHolder.itemView.getGlobalVisibleRect(rect)
            onRecyclerItemClickListener(viewHolder.adapterPosition,rect)
        }
        viewHolder.itemView.setOnLongClickListener {
            fragment.viewModel.sudokuPosSelChange(viewHolder.adapterPosition){
                for (i in it){
                    notifyItemChanged(i)
                }
            }
            onRecyclerItemLongClickListener(viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=sudoku.itemList[position]
        val itemSel=sudoku.itemList[sudoku.posSel]
        holder.sudokuItemText.text=item.number
        fragment.context?.apply {
            if(item.number.length==1){
                if(resources.configuration.orientation==Configuration.ORIENTATION_PORTRAIT){
                    holder.sudokuItemText.textSize=30.0F
                }else {
                    holder.sudokuItemText.textSize=25.0F
                }
            }else{
                if(resources.configuration.orientation==Configuration.ORIENTATION_PORTRAIT){
                    holder.sudokuItemText.textSize=10.0F
                }else {
                    holder.sudokuItemText.textSize=8.0F
                }
            }
            if(item.lock){
                holder.sudokuItemText.setTextColor(ContextCompat.getColor(this,R.color.dark_grey))
            }else {
                holder.sudokuItemText.setTextColor(ContextCompat.getColor(this, item.textColorId))
            }
            if(position==sudoku.posSel){
                holder.sudokuItemText.paintFlags=holder.sudokuItemText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                holder.sudokuItemText.setBackgroundColor(getColorFromResource(this,androidx.appcompat.R.attr.colorPrimary))
            }else{
                holder.sudokuItemText.paintFlags=holder.sudokuItemText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                if(sudoku.needNotifySameNumber&&item==itemSel&&item.number.isNotEmpty()){
                    holder.sudokuItemText.setBackgroundColor(getColorFromResource(this,androidx.appcompat.R.attr.colorPrimary))
                }else {
                    holder.sudokuItemText.setBackgroundResource(item.textBackgroundColorId)
                }
            }
        }
    }

    override fun getItemCount()=sudoku.itemList.size

    fun setOnRecyclerItemClickListener(l:(position: Int,rect:Rect)->Unit){
        onRecyclerItemClickListener=l
    }

    fun setOnRecyclerItemLongClickListener(l:(position:Int)->Boolean){
        onRecyclerItemLongClickListener=l
    }

    private fun getColorFromResource(context: Context,resId:Int):Int{
        var color=0
        val typedValue= TypedValue()
        if(context.theme.resolveAttribute(resId,typedValue,true)){
            color=typedValue.data
        }
        return color
    }
}