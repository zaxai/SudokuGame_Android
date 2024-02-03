package com.zaxai.sudokugame.ui

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
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
import java.math.BigInteger

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
            if(item.lock){
                holder.sudokuItemText.setTextColor(ContextCompat.getColor(this,R.color.dark_grey))
            }else {
                holder.sudokuItemText.setTextColor(ContextCompat.getColor(this, item.textColorId))
            }
            if(position==sudoku.posSel){
                holder.sudokuItemText.paintFlags=holder.sudokuItemText.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                val typedValue= TypedValue()
                if(theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary,typedValue,true)) {
                    holder.sudokuItemText.setBackgroundColor(typedValue.data)
                }
            }else{
                holder.sudokuItemText.paintFlags=holder.sudokuItemText.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                if(sudoku.needNotifySameNumber&&item==itemSel&&item.number.isNotEmpty()){
                    val typedValue= TypedValue()
                    if(theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary,typedValue,true)) {
                        holder.sudokuItemText.setBackgroundColor(typedValue.data)
                    }
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
}