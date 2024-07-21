package com.example.qrscanmaster.ui.infoqr

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.qrscanmaster.R
import com.example.qrscanmaster.dependencies.barcodeImageGenerator
import com.example.qrscanmaster.dependencies.barcodeImageSaved
import com.example.qrscanmaster.extension.unsafeLazy
import com.example.qrscanmaster.model.Barcode
import com.example.qrscanmaster.model.ParsedBarcode
import com.example.qrscanmaster.util.ParmissionRequestFragment
import com.example.qrscanmaster.util.addTo
import com.example.qrscanmaster.util.showSnackbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InfoQr.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoQr : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: Barcode? = null
    // 01 revisar esto para inicializar solo cuando doy click en descargar atravez de lazy
    //private lateinit var drawerLayout: DrawerLayout
    private lateinit var btnEditName: ImageButton
    private lateinit var btnFavorito: ImageButton
    private lateinit var btnDelete: ImageButton
    private lateinit var txtSchemaName:TextView
    private lateinit var txtContent:TextView
    private lateinit var btnCopyPassWifi:Button
    private  var barcode:ParsedBarcode? = null
    private lateinit var imageQr:ImageView
    private lateinit var btnQrSaveImage:Button
    //request Permission storage
    private lateinit var drawerView: View
    private val disposable = CompositeDisposable()
    private val coarsePermission =
        ParmissionRequestFragment(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, onRational = {
            drawerView.showSnackbar("Requere acceso para guardar",Snackbar.LENGTH_INDEFINITE,"Ok"){
                checkPermissionStorage()
            }
        }, onDenied = {

            drawerView.showSnackbar("Redireccion a configuracion proximo",Snackbar.LENGTH_INDEFINITE,"Ok"){

            }
        })
    private val clipboardManager by unsafeLazy {
        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as Barcode?
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_qr, container, false)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment InfoQr.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Barcode) =
            InfoQr().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)

                }
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btnEditName=view.findViewById(R.id.btnEditName)
        btnDelete=view.findViewById(R.id.btnDelete)
        btnFavorito=view.findViewById(R.id.btnFavorite)
        txtSchemaName=view.findViewById(R.id.txtSchemaType)
        txtContent=view.findViewById(R.id.txtContent)
        btnCopyPassWifi=view.findViewById(R.id.btnCopyPassWifi)
        btnQrSaveImage= view.findViewById(R.id.btnSaveQr)
        drawerView= view
        //01 reviar el redibujado desactivado en gradle el qr al girar se hace mas  grande modificar atributos de imageview xml
        imageQr=view.findViewById(R.id.mwQr)
        parseBarcodeInfo()
        initMenuBar()
        handleButtonsClicked()
        showBarcodeImage()

    }

    private fun showBarcodeImage(){
        try {
            // 01 revisar el color y setting creo que es configurable
            val bitmap = param1?.let { barcodeImageGenerator.generateBitmap(it,2000,2000,0, Color.BLACK,Color.WHITE)}
            imageQr.setImageBitmap(bitmap)
        }catch (ex:Exception){

        }
    }

    private fun initMenuBar() {

        txtSchemaName.text= param1?.schema.toString()
        txtContent.text= barcode?.formattedText
        btnEditName.setOnClickListener {
            Toast.makeText(requireContext(), "test", Toast.LENGTH_SHORT).show()
        }

        btnDelete.setOnClickListener {
            Toast.makeText(requireContext(), "Delete", Toast.LENGTH_SHORT).show()
        }

        btnFavorito.setOnClickListener {
            Toast.makeText(requireContext(), "favovito", Toast.LENGTH_SHORT).show()
        }

    }

    private fun handleButtonsClicked(){
        btnCopyPassWifi.setOnClickListener {
            copyNetworkPasswordToClipboard()
        }

        btnQrSaveImage.setOnClickListener{
            checkPermissionStorage()
        }

    }

    private fun parseBarcodeInfo(){
        barcode= param1?.let { ParsedBarcode(it) }
    }

    private fun copyNetworkPasswordToClipboard(){
        copyToClipboard(barcode?.networkPassword.orEmpty())
        snackBar(1)
    }

    private fun copyToClipboard(text:String){
        val clipData=ClipData.newPlainText("",text)
        clipboardManager.setPrimaryClip(clipData)
    }

     private fun snackBar(stringId:Int){
         //01 completar con los string para que sea multilenguaje
         Snackbar.make(requireView(), "Copied to clipboard! \uD83D\uDCCB", Snackbar.LENGTH_LONG).show()

         //personalizado
         /*val snackbar = Snackbar.make(requireView(), "Replace with your own action",
             Snackbar.LENGTH_LONG).setAction("Action", null)
         snackbar.setActionTextColor(Color.BLUE)
         val snackbarView = snackbar.view
         snackbarView.setBackgroundColor(Color.LTGRAY)
         val textView =
             snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
         textView.setTextColor(Color.BLUE)
         textView.textSize = 28f
         snackbar.show()*/
    }



    private fun checkPermissionStorage(){
        coarsePermission.runWithPermission {
            val bottomSheetDialog=BottomSheetDialog(requireContext())
            val bottomSheetView= layoutInflater.inflate(R.layout.bottom_save_qr_options, null)
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()

            val btnOptionSavePng:Button= bottomSheetView.findViewById(R.id.btnPngFormatQr)
            val btnOptionSaveSvg:Button= bottomSheetView.findViewById(R.id.btnSvgFormatQr)

            btnOptionSavePng.setOnClickListener {

                if(param1 != null){
                    //falta almacenar el resultado sino no funciona el complete
                   val saveFun= barcodeImageGenerator.generateBitmapAsync(param1!!,640,640,2)
                       .flatMapCompletable { barcodeImageSaved.savePngImageToPublicDirectory(requireContext(),it,param1!!) }
                    val disposable = saveFun
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { Toast.makeText(requireContext(), "guardado", Toast.LENGTH_SHORT).show() },
                            { Toast.makeText(requireContext(), "error guardar", Toast.LENGTH_SHORT).show() }
                        ).addTo(disposable)

                    // Añade el disposable al CompositeDisposable

                }

            }
        }
    }

}