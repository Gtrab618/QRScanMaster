package com.example.qrscanmaster.ui.home

import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraCharacteristics
import android.os.Bundle
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
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.qrscanmaster.R
import com.example.qrscanmaster.comunication.Communicator
import com.example.qrscanmaster.dependencies.scannerCameraHelper
import com.example.qrscanmaster.dependencies.settingGen
import com.example.qrscanmaster.services.SqliteService
import com.example.qrscanmaster.util.decodeQRCode
import com.google.zxing.Result
import java.io.IOException

class Home : Fragment() {

    private lateinit var comm: Communicator
    //private lateinit var btn:Button
    private lateinit var codeScanner: CodeScanner
    private lateinit var scannerView:CodeScannerView
    private lateinit var btnGalery:ImageButton
    private lateinit var btnCameraFront:ImageButton
    private lateinit var btnFlash:ImageButton
    private lateinit var btnZoomIncrease:ImageButton
    private lateinit var btnZoomDecrease:ImageButton
    private lateinit var skbZoom: AppCompatSeekBar
    private var maxZoom:Float=0f
    private var typeCamera=1
    private var maxZoomFront=0f
    private var maxZoomBack=0f
    private val zoomStep=5


    //capturar imagen para ser guardada y procesada ver si puedo separar en otra clase a futuro
    private val galeryQrResult=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->

        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            //selecciono la uri para procesar la imagen
            val selectedImageUri= result.data?.data
            //let si no esta vacio
            selectedImageUri?.let { uri ->
                try {
                    //leo un stream del archivo
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
                    inputStream?.use { stream ->
                        //convierte el flujo en un bitmap
                        val bitmap = BitmapFactory.decodeStream(stream)
                        val result = decodeQRCode(bitmap)
                        if (result != null) {
                            // Se ha encontrado un código QR, hacer algo con el resultado
                            comm.passInfoQr(result)
                        } else {
                            // No se encontró ningún código QR en la imagen
                            Toast.makeText(requireActivity(), "No se encontró ningún código QR en la imagen", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                    // Manejar cualquier error de E/S
                    Toast.makeText(requireActivity(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            // El usuario canceló o hubo un error
            Toast.makeText(requireActivity(), "Imagen no seleccionada", Toast.LENGTH_SHORT).show()
        }

        /*
        sacar el nombre de la imagen mediante la uri
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
        val data: Intent? = result.data
        val uri: Uri? = data?.data
        // Aquí puedes usar el URI para acceder a la información de la imagen
        // Por ejemplo, si quieres obtener el nombre del archivo:
        val displayName: String? = uri?.let { uri ->
            requireActivity().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            }
        }
        displayName?.let {
            Toast.makeText(requireActivity(), "Imagen seleccionada: $it", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(requireActivity(), "No se pudo obtener el nombre de la imagen", Toast.LENGTH_SHORT).show()
        }
    } else {
        // El usuario canceló o hubo un error
        Toast.makeText(requireActivity(), "Galería cancelada", Toast.LENGTH_SHORT).show()
    }
         */
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewHome= inflater.inflate(R.layout.fragment_home, container, false)

        //importante inicialziar el comunicator
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // al hacer esto lo inicializo con el main por eso llama al main
        comm = requireActivity() as Communicator
        //btn=viewHome.findViewById(R.id.button2)
       /* val txt= viewHome.findViewById<EditText>(R.id.editTextText)
        val enterBtn=viewHome.findViewById<Button>(R.id.button)
        enterBtn.setOnClickListener{
            comm.passInfoQr(txt.text.toString())
        }*/

     /*   btn.setOnClickListener{
            IntentIntegrator.forSupportFragment(this@Home).initiateScan()
        }*/
        return viewHome
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        btnGalery=view.findViewById(R.id.btnGalery)
        btnCameraFront=view.findViewById(R.id.btnCameraFront)
        btnFlash=view.findViewById(R.id.btnFlash)
        btnZoomIncrease=view.findViewById(R.id.btnZoomIn)
        btnZoomDecrease=view.findViewById(R.id.btnZoomOut)
        skbZoom=view.findViewById(R.id.skbZoom)
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

    private fun initScanner(){
        //inicialziar el lector

        codeScanner = CodeScanner(requireActivity(), scannerView).apply {
            //escaneo mas suave en vez de cada rato
            autoFocusMode=AutoFocusMode.CONTINUOUS
            //escanear solo 1 qr
            scanMode=ScanMode.SINGLE
            //se auto ajuste la camara
            isAutoFocusEnabled = true
            //tocar para ajustar
            isTouchFocusEnabled = true
            decodeCallback = DecodeCallback(::handleScannedBarcode)

        }
        //averiguar que hace esto

        // sirve para decodificar directamente el codigo qr
        /*codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
            }
        }*/
        //aberiguar que hace esto
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun initBtnCameraFlashGalery(){
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
                duration=1000
                rotationYBy(360f)
            }
            try {
                //0 camera back 1 camera front  error -1 back -2front codeScanner
                /*codeScanner.camera=*/if(settingGen.isBackCamera){
                    codeScanner.camera=CodeScanner.CAMERA_FRONT
                    settingGen.isBackCamera=false
                    maxZoom=maxZoomFront
                    skbZoom.max=maxZoom.toInt()
                    if(codeScanner.isFlashEnabled){
                        btnFlash.setImageResource(R.drawable.flash_off)
                    }
                }else{
                    codeScanner.camera=CodeScanner.CAMERA_BACK
                    settingGen.isBackCamera=true
                    maxZoom=maxZoomBack
                    skbZoom.max=maxZoom.toInt()
                }
                resetZoom()
            }catch (e:IOException){
                Toast.makeText(requireContext(), "Error Camara cod=0", Toast.LENGTH_SHORT).show()
            }

        }


        btnFlash.setOnClickListener {
            codeScanner.isFlashEnabled=codeScanner.isFlashEnabled.not()
            // mejorar animated1
            it.animate().apply {
                duration=980
                rotationYBy(360f)
            }.withEndAction {
                if(codeScanner.isFlashEnabled){
                    btnFlash.setImageResource(R.drawable.flash_on)
                }else{
                    btnFlash.setImageResource(R.drawable.flash_off)
                }
            }

        }


    }

    private fun initZoomSeekBar(){
        val admin = SqliteService(requireContext(),"qrDataBase",null,1)
        val dataBase= admin.writableDatabase
        dataBase.use {
            val filas= dataBase.rawQuery("SELECT percentZoom,typeCamera FROM zoom",null)
            filas.use {
                //data zoom found
                if (filas.moveToFirst()){

                    do {
                        typeCamera=filas.getString(1).toInt()

                        //1 camera back 0 camera front
                        if(typeCamera==1){
                            maxZoomBack=filas.getString(0).toFloat()
                        }else {
                            maxZoomFront=filas.getString(0).toFloat()
                        }

                    }while (filas.moveToNext())
                    maxZoom=maxZoomBack
                    skbZoom.max=maxZoom.toInt()

                    //data zoom not found
                }else{

                    val infoBack=scannerCameraHelper.getBackCameraProperties(settingGen.isBackCamera,requireContext())?.apply {
                        val maxZoom=get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)

                        if(maxZoom!=null){

                            maxZoomBack=maxZoom
                            maxZoomBack*=6
                            val insertBack= ContentValues()
                            insertBack.put("percentZoom",maxZoomBack)
                            insertBack.put("typeCamera",1)
                            dataBase.insert("zoom",null,insertBack)
                            this@Home.maxZoom=maxZoomBack
                            skbZoom.max=maxZoomBack.toInt()
                        }

                    }

                    val infoFront=scannerCameraHelper.getBackCameraProperties(!settingGen.isBackCamera,requireContext())?.apply {
                        val maxZoom=get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)

                        if(maxZoom!=null){
                            maxZoomFront=maxZoom
                            maxZoomFront*=6
                            val insertFront=ContentValues()
                            insertFront.put("percentZoom",maxZoomFront)
                            insertFront.put("typeCamera",0)
                            dataBase.insert("zoom",null,insertFront)
                        }

                    }

                }
            }
        }


    }


    private fun handleZoomChanged(){
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
    private fun initBtnZoom(){
        btnZoomIncrease.setOnClickListener {
            increaseZoom()
        }
        btnZoomDecrease.setOnClickListener {
            decreaseZoom()
        }
    }
    //zoom
    private fun increaseZoom(){
        codeScanner.apply {
            if(zoom<maxZoom-zoomStep){
                zoom +=zoomStep

            }else{
                zoom=maxZoom.toInt()

            }
            skbZoom.progress=zoom
        }
    }

    private fun decreaseZoom(){
        codeScanner.apply {
            if(zoom>zoomStep){
                zoom-=zoomStep
            }else{
                zoom=0

            }
            skbZoom.progress=zoom
        }
    }

    private fun resetZoom(){
        codeScanner.apply {
            zoom=0
            skbZoom.progress=0
        }
    }

    private fun handleScannedBarcode(result: Result){

        comm.passInfoQr(result)
    }

}