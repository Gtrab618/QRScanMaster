package com.gtrab.qrscanmaster.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager

import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.RecyclerView
import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.comunication.Communicator
import com.gtrab.qrscanmaster.dependencies.barcodeDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val PAGE_SIZE = 20
private lateinit var comm: Communicator

class History : Fragment() {
    private val scanHistoryAdapter=BarcodeHistoryAdapter{
        comm.passInfoQr(it)
    }
    private lateinit var recycler: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        comm= context as Communicator
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler= view.findViewById(R.id.rcyView)
        initRecycleView()
        loadHistory()
    }

    private fun initRecycleView(){
        recycler.adapter=scanHistoryAdapter
    }

    private fun loadHistory(){

        val pager = Pager(
            config= PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                barcodeDatabase.getAll()

            }
        )
        lifecycleScope.launch {
            try {

                pager.flow
                    .cachedIn(lifecycleScope)
                    .collectLatest {
                        scanHistoryAdapter.submitData(it)
                    }
            }catch (e: Exception){
                println("error history 47 $e")
            }
        }


    }



}