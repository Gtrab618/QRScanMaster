package com.gtrab.qrscanmaster.ui.home

import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraCharacteristics
import android.os.Bundle
import android.os.VibrationEffect
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.comunication.Communicator
import com.gtrab.qrscanmaster.dependencies.barcodeDatabase
import com.gtrab.qrscanmaster.dependencies.barcodeParser
import com.gtrab.qrscanmaster.dependencies.scannerCameraHelper
import com.gtrab.qrscanmaster.dependencies.settingGen
import com.gtrab.qrscanmaster.services.SqliteService
import com.gtrab.qrscanmaster.usecase.saveIfNotPresent
import com.google.zxing.Result
import com.gtrab.qrscanmaster.dependencies.settings
import com.gtrab.qrscanmaster.extension.plusVibrator.vibrateOnce
import com.gtrab.qrscanmaster.extension.unsafeLazy
import com.gtrab.qrscanmaster.extension.vibrator
import com.gtrab.qrscanmaster.model.Barcode
import com.gtrab.qrscanmaster.ui.dialogs.ConfirmDialogFragment
import com.gtrab.qrscanmaster.usecase.BarcodeParse
import com.gtrab.qrscanmaster.util.addTo
import com.gtrab.qrscanmaster.util.decodeQRCode
import com.gtrab.qrscanmaster.util.getFormatString
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.IOException

class Home : Fragment(), ConfirmDialogFragment.ConfirmDialogListener {

    private val vibrationPattern = arrayOf<Long>(0, 350).toLongArray()
    private lateinit var comm: Communicator
    private val disposable = CompositeDisposable()
    private lateinit var temBarcodeManual: Barcode
    private lateinit var codeScanner: CodeScanner
    private lateinit var scannerView: CodeScannerView
    private lateinit var btnGalery: ImageButton
    private lateinit var btnCameraFront: ImageButton
    private lateinit var btnFlash: ImageButton
    private lateinit var btnZoomIncrease: ImageButton
    private lateinit var btnZoomDecrease: ImageButton
    private lateinit var skbZoom: AppCompatSeekBar
    private var lastResult: Barcode? = null
    private var maxZoom: Float = 0f
    private var typeCamera = 1
    private var maxZoomFront = 0f
    private var maxZoomBack = 0f
    private val zoomStep = 5


    //capturar imagen para ser guardada y procesada ver si puedo separar en otra clase a futuro
    private val galeryQrResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                //selecciono la uri para procesar la imagen
                val selectedImageUri = result.data?.data
                //let si no esta vacio


                selectedImageUri?.let { uri ->
                    try {
                        //leo un stream del archivo
                        val inputStream = requireActivity().contentResolver.openInputStream(uri)
                        inputStream?.use { stream ->
                            //convierte el flujo en un bitmap
                            val bitmap = BitmapFactory.decodeStream(stream)
                            val resultQr = decodeQRCode(bitmap)
                            if (resultQr != null) {
                                // Se ha encontrado un código QR, hacer algo con el resultado
                                handleScannedBarcode(resultQr)
                            } else {
                                // revisar si es un qr personalizado demora mas !!!!!!!!!!!!!!!!!!!!!
                                //api de google para qr personalizados
                                val image: InputImage?
                                try {
                                    image = selectedImageUri.let {
                                        InputImage.fromFilePath(
                                            requireContext(),
                                            it
                                        )
                                    }

                                    val scanner = BarcodeScanning.getClient()
                                    val result =
                                        scanner.process(image).addOnSuccessListener { barcodes ->

                                            if (barcodes.size > 0) {
                                                val qrcode = barcodes[0]
                                                val resultQr = Result(
                                                    qrcode.rawValue, // El texto del código QR
                                                    qrcode.rawBytes, // Los datos en bytes del código QR
                                                    null, // Los puntos del resultado
                                                    qrcode.getFormatString(qrcode.format), // El formato del código QR
                                                    System.currentTimeMillis() // El timestamp
                                                )
                                                handleScannedBarcode(resultQr)
                                            } else {
                                                Toast.makeText(
                                                    requireContext(),
                                                    R.string.home_notfound_qr,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                        }.addOnFailureListener {
                                            Toast.makeText(
                                                requireContext(),
                                                R.string.home_notfound_qr,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                } catch (e: IOException) {
                                    //error leer qr personalizado api google
                                    FirebaseCrashlytics.getInstance().recordException(e)
                                    FirebaseCrashlytics.getInstance().log("home 140: ${e.message}")


                                }
                            }
                        }

                    } catch (e: IOException) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        FirebaseCrashlytics.getInstance().log("fragmentCreateQrMain save: ${e.message}")


                        // Manejar cualquier error de E/S
                        Toast.makeText(
                            requireActivity(),
                            R.string.home_error_uri,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } else {
                // El usuario canceló o hubo un error
                Toast.makeText(requireActivity(), R.string.home_notselect_qr, Toast.LENGTH_SHORT)
                    .show()
            }


        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewHome = inflater.inflate(R.layout.fragment_home, container, false)
        comm = context as Communicator

        return viewHome
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        btnGalery = view.findViewById(R.id.btnGalery)
        btnCameraFront = view.findViewById(R.id.btnCameraFront)
        btnFlash = view.findViewById(R.id.btnFlash)
        btnZoomIncrease = view.findViewById(R.id.btnZoomIn)
        btnZoomDecrease = view.findViewById(R.id.btnZoomOut)
        skbZoom = view.findViewById(R.id.skbZoom)
        initBtnCameraFlashGalery()
        initZoomSeekBar()
        initBtnZoom()
        handleZoomChanged()
        initScanner()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun initScanner() {
        //inicialziar el lector

        codeScanner = CodeScanner(requireActivity(), scannerView).apply {
            //escaneo mas suave en vez de cada rato
            autoFocusMode = AutoFocusMode.CONTINUOUS
            //escanear solo 1 qr
            scanMode = ScanMode.SINGLE
            //se auto ajuste la camara
            isAutoFocusEnabled = true
            //tocar para ajustar
            isTouchFocusEnabled = true
            decodeCallback = DecodeCallback(::handleScannedBarcode)

        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun initBtnCameraFlashGalery() {
        btnGalery.setOnClickListener {
            it.animate().apply {
                duration = 1000
                scaleX(1.5f)  // Cambia el tamaño horizontalmente
                scaleY(1.5f)
            }.withEndAction {
                it.animate().apply {
                    duration = 1000
                    scaleX(1f)  // Cambia el tamaño horizontalmente
                    scaleY(1f)
                }
            }
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.resolveActivity(requireActivity().packageManager)?.also {
                galeryQrResult.launch(intent)
            }

        }
        btnCameraFront.setOnClickListener {
            // mejorar animated1
            it.animate().apply {
                duration = 1000
                rotationYBy(360f)
            }
            try {
                //0 camera back 1 camera front  error -1 back -2front codeScanner
                /*codeScanner.camera=*/if (settingGen.isBackCamera) {
                    codeScanner.camera = CodeScanner.CAMERA_FRONT
                    settingGen.isBackCamera = false
                    maxZoom = maxZoomFront
                    skbZoom.max = maxZoom.toInt()
                    if (codeScanner.isFlashEnabled) {
                        btnFlash.setImageResource(R.drawable.flash_off)
                    }
                } else {
                    codeScanner.camera = CodeScanner.CAMERA_BACK
                    settingGen.isBackCamera = true
                    maxZoom = maxZoomBack
                    skbZoom.max = maxZoom.toInt()
                }
                resetZoom()
            } catch (e: IOException) {

                FirebaseCrashlytics.getInstance().recordException(e)

                FirebaseCrashlytics.getInstance().log("camara front o back 298: ${e.message}")
            }

        }


        btnFlash.setOnClickListener {
            codeScanner.isFlashEnabled = codeScanner.isFlashEnabled.not()
            // mejorar animated1
            it.animate().apply {
                duration = 980
                rotationYBy(360f)
            }.withEndAction {
                if (codeScanner.isFlashEnabled) {
                    btnFlash.setImageResource(R.drawable.flash_on)
                } else {
                    btnFlash.setImageResource(R.drawable.flash_off)
                }
            }

        }


    }

    private fun initZoomSeekBar() {
        val admin = SqliteService(requireContext(), "qr_serv_init", null, 1)
        val dataBase = admin.writableDatabase
        dataBase.use {
            val filas = dataBase.rawQuery("SELECT percentZoom,typeCamera FROM zoom", null)
            filas.use {
                //data zoom found
                if (filas.moveToFirst()) {

                    do {
                        typeCamera = filas.getString(1).toInt()

                        //1 camera back 0 camera front
                        if (typeCamera == 1) {
                            maxZoomBack = filas.getString(0).toFloat()
                        } else {
                            maxZoomFront = filas.getString(0).toFloat()
                        }

                    } while (filas.moveToNext())
                    maxZoom = maxZoomBack
                    skbZoom.max = maxZoom.toInt()

                    //data zoom not found
                } else {

                    val infoBack = scannerCameraHelper.getBackCameraProperties(
                        settingGen.isBackCamera,
                        requireContext()
                    )?.apply {
                        val maxZoom = get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)

                        if (maxZoom != null) {

                            maxZoomBack = maxZoom
                            maxZoomBack *= 6
                            val insertBack = ContentValues()
                            insertBack.put("percentZoom", maxZoomBack)
                            insertBack.put("typeCamera", 1)
                            dataBase.insert("zoom", null, insertBack)
                            this@Home.maxZoom = maxZoomBack
                            skbZoom.max = maxZoomBack.toInt()
                        }

                    }

                    val infoFront = scannerCameraHelper.getBackCameraProperties(
                        !settingGen.isBackCamera,
                        requireContext()
                    )?.apply {
                        val maxZoom = get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)

                        if (maxZoom != null) {
                            maxZoomFront = maxZoom
                            maxZoomFront *= 6
                            val insertFront = ContentValues()
                            insertFront.put("percentZoom", maxZoomFront)
                            insertFront.put("typeCamera", 0)
                            dataBase.insert("zoom", null, insertFront)
                        }

                    }

                }
            }
        }


    }


    private fun handleZoomChanged() {
        skbZoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    codeScanner.zoom = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    private fun initBtnZoom() {
        btnZoomIncrease.setOnClickListener {
            increaseZoom()
        }
        btnZoomDecrease.setOnClickListener {
            decreaseZoom()
        }
    }

    //zoom
    private fun increaseZoom() {
        codeScanner.apply {
            if (zoom < maxZoom - zoomStep) {
                zoom += zoomStep

            } else {
                zoom = maxZoom.toInt()

            }
            skbZoom.progress = zoom
        }
    }

    private fun decreaseZoom() {
        codeScanner.apply {
            if (zoom > zoomStep) {
                zoom -= zoomStep
            } else {
                zoom = 0

            }
            skbZoom.progress = zoom
        }
    }

    private fun resetZoom() {
        codeScanner.apply {
            zoom = 0
            skbZoom.progress = 0
        }
    }

    private fun handleScannedBarcode(result: Result) {

        vibrateIfNeeded()
        val barcode = barcodeParser.parseResult(result)


        when {
            settings.confirmScansManually ->  showScanConfirmationDialog(barcode)
            settings.saveScanBarcodeToHistory -> saveScannedBarcodeScreen(barcode)
            else -> comm.passInfoQr(barcode)
        }

    }

    private fun vibrateIfNeeded() {
        if (settings.vibrate) {

            requireActivity().apply {
                runOnUiThread {
                    applicationContext.vibrator?.vibrateOnce(vibrationPattern)
                }
            }

        }
    }

    private fun showScanConfirmationDialog(barcode: Barcode) {
        temBarcodeManual = barcode
        //01 para la version ingles añadir lo local
        val dialog = ConfirmDialogFragment.newInstance("Scanner","¿Ver este qr? ${barcode.schema}")
        dialog.show(childFragmentManager, "")

    }

    override fun onDialogResult(result: Boolean) {
        try {
            if (result) {
                saveScannedBarcodeScreen(temBarcodeManual)

            } else {
               requireActivity().runOnUiThread {
                   codeScanner.startPreview()
               }
            }
        } catch (e: Exception) {

            FirebaseCrashlytics.getInstance().recordException(e)
            // También puedes agregar un mensaje personalizado
            FirebaseCrashlytics.getInstance().log("ErrorDialogManual510 home: ${e.message}")

        }

    }

    private fun saveScannedBarcodeScreen(barcode: Barcode) {

        barcodeDatabase.saveIfNotPresent(barcode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { id ->
                    //01 revisar para que es lastResult
                    lastResult = barcode
                    //revisar esto 02
                    comm.passInfoQr(barcode)
                    //realizar pruebas de carga recicleview
                    /* if(bandera>0){ 03
                         barcode.text=bandera.toString()
                         saveScannedBarcodeScreen(barcode)
                         bandera--
                     }*/
                },
                {error ->
                    FirebaseCrashlytics.getInstance().recordException(error)
                    FirebaseCrashlytics.getInstance().log("Home 543: ${error.message}")
                }
            ).addTo(disposable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }
}