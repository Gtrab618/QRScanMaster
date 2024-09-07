package com.gtrab.qrscanmaster.ui.create.qr

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.zxing.BarcodeFormat
import com.gtrab.qrscanmaster.comunication.Communicator
import com.gtrab.qrscanmaster.databinding.FragmentCreateQrCodeAppBinding
import com.gtrab.qrscanmaster.extension.unsafeLazy
import com.gtrab.qrscanmaster.model.Barcode
import com.gtrab.qrscanmaster.model.schema.App
import com.gtrab.qrscanmaster.model.schema.Schema
import com.gtrab.qrscanmaster.ui.create.CreateQrBase
import com.gtrab.qrscanmaster.ui.create.FragmentCreateQrMain
import com.gtrab.qrscanmaster.util.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class CreateQrCodeApp : CreateQrBase() {

    private val disposable = CompositeDisposable()
    private lateinit var binding:FragmentCreateQrCodeAppBinding
    private val appAdapterData by unsafeLazy { AppAdapter(parentFragmen) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding=FragmentCreateQrCodeAppBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        loadApps()

    }

    private fun loadApps(){

        showLoading(true)
        Single.just(getApps())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {apps ->

                    showLoading(false)
                    showApps(apps)

                },
                { error ->

                    showLoading(false)
                    FirebaseCrashlytics.getInstance().log("Error 55 (CreateQrCodeApp function)")
                    FirebaseCrashlytics.getInstance().recordException(error)

                }
            ).addTo(disposable)
    }

    private fun getApps(): List<ResolveInfo> {
        val mainIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return requireContext().packageManager.queryIntentActivities(mainIntent, 0).filter { it.activityInfo?.packageName != null }
    }

    private fun showLoading(isLoading:Boolean){
        binding.progeBar.isVisible=isLoading
        binding.recyclerViewApps.isVisible=isLoading.not()

    }

   private fun initRecyclerView(){
      binding.recyclerViewApps.apply {
          layoutManager = LinearLayoutManager(requireContext())
          adapter = appAdapterData
      }
   }
    private fun showApps(apps: List<ResolveInfo>) {
        appAdapterData.apps = apps
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }



}