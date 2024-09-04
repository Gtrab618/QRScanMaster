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
import com.gtrab.qrscanmaster.util.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class CreateQrCodeApp : Fragment() {

    private val disposable = CompositeDisposable()
    private lateinit var binding:FragmentCreateQrCodeAppBinding
    private val appAdapterData by unsafeLazy { AppAdapter{
        barcodeApp(it)}
    }
    private val barcodeFormat by unsafeLazy {
        BarcodeFormat.QR_CODE
    }
    private lateinit var comm :Communicator


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding=FragmentCreateQrCodeAppBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        comm= context as Communicator
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
                    //01 PONER ERROR FIREBASE
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

    private fun barcodeApp(appPackage:String){
        createBarcode(App.fromPackage(appPackage))
    }

    private fun createBarcode(schema: Schema, finish:Boolean= false){
        val barcode= Barcode(
            text = schema.toBarcodeText(),
            formattedText = schema.toFormattedText(),
            format = barcodeFormat,
            schema = schema.schema,
            date = System.currentTimeMillis()
        )

        //02 completar para saber si el usuario quiere guardar los qr o no settings
        comm.passInfoQr(barcode)

    }


}