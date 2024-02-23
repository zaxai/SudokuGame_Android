package com.zaxai.sudokugame.logic

import android.graphics.Point
import androidx.lifecycle.MutableLiveData
import kotlin.concurrent.thread

data class Sudoku(val itemList: List<SudokuItem>){
    var posSel=0
    var needNotifySameNumber=true
    private var dataList2=ArrayList<ArrayList<Int>>()
    private var dataFreezeList2=ArrayList<ArrayList<Int>>()
    private var total=0
    private var totalFreeze=0
    private var dataList3=ArrayList<ArrayList<ArrayList<Int>>>()
    private var autoCalcRun=false
    private var autoCalcThread= thread {  }
    private var itemNumberBackupList=ArrayList<String>()
    private var ruleErrorInfo=RuleErrorInfo(RuleErrorType.RULE_ERR_NONE,0,0)

    enum class Direction{
        DIR_ROW,DIR_COLUMN
    }

    enum class Block(val index:Int){
        BLOCK_1(0),
        BLOCK_2(1),
        BLOCK_3(2),
        BLOCK_4(3),
        BLOCK_5(4),
        BLOCK_6(5),
        BLOCK_7(6),
        BLOCK_8(7),
        BLOCK_9(8);

        companion object {
            fun fromInt(index:Int)=values().first{
                it.index==index
            }
        }
    }

    enum class RuleErrorType{
        RULE_ERR_NONE,RULE_ERR_ROW,RULE_ERR_COLUMN,RULE_ERR_BLOCK
    }

    class RuleErrorInfo(val type:RuleErrorType,val index:Int,val number:Int)

    companion object{
        const val SUDOKU_SIZE=9
        const val SUDOKU_TOTAL_SIZE=SUDOKU_SIZE*SUDOKU_SIZE

        fun getBlock(index:Int)=Block.fromInt(index)

        fun getBlock(x:Int,y:Int)=Block.fromInt(x/3*3+y/3)

        fun getBlock(pt:Point)=getBlock(pt.x,pt.y)

        fun getItemPoint(itemIndex:Int)=Point(itemIndex/ SUDOKU_SIZE,itemIndex% SUDOKU_SIZE)
    }

    init {
        for (i in 0 until SUDOKU_SIZE){
            dataList3.add(ArrayList<ArrayList<Int>>())
            val dataList=ArrayList<Int>()
            val dataFreezeList=ArrayList<Int>()
            for (j in 0 until SUDOKU_SIZE){
                dataList.add(0)
                dataFreezeList.add(0)
                dataList3[i].add(ArrayList<Int>())
                itemNumberBackupList.add("")
            }
            dataList2.add(dataList)
            dataFreezeList2.add(dataFreezeList)
        }
    }

    private fun getNumCount(num:Int, dir:Direction, index:Int):Int{
        var count=0
        if(index< SUDOKU_SIZE){
            for (i in 0 until SUDOKU_SIZE){
                when(dir){
                    Direction.DIR_ROW->{
                        if (dataList2[index][i]==num){
                            count++
                        }
                    }
                    Direction.DIR_COLUMN->{
                        if(dataList2[i][index]==num){
                            count++
                        }
                    }
                }
            }
        }
        return count
    }

    private fun getNumCount(num:Int, block:Block):Int{
        var count=0
        for (i in 0 until 3){
            for (j in 0 until 3){
                if(dataList2[(block.index/3)*3+i][(block.index%3)*3+j]==num){
                    count++
                }
            }
        }
        return count
    }

    private fun getFirstNumReverseIndex(num:Int, dir:Direction, index:Int):Int{
        for (i in 0 until SUDOKU_SIZE){
            when(dir){
                Direction.DIR_ROW->{
                    if(dataList2[index][i]==num){
                        return i
                    }
                }
                Direction.DIR_COLUMN->{
                    if(dataList2[i][index]==num){
                        return i
                    }
                }
            }
        }
        return -1
    }

    private fun threeLinesModule(dir:Direction, startIndex:Int):Int{
        var count=0
        for (num in 1..9){
            val indexExistList=ArrayList<Int>()
            val indexNoneList=ArrayList<Int>()
            val blockList=ArrayList<Int>()
            for (i in 0 until 3){
                if(getNumCount(num,dir,startIndex+i)==1){
                    indexExistList.add(startIndex+i)
                    blockList.add(getFirstNumReverseIndex(num,dir,startIndex+i)/3)
                }else{
                    indexNoneList.add(startIndex+i)
                }
            }
            if(indexExistList.size==2&&indexNoneList.size==1){
                val ptList=ArrayList<Point>()
                for(block in 0 until 3){
                    if (!blockList.contains(block)){
                        for (i in 0 until 3){
                            when(dir){
                                Direction.DIR_ROW->{
                                    val pt=Point()
                                    pt.x=indexNoneList[0]
                                    pt.y=block*3+i
                                    if(dataList2[pt.x][pt.y]==0&&getNumCount(num,Direction.DIR_COLUMN,pt.y)==0){
                                        ptList.add(pt)
                                    }
                                }
                                Direction.DIR_COLUMN->{
                                    val pt=Point()
                                    pt.x=block*3+i
                                    pt.y=indexNoneList[0]
                                    if(dataList2[pt.x][pt.y]==0&&getNumCount(num,Direction.DIR_ROW,pt.x)==0){
                                        ptList.add(pt)
                                    }
                                }
                            }
                        }
                        break
                    }
                }
                if(ptList.size==1){
                    dataList2[ptList[0].x][ptList[0].y]=num
                    count++
                }
            }
        }
        return count
    }

    private fun candidateModule(x:Int,y:Int):Int{
        var count=0
        if(dataList2[x][y]==0){
            val numList=ArrayList<Int>()
            getCandidateNum(x,y,numList)
            if(numList.size==1){
                dataList2[x][y]=numList[0]
                count++
            }
        }
        return count
    }

    private fun threeLinesAlgorithm():Int{
        var count=0
        for (i in 0 until 3){
            count+=threeLinesModule(Direction.DIR_ROW,i*3)
            count+=threeLinesModule(Direction.DIR_COLUMN,i*3)
        }
        return count
    }

    private fun candidateAlgorithm():Int{
        var count=0
        for (i in 0 until SUDOKU_SIZE){
            for (j in 0 until SUDOKU_SIZE){
                count+=candidateModule(i,j)
            }
        }
        return count
    }

    private fun basicAlgorithm():Int{
        return (threeLinesAlgorithm()+candidateAlgorithm())
    }

    private fun dataFreeze(){
        for(i in 0 until SUDOKU_SIZE){
            for(j in 0 until SUDOKU_SIZE){
                dataFreezeList2[i][j] = dataList2[i][j]
            }
        }
        totalFreeze=total
    }

    private fun dataUnfreeze(){
        for(i in 0 until SUDOKU_SIZE){
            for(j in 0 until SUDOKU_SIZE){
                dataList2[i][j] = dataFreezeList2[i][j]
            }
        }
        total=totalFreeze
    }

    private fun dataConvert(){
        for (i in 0 until SUDOKU_TOTAL_SIZE){
            val x=i/ SUDOKU_SIZE
            val y=i% SUDOKU_SIZE
            dataList3[x][y].clear()
            if(dataList2[x][y]!=0){
                dataList3[x][y].add(dataList2[x][y])
            }else{
                getCandidateNum(x,y,dataList3[x][y])
            }
        }
    }

    private fun candidateSel(candidateSize:Int):Boolean{
        for (i in 0 until SUDOKU_TOTAL_SIZE){
            val x=i/ SUDOKU_SIZE
            val y=i% SUDOKU_SIZE
            if(dataList3[x][y].size==candidateSize){
                dataList2[x][y]=dataList3[x][y][(0 until candidateSize).random()]
                total++
                return true
            }
        }
        return false
    }

    private fun randomCalc():Boolean{
        while (total< SUDOKU_TOTAL_SIZE){
            val count=basicAlgorithm()
            total+=count
            if(count==0){
                dataConvert()
                var find=false
                for(i in 2..9){
                    if(candidateSel(i)){
                        find=true
                        break
                    }
                }
                if(!find)
                    break
            }
        }
        return (total== SUDOKU_TOTAL_SIZE&&ruleCheck())
    }

    private fun autoCalcThread(count:MutableLiveData<Int>,result:MutableLiveData<Boolean>){
        autoCalcThread = thread {
            autoCalcRun = true
            var resultLocal = false
            var countLocal=0
            dataFreeze()
            do {
                count.postValue(++countLocal)
                if (randomCalc()) {
                    resultLocal = true
                    break
                } else {
                    dataUnfreeze()
                }
            } while (autoCalcRun)
            result.postValue(resultLocal)
            autoCalcRun = false
        }
    }

    fun setDataFromItem(){
        total=0
        for (i in 0 until SUDOKU_TOTAL_SIZE){
            if(itemList[i].number.isNotEmpty()) {
                dataList2[i / SUDOKU_SIZE][i % SUDOKU_SIZE] = itemList[i].number.toInt()
                total++
            }else{
                dataList2[i / SUDOKU_SIZE][i % SUDOKU_SIZE] =0
            }
        }
    }

    fun setItemFromData(){
        for (i in 0 until SUDOKU_TOTAL_SIZE){
            if(dataList2[i / SUDOKU_SIZE][i % SUDOKU_SIZE]!=0){
                itemList[i].number=dataList2[i / SUDOKU_SIZE][i % SUDOKU_SIZE].toString()
            }else{
                itemList[i].number=""
            }
        }
    }

    fun getCandidateNum(x:Int,y:Int,numList:ArrayList<Int>){
        numList.clear()
        val block=getBlock(x,y)
        for (num in 1..9){
            if(getNumCount(num,Direction.DIR_ROW,x)==0&&getNumCount(num,Direction.DIR_COLUMN,y)==0&&getNumCount(num,block)==0){
                numList.add(num)
            }
        }
    }

    fun ruleCheck():Boolean{
        for(num in 1..9){
            for (i in 0 until SUDOKU_SIZE){
                if(getNumCount(num,Direction.DIR_ROW,i)>1){
                    ruleErrorInfo= RuleErrorInfo(RuleErrorType.RULE_ERR_ROW,i,num)
                    return false
                }
                if(getNumCount(num,Direction.DIR_COLUMN,i)>1){
                    ruleErrorInfo= RuleErrorInfo(RuleErrorType.RULE_ERR_COLUMN,i,num)
                    return false
                }
                if(getNumCount(num, getBlock(i))>1) {
                    ruleErrorInfo= RuleErrorInfo(RuleErrorType.RULE_ERR_BLOCK,i,num)
                    return false
                }
            }
        }
        ruleErrorInfo= RuleErrorInfo(RuleErrorType.RULE_ERR_NONE,0,0)
        return true
    }

    fun getLastRuleError()=ruleErrorInfo

    fun basicCalc(){
        while (total< SUDOKU_TOTAL_SIZE){
            val count=basicAlgorithm()
            total+=count
            if(count==0){
                break
            }
        }
    }

    fun startAutoCalc(count:MutableLiveData<Int>,result:MutableLiveData<Boolean>){
        if(!autoCalcRun) {
            autoCalcThread.join()
            autoCalcThread(count,result)
        }
    }

    fun stopAutoCalc(){
        autoCalcRun=false
        autoCalcThread.join()
    }

    fun itemListBackup(){
        for (i in 0 until SUDOKU_TOTAL_SIZE){
            itemNumberBackupList[i]=itemList[i].number
        }
    }

    fun itemListRecovery(){
        for (i in 0 until SUDOKU_TOTAL_SIZE){
            itemList[i].number=itemNumberBackupList[i]
        }
    }

    fun getSameNumberPosList(isIncludePosSel:Boolean=false):List<Int>{
        val posSameNumberList=ArrayList<Int>()
        for (i in itemList.indices) {
            if(isIncludePosSel&&itemList[i]==itemList[posSel]){
                posSameNumberList.add(i)
            }else if(!isIncludePosSel&&itemList[i]==itemList[posSel]&&i!=posSel){
                posSameNumberList.add(i)
            }
        }
        return posSameNumberList
    }

    fun posSelChange(newPos:Int):List<Int>{
        val posNotifyList=ArrayList<Int>()
        if(newPos!=posSel){
            val posSelOld=posSel
            posSel=newPos
            if(needNotifySameNumber) {
                val itemSelOld = itemList[posSelOld]
                val itemSel = itemList[posSel]
                if (itemSelOld == itemSel) {
                    posNotifyList.add(posSelOld)
                    posNotifyList.add(posSel)
                } else {
                    for (i in itemList.indices) {
                        if (itemSelOld.number.isNotEmpty() && itemList[i] == itemSelOld) {
                            posNotifyList.add(i)
                        } else if (itemSelOld.number.isEmpty() && i == posSelOld) {
                            posNotifyList.add(i)
                        }
                        if (itemSel.number.isNotEmpty() && itemList[i] == itemSel) {
                            posNotifyList.add(i)
                        } else if (itemSel.number.isEmpty() && i == posSel) {
                            posNotifyList.add(i)
                        }
                    }
                }
            }else{
                posNotifyList.add(posSelOld)
                posNotifyList.add(posSel)
            }
        }
        return posNotifyList
    }

    fun setItem(position:Int,number:String):List<Int>{
        val posNotifyList=ArrayList<Int>()
        if(itemList[position].number!=number){
            val itemOld=SudokuItem(itemList[position].number)
            itemList[position].number=number
            val item=itemList[position]
            if(needNotifySameNumber) {
                for (i in itemList.indices) {
                    if (itemOld.number.isEmpty()) {
                        if (itemList[i] == item) {
                            posNotifyList.add(i)
                        }
                    } else {
                        if (itemList[i] == itemOld || itemList[i] == item) {
                            posNotifyList.add(i)
                        }
                    }
                }
            }else{
                posNotifyList.add(position)
            }
        }
        return posNotifyList
    }

    fun isItemLocked(position: Int)=itemList[position].lock

    fun itemLock():List<Int>{
        val posNotifyList=ArrayList<Int>()
        for (i in itemList.indices){
            if(itemList[i].number.isNotEmpty()&&!itemList[i].lock){
                itemList[i].lock=true
                posNotifyList.add(i)
            }
        }
        return posNotifyList
    }

    fun itemUnlock():List<Int>{
        val posNotifyList=ArrayList<Int>()
        for (i in itemList.indices){
            if(itemList[i].lock){
                itemList[i].lock=false
                posNotifyList.add(i)
            }
        }
        return posNotifyList
    }

    fun itemSelEmpty():List<Int>{
        val posNotifyList=ArrayList<Int>()
        if(!itemList[posSel].lock&&itemList[posSel].number.isNotEmpty()) {
            if(needNotifySameNumber) {
                for (i in itemList.indices) {
                    if (itemList[i] == itemList[posSel]) {
                        posNotifyList.add(i)
                    }
                }
            }else{
                posNotifyList.add(posSel)
            }
            itemList[posSel].number=""
        }
        return posNotifyList
    }

    fun itemClear():List<Int>{
        val posNotifyList=ArrayList<Int>()
        val itemSel=SudokuItem(itemList[posSel].number)
        itemSel.lock=itemList[posSel].lock
        for (i in itemList.indices){
            if(!itemList[i].lock&&itemList[i].number.isNotEmpty()){
                itemList[i].number=""
                posNotifyList.add(i)
            }else if(itemList[i].lock&&!itemSel.lock&&itemList[i]==itemSel&&needNotifySameNumber) {
                posNotifyList.add(i)
            }
        }
        return posNotifyList
    }
}