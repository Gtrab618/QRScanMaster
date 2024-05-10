package com.example.qrscanmaster.ui.home

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.qrscanmaster.R
import com.example.qrscanmaster.comunication.Communicator
import com.example.qrscanmaster.dependencies.settingGen
import com.example.qrscanmaster.usecase.SettingsGeneral
import com.example.qrscanmaster.util.decodeQRCode
import java.io.File
import java.io.FileInputStream
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Home : Fragment() {

    //private lateinit var comm: Communicator
    //private lateinit var btn:Button
    private lateinit var codeScanner: CodeScanner
    private lateinit var scannerView:CodeScannerView
    private lateinit var btnGalery:ImageButton
    private lateinit var btnCameraFront:ImageButton
    private lateinit var btnFlash:ImageButton
    private lateinit var btnZoomIncrease:ImageButton
    private lateinit var btnZoomDecrease:ImageButton
    private var maxZoom:Int=0
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
                            Toast.makeText(requireActivity(), "Contenido del QR: $result", Toast.LENGTH_LONG).show()
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
        btnGalery=viewHome.findViewById(R.id.btnGalery)
        btnCameraFront=viewHome.findViewById(R.id.btnCameraFront)
        btnFlash=viewHome.findViewById(R.id.btnFlash)
        btnZoomIncrease=viewHome.findViewById(R.id.btnZoomIn)
        initBtnCameraFlashGalery()
        initZoomSeekBar()
        initBtnZoom()
        //comm = requireActivity() as Communicator
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

        }

        //que se hace con el codigo
        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
            }
        }
        //aberiguar que hace esto
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun initBtnCameraFlashGalery(){
        btnGalery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.resolveActivity(requireActivity().packageManager)?.also {
                galeryQrResult.launch(intent)
            }

        }
        btnCameraFront.setOnClickListener {

            val bande=codeScanner.camera==CodeScanner.CAMERA_BACK

            //0 camera back 1 camera front  error -1 back -2front codeScanner
            /*codeScanner.camera=*/if(settingGen.isBackCamera){
            codeScanner.camera=CodeScanner.CAMERA_FRONT
            settingGen.isBackCamera=false
            }else{
            codeScanner.camera=CodeScanner.CAMERA_BACK
            settingGen.isBackCamera=true
            }

        }


        btnFlash.setOnClickListener {
            codeScanner.isFlashEnabled=codeScanner.isFlashEnabled.not()
        }


    }

    private fun initZoomSeekBar(){

    }

    private fun initBtnZoom(){
        btnZoomIncrease.setOnClickListener {
            increaseZoom()
        }
    }
    //zoom
    private fun increaseZoom(){
        Toast.makeText(requireActivity(), "zoom", Toast.LENGTH_SHORT).show()
        codeScanner.apply {
            if(zoom<maxZoom-zoomStep){
                zoom +=zoomStep
                Toast.makeText(requireActivity(), "min", Toast.LENGTH_SHORT).show()
            }else{
                zoom=maxZoom
                Toast.makeText(requireActivity(), "max", Toast.LENGTH_SHORT).show()
            }

        }
    }


}