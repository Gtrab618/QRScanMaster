package com.gtrab.qrscanmaster.ui.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.zxing.BarcodeFormat
import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.comunication.Communicator
import com.gtrab.qrscanmaster.databinding.FragmentCreateQrMainBinding
import com.gtrab.qrscanmaster.dependencies.barcodeDatabase
import com.gtrab.qrscanmaster.dependencies.settings
import com.gtrab.qrscanmaster.extension.unsafeLazy
import com.gtrab.qrscanmaster.model.Barcode
import com.gtrab.qrscanmaster.model.schema.App
import com.gtrab.qrscanmaster.model.schema.Schema
import com.gtrab.qrscanmaster.ui.create.qr.AppAdapter
import com.gtrab.qrscanmaster.usecase.saveIfNotPresent
import com.gtrab.qrscanmaster.util.addTo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class FragmentCreateQrMain : Fragment(), AppAdapter.Listener {

    private lateinit var binding:FragmentCreateQrMainBinding

    private lateinit var comm:Communicator
    private val disposable = CompositeDisposable()
    private val barcodeFormat by unsafeLazy {
        BarcodeFormat.QR_CODE
    }

    var  isCreateBarcodeButtonEnabled:Boolean
        get()=false
        set(enable){
            val iconId= if (enable){
                R.drawable.confirm_qr_on
            }else{
                R.drawable.confirm_qr_off
            }
            binding.btnConfirm.setImageResource(iconId)
            binding.btnConfirm.isEnabled=enable
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentCreateQrMainBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null){
            childFragmentManager
                .beginTransaction()
                .replace(R.id.createContainer,CreateOptions())
                .commit()
        }
        comm= context as Communicator
        initHandleButton()
        binding.btnConfirm.isEnabled=false
    }

    override fun onAppClicked(packageName: String) {
        createBarcode(App.fromPackage(packageName))
    }

    private fun initHandleButton(){
        binding.btnConfirm.setOnClickListener {
            createBarcode()
            binding.btnConfirm.isEnabled=false
        }
    }


    private fun createBarcode(){
        val schema = getCurrentFragment().getBarcodeSchema()
        createBarcode(schema)

    }

    private fun createBarcode(schema: Schema,finish:Boolean=false){
        val barcode = Barcode(
            text = schema.toBarcodeText(),
            formattedText = schema.toFormattedText(),
            format = barcodeFormat,
            schema = schema.schema,
            date = System.currentTimeMillis(),

        )

        if(settings.saveCreatedBarcodesToHistory.not()){
            comm.passInfoQr(barcode)
            return
        }

        barcodeDatabase.saveIfNotPresent(barcode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { id ->
                    comm.passInfoQr(barcode.copy(id = id))

                },
                { error ->
                    FirebaseCrashlytics.getInstance().recordException(error)
                    // También puedes agregar un mensaje personalizado
                    FirebaseCrashlytics.getInstance().log("fragmentCreateQrMain save: ${error.message}")

                }
            ).addTo(disposable)

    }



    private fun getCurrentFragment(): CreateQrBase {
        return childFragmentManager.findFragmentById(R.id.createContainer) as CreateQrBase
    }



}