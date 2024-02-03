package com.zaxai.sudokugame.logic

import com.zaxai.sudokugame.R

data class SudokuItem(var number:String){
    var textColorId= R.color.black
    var textBackgroundColorId= R.color.white
    var lock=false
}
