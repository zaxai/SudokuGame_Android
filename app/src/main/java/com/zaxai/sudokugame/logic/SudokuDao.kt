package com.zaxai.sudokugame.logic

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SudokuDao(private val context: Context) {
    companion object{
        const val sudokuItemKey="sudoku_item"
        const val highlightKey="highlight"
        const val count="count"
        const val longClickKey="long_click"
    }

    enum class LongClickOperation{
        LONG_CLICK_EMPTY,LONG_CLICK_CLEAR
    }

    private fun sharedPreferences()=PreferenceManager.getDefaultSharedPreferences(context)

    fun needHighlightSameNumber(): Boolean {
        return sharedPreferences().getBoolean(highlightKey, true)
    }

    fun isCalcCountDisplayed():Boolean{
        return sharedPreferences().getBoolean(count, false)
    }

    fun getLongClickOperation()=when(sharedPreferences().getString(longClickKey,"empty")){
        "clear"->LongClickOperation.LONG_CLICK_CLEAR
        else ->LongClickOperation.LONG_CLICK_EMPTY
    }

    fun saveItemList(itemList: List<SudokuItem>){
        val itemNumList=ArrayList<String>()
        for (item in itemList){
            if(item.number.length==1) {
                itemNumList.add(item.number)
            }else{
                itemNumList.add("")
            }
        }
        sharedPreferences().edit {
            putString(sudokuItemKey, Gson().toJson(itemNumList))
        }
    }

    fun syncItemList(itemList: List<SudokuItem>){
        val itemString=sharedPreferences().getString(sudokuItemKey,"")
        if(itemString!=null&&itemString.isNotEmpty()) {
            val itemNumList = Gson().fromJson<ArrayList<String>>(itemString,object:TypeToken<ArrayList<String>>(){}.type)
            if(itemNumList.size==itemList.size) {
                for (i in itemList.indices) {
                    itemList[i].number = itemNumList[i]
                    itemList[i].lock=false
                }
            }
        }
    }
}