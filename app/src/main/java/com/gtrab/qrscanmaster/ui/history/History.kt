package com.gtrab.qrscanmaster.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager

import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.comunication.Communicator
import com.gtrab.qrscanmaster.dependencies.barcodeDatabase
import com.gtrab.qrscanmaster.dependencies.barcodeImageSaved
import com.gtrab.qrscanmaster.ui.dialogs.ConfirmDialogFragment
import com.gtrab.qrscanmaster.util.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private const val PAGE_SIZE = 20

class History : Fragment(), ConfirmDialogFragment.ConfirmDialogListener {
    private var job: Job? = null
    private lateinit var btnDeleteAll: ImageButton
    private lateinit var btnShowFavorite: ImageButton
    private lateinit var comm: Communicator
    private var showFavoriteBool: Boolean = false
    private val disposable = CompositeDisposable()

    private val scanHistoryAdapter = BarcodeHistoryAdapter {
        comm.passInfoQr(it)
    }
    private lateinit var recycler: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        comm = context as Communicator
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler = view.findViewById(R.id.rcyView)
        btnDeleteAll = view.findViewById(R.id.btnDeleteAll)
        btnShowFavorite = view.findViewById(R.id.btnShowFavorite)
        initRecycleView()
        loadHistory()
        handleButtonCliked()
    }

    private fun initRecycleView() {
        recycler.adapter = scanHistoryAdapter
    }

    private fun loadHistory() {

        val pager = Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                if (!showFavoriteBool) {
                    barcodeDatabase.getAll()
                } else {
                    barcodeDatabase.getFavorites()
                }

            }
        )
        job=lifecycleScope.launch {
            try {

                pager.flow
                    .cachedIn(lifecycleScope)
                    .collectLatest {
                        scanHistoryAdapter.submitData(it)
                    }
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                // También puedes agregar un mensaje personalizado
                FirebaseCrashlytics.getInstance().log("error history 47: ${e.message}")

            }
        }


    }


    private fun handleButtonCliked() {
        btnDeleteAll.setOnClickListener {
            showDeleteConfirmtaionDialog()

        }

        btnShowFavorite.setOnClickListener {
            switchFavorite()
        }
    }

    private fun switchFavorite(){
        showFavoriteBool = !showFavoriteBool
        if (showFavoriteBool){
            btnShowFavorite.setImageResource(R.drawable.favorite_history_on)
        }else{
            btnShowFavorite.setImageResource(R.drawable.favorite_history_off)
        }
        loadHistory()
    }
    private fun showDeleteConfirmtaionDialog() {
        val title = getString(R.string.hs_dlg_title)
        val message = getString(R.string.hs_dlg_message)

        val dialog = ConfirmDialogFragment.newInstance(
            title,
            message
        )
        dialog.show(childFragmentManager, "")
    }

    override fun onDialogResult(result: Boolean) {
        if (result){
            clearHistory()
        }
    }

    private fun clearHistory() {
        barcodeDatabase.deleteAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, { error ->
                FirebaseCrashlytics.getInstance().recordException(error)
                // También puedes agregar un mensaje personalizado
                FirebaseCrashlytics.getInstance().log("history 136: ${error.message}")

            }).addTo(disposable)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
        disposable.clear()
    }
}