package com.zaxai.sudokugame.ui

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.zaxai.sudokugame.MainActivity
import com.zaxai.sudokugame.R
import com.zaxai.sudokugame.link.SudokuViewModel
import com.zaxai.sudokugame.logic.Sudoku
import com.zaxai.sudokugame.logic.SudokuDao

class SudokuFragment:Fragment() {
    val viewModel by lazy { ViewModelProvider(this).get(SudokuViewModel::class.java) }
    private lateinit var adapter: SudokuAdapter
    private lateinit var launcher:ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sudoku,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar:Toolbar=view.findViewById(R.id.toolbar)
        val sudokuRecyclerView:RecyclerView=view.findViewById(R.id.sudokuRecyclerView)
        val calcProgress:ProgressBar=view.findViewById(R.id.calcProgress)
        val calcCountText:TextView=view.findViewById(R.id.calcCountText)
        val backupBtn:Button=view.findViewById(R.id.backupBtn)
        val restoreBtn:Button=view.findViewById(R.id.restoreBtn)
        val emptyBtn:Button=view.findViewById(R.id.emptyBtn)
        val clearBtn:Button=view.findViewById(R.id.clearBtn)
        val lockBtn:Button=view.findViewById(R.id.lockBtn)
        val unlockBtn:Button=view.findViewById(R.id.unlockBtn)
        val calcBtn:FloatingActionButton=view.findViewById(R.id.calcBtn)
        setHasOptionsMenu(true)
        (activity as MainActivity).setSupportActionBar(toolbar)
        adapter= SudokuAdapter(this,viewModel.sudoku)
        adapter.setOnRecyclerItemClickListener {position,rect ->
            if(!viewModel.isSudokuItemLocked(position)) {
                NumKeyboardDialogFragment(sudokuRecyclerView.width / 3, Point(rect.right, rect.bottom))
                    .setOnItemClickListener { number ->
                        viewModel.setSudokuItem(position, number.toString()) {
                            for (i in it) {
                                adapter.notifyItemChanged(i)
                            }
                        }
                    }.show(parentFragmentManager)
            }
        }
        adapter.setOnRecyclerItemLongClickListener {
            when(viewModel.getLongClickOperation(requireContext())){
                SudokuDao.LongClickOperation.LONG_CLICK_EMPTY->{
                    empty()
                }
                SudokuDao.LongClickOperation.LONG_CLICK_CLEAR->{
                    clear(view)
                }
            }
            true
        }
        sudokuRecyclerView.layoutManager=GridLayoutManager(context,9)
        sudokuRecyclerView.adapter=adapter
        sudokuRecyclerView.addItemDecoration(GridDividerItemDecoration(requireContext()))
        if(savedInstanceState==null) {
            viewModel.syncSudokuItemList(requireContext())
            viewModel.syncSudokuNotifySameNumberFlag(requireContext())
            adapter.notifyItemRangeChanged(0, Sudoku.SUDOKU_TOTAL_SIZE)
        }
        launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(viewModel.checkHighlightFlagChanged(requireContext())){
                viewModel.syncSudokuNotifySameNumberFlag(requireContext())
                for (i in viewModel.getSudokuSameNumberPosList()){
                    adapter.notifyItemChanged(i)
                }
            }
            if(viewModel.isCalculating){
                if (viewModel.isCalcCountDisplayed(requireContext())) {
                    calcCountText.visibility = View.VISIBLE
                } else {
                    calcCountText.visibility = View.INVISIBLE
                }
            }
        }
        viewModel.calcCount.observe(viewLifecycleOwner){
            if(viewModel.isCalcCountDisplayed(requireContext())) {
                calcCountText.text = it.toString()
            }
        }
        viewModel.calcResult.observe(viewLifecycleOwner){
            if(viewModel.isCalculating) {
                if (it) {
                    viewModel.endAutoCalc()
                    adapter.notifyItemRangeChanged(0, Sudoku.SUDOKU_TOTAL_SIZE)
                }
                setViewOnEndCalc(view)
                viewModel.isCalculating = false
            }
        }
        backupBtn.setOnClickListener {
            viewModel.saveSudokuItemList(requireContext())
            Toast.makeText(context,"已标记起始",Toast.LENGTH_SHORT).show()
        }
        restoreBtn.setOnClickListener {
            viewModel.syncSudokuItemList(requireContext())
            adapter.notifyItemRangeChanged(0, Sudoku.SUDOKU_TOTAL_SIZE)
            Toast.makeText(context,"已恢复起始",Toast.LENGTH_SHORT).show()
        }
        emptyBtn.setOnClickListener {
            empty()
        }
        clearBtn.setOnClickListener {
            clear(view)
        }
        lockBtn.setOnClickListener {
            viewModel.sudokuItemLock(){
                for (i in it) {
                    adapter.notifyItemChanged(i)
                }
            }
        }
        unlockBtn.setOnClickListener {
            viewModel.sudokuItemUnlock(){
                for (i in it) {
                    adapter.notifyItemChanged(i)
                }
            }
        }
        calcBtn.setOnClickListener {
            if(!viewModel.isCalculating) {
                if (viewModel.startAutoCalc()) {
                    setViewOnStartCalc(view)
                    viewModel.isCalculating=true
                } else {
                    Snackbar.make(view,"单行、单列或九宫格存在重复数字",Snackbar.LENGTH_SHORT).show()
                }
            }else{
                viewModel.stopAutoCalc()
                Snackbar.make(view, "已停止计算", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(viewModel.isCalculating) {
            viewModel.isCalculating = false
            viewModel.stopAutoCalc()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.helpBtn->{
                val intent=Intent(context,IntroduceActivity::class.java)
                startActivity(intent)
            }
            R.id.photoBtn->{
                Toast.makeText(context,"拍照识别功能正在开发中",Toast.LENGTH_SHORT).show()
            }
            R.id.albumBtn->{
                Toast.makeText(context,"相册导入功能正在开发中",Toast.LENGTH_SHORT).show()
            }
            R.id.settingsBtn->{
                val intent=Intent(context,SettingsActivity::class.java)
                launcher.launch(intent)
            }
            R.id.aboutBtn->{
                val intent=Intent(context,AboutActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    private fun empty(){
        viewModel.sudokuItemSelEmpty(){
            for (i in it) {
                adapter.notifyItemChanged(i)
            }
        }
    }

    private fun clear(view: View){
        viewModel.sudokuItemClear(){posNotifyList->
            for (i in posNotifyList) {
                adapter.notifyItemChanged(i)
            }
            if(posNotifyList.isNotEmpty()) {
                Snackbar.make(view, "已清空", Snackbar.LENGTH_SHORT).setAction("撤销") {
                    viewModel.sudokuItemRecovery()
                    for (i in posNotifyList) {
                        adapter.notifyItemChanged(i)
                    }
                }.show()
            }
        }
    }

    private fun setViewOnStartCalc(view: View){
        val calcProgress:ProgressBar=view.findViewById(R.id.calcProgress)
        val calcCountText:TextView=view.findViewById(R.id.calcCountText)
        val calcBtn:FloatingActionButton=view.findViewById(R.id.calcBtn)
        if(viewModel.isCalcCountDisplayed(requireContext())) {
            calcCountText.visibility=View.VISIBLE
        }
        calcProgress.visibility=View.VISIBLE
        calcBtn.setImageResource(R.drawable.ic_stop)
    }

    private fun setViewOnEndCalc(view:View){
        val calcProgress:ProgressBar=view.findViewById(R.id.calcProgress)
        val calcCountText:TextView=view.findViewById(R.id.calcCountText)
        val calcBtn:FloatingActionButton=view.findViewById(R.id.calcBtn)
        if (viewModel.isCalcCountDisplayed(requireContext())) {
            calcCountText.visibility = View.INVISIBLE
        }
        calcProgress.visibility = View.INVISIBLE
        calcBtn.setImageResource(R.drawable.ic_calc)
    }
}