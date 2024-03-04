package com.zaxai.sudokugame.link

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zaxai.sudokugame.R
import com.zaxai.sudokugame.logic.Sudoku
import com.zaxai.sudokugame.logic.SudokuDao
import com.zaxai.sudokugame.logic.SudokuItem

class SudokuViewModel:ViewModel() {
    var sudoku: Sudoku
    var isCalculating=false
    val calcCount:LiveData<Int>
        get()=_calcCount
    private val _calcCount=MutableLiveData<Int>()
    val calcResult:LiveData<Boolean>
        get()=_calcResult
    private val _calcResult=MutableLiveData<Boolean>()

    init {
        val sudokuItemList=ArrayList<SudokuItem>()
        for (i in 0 until Sudoku.SUDOKU_TOTAL_SIZE){
            val sudokuItem=SudokuItem("")
            when(Sudoku.getBlock(Sudoku.getItemPoint(i))) {
                Sudoku.Block.BLOCK_2,
                Sudoku.Block.BLOCK_4,
                Sudoku.Block.BLOCK_6,
                Sudoku.Block.BLOCK_8 -> sudokuItem.textBackgroundColorId=R.color.grey
                else -> sudokuItem.textBackgroundColorId=R.color.white
            }
            sudokuItemList.add(sudokuItem)
        }
        sudoku=Sudoku(sudokuItemList)
    }

    fun checkHighlightFlagChanged(context: Context)=(sudoku.needNotifySameNumber!=SudokuDao(context).needHighlightSameNumber())

    fun syncSudokuNotifySameNumberFlag(context: Context){
        sudoku.needNotifySameNumber=SudokuDao(context).needHighlightSameNumber()
    }

    fun isCalcCountDisplayed(context: Context)=SudokuDao(context).isCalcCountDisplayed()

    fun getLongClickOperation(context: Context)=SudokuDao(context).getLongClickOperation()

    fun saveSudokuItemList(context: Context)=SudokuDao(context).saveItemList(sudoku.itemList)

    fun syncSudokuItemList(context: Context)=SudokuDao(context).syncItemList(sudoku.itemList)

    fun startSudokuAutoCalc()=sudoku.startAutoCalc(_calcCount,_calcResult)

    fun endSudokuAutoCalc()=sudoku.setItemFromData()

    fun stopSudokuAutoCalc()=sudoku.stopAutoCalc()

    fun getSudokuSameNumberPosList(isIncludePosSel:Boolean=false)=sudoku.getSameNumberPosList(isIncludePosSel)

    fun sudokuPosSelChange(newPos:Int,block:(posNotifyList:List<Int>)->Unit){
        block(sudoku.posSelChange(newPos))
    }

    fun setSudokuItem(position:Int,number:String,block:(posNotifyList:List<Int>)->Unit){
        block(sudoku.setItem(position,number))
    }

    fun isSudokuItemLocked(position: Int)=sudoku.isItemLocked(position)

    fun sudokuItemLock(block:(posNotifyList:List<Int>)->Unit){
        block(sudoku.itemLock())
    }

    fun sudokuItemUnlock(block:(posNotifyList:List<Int>)->Unit){
        block(sudoku.itemUnlock())
    }

    fun sudokuItemSelEmpty(block:(posNotifyList:List<Int>)->Unit){
        block(sudoku.itemSelEmpty())
    }

    fun sudokuItemClear(block:(posNotifyList:List<Int>)->Unit){
        sudoku.itemListBackup()
        block(sudoku.itemClear())
    }

    fun sudokuItemRecovery(){
        sudoku.itemListRecovery()
    }

    fun sudokuRuleCheck():Boolean{
        sudoku.setDataFromItem()
        return sudoku.ruleCheck()
    }

    fun getSudokuLastRuleError()=sudoku.getLastRuleError()

    fun getSudokuConfirmedCount()=sudoku.getConfirmedCount()

    fun getSudokuUnconfirmedCount()=sudoku.getUnconfirmedCount()

    fun sudokuItemCandidate(block:(posNotifyList:List<Int>)->Unit){
        block(sudoku.itemCandidate())
    }
}