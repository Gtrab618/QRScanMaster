package com.gtrab.qrscanmaster.ui.infoqr

import android.Manifest
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.gtrab.qrscanmaster.dependencies.barcodeDatabase
import com.gtrab.qrscanmaster.dependencies.barcodeImageGenerator
import com.gtrab.qrscanmaster.dependencies.barcodeImageSaved
import com.gtrab.qrscanmaster.extension.unsafeLazy
import com.gtrab.qrscanmaster.model.Barcode
import com.gtrab.qrscanmaster.model.ParsedBarcode
import com.gtrab.qrscanmaster.util.ParmissionRequestFragment
import com.gtrab.qrscanmaster.util.addTo
import com.gtrab.qrscanmaster.util.showSnackbar
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gtrab.qrscanmaster.R
import com.gtrab.qrscanmaster.dependencies.settings
import com.gtrab.qrscanmaster.model.schema.BarcodeSchema
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Locale

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

    private val dateFormatter= SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH)
    // 01 revisar esto para inicializar solo cuando doy click en descargar atravez de lazy
    //private lateinit var drawerLayout: DrawerLayout
    private lateinit var adView:AdView
    private lateinit var btnEditName: ImageButton
    private lateinit var btnFavorito: ImageButton
    private lateinit var btnDelete: ImageButton
    private lateinit var txtSchemaName: TextView
    private lateinit var txtContent: TextView
    private lateinit var txtNameQr: TextView
    private lateinit var txtDate: TextView
    private lateinit var btnCopyPassWifi: Button
    private lateinit var btnUrl:Button
    private lateinit var btnPlayStore:Button
    private lateinit var btnSendSms:Button
    private lateinit var btnLookLocation:Button
    private lateinit var btnSearchFlight:Button
    private lateinit var btnAddContact:Button
    private lateinit var btnSendEmail:Button


    private var barcodeParsed: ParsedBarcode? = null
    private lateinit var imageQr: ImageView
    private lateinit var btnQrSaveImage: Button

    //request Permission storage
    private lateinit var drawerView: View
    private val disposable = CompositeDisposable()
    private val coarsePermission =
        ParmissionRequestFragment(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, onRational = {
            drawerView.showSnackbar(
                "Requere acceso para guardar",
                Snackbar.LENGTH_INDEFINITE,
                "Ok"
            ) {
                checkPermissionStorage()
            }
        }, onDenied = {

            drawerView.showSnackbar(
                "Redireccion a configuracion proximo",
                Snackbar.LENGTH_INDEFINITE,
                "Ok"
            ) {

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
        MobileAds.initialize(requireContext()){}
        adView= view.findViewById(R.id.adView)
        //02 completar la carga es decir que carge el name si el barcode ya a sido guardado no solo al editar
        btnEditName = view.findViewById(R.id.btnEditName)
        btnDelete = view.findViewById(R.id.btnDelete)
        btnFavorito = view.findViewById(R.id.btnFavorite)
        btnUrl= view.findViewById(R.id.btnOpenUrl)
        txtSchemaName = view.findViewById(R.id.txtSchemaType)
        txtContent = view.findViewById(R.id.txtContent)
        txtNameQr = view.findViewById(R.id.txtNameQr)
        txtDate= view.findViewById(R.id.txtDate)
        btnCopyPassWifi = view.findViewById(R.id.btnCopyPassWifi)
        btnQrSaveImage = view.findViewById(R.id.btnSaveQr)
        btnPlayStore= view.findViewById(R.id.btnPlayStore)
        btnSendSms=view.findViewById(R.id.btnSendSms)
        btnLookLocation= view.findViewById(R.id.btnLookLocation)
        btnSearchFlight= view.findViewById(R.id.btnSearchFlight)
        btnAddContact= view.findViewById(R.id.btnAddContact)
        btnSendEmail=view.findViewById(R.id.btnSendEmail)
        drawerView = view
        imageQr = view.findViewById(R.id.mwQr)
        parseBarcodeInfo()
        initMenuBar()
        handleButtonsClicked()
        showBarcodeImage()
        showDataBarcode()
        showOrHideButtons()
        applySetting()
        //01 temporal anuncios
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
    //precargar datos para iniciar la interfaz
    private fun parseBarcodeInfo() {
        barcodeParsed = param1?.let { ParsedBarcode(it) }
    }

    private fun showBarcodeImage() {
        try {
            // 01 revisar el color y setting creo que es configurable
            val bitmap = param1?.let {
                barcodeImageGenerator.generateBitmap(
                    it,
                    2000,
                    2000,
                    0,
                    Color.BLACK,
                    Color.WHITE
                )
            }
            imageQr.setImageBitmap(bitmap)
        } catch (ex: Exception) {

        }
    }

    private fun initMenuBar() {

        btnEditName.setOnClickListener {
            showEditBarcodeNameDialog()
        }

        btnDelete.setOnClickListener {
            deleteBarcode()
        }

        btnFavorito.setOnClickListener {
            selectedIsFavorite()
        }

    }

    private fun showDataBarcode(){
        showBarcodeDate()
        showBarcodeSchema()
        showBarcodeContent()
        showBarcodeNameInit()
        showBarcodeSchema()
        showBarcodeIsFavorite()
    }

    private fun showBarcodeDate(){

        txtDate.text=dateFormatter.format(barcodeParsed?.date)

    }

    private fun showBarcodeSchema(){
        txtSchemaName.text=barcodeParsed?.schema.toString()
    }

    private fun showBarcodeContent(){
        //01 revisar en el futuro al momento de crear
        txtContent.text=barcodeParsed?.formattedText
    }

    private fun showBarcodeNameInit(){
        showBarcodeName(barcodeParsed?.name ?: "")
    }

    private fun showBarcodeIsFavorite(){
        showBarcodeIsFavorite(barcodeParsed?.isFavorite ?:false)
    }

    private fun applySetting(){
        if(settings.copyToClipboard){
            barcodeParsed?.text?.let { copyToClipboard(it) }
        }

    }

    //utilizando el barcode schema ver si asigno el onlistener solo a algunos botones o no
    private fun showOrHideButtons(){
        btnUrl.isVisible=barcodeParsed?.url.isNullOrBlank().not()
        btnPlayStore.isVisible=barcodeParsed?.appMarketUrl.isNullOrBlank().not()
        btnSendSms.isVisible=barcodeParsed?.phone.isNullOrBlank().not() || barcodeParsed?.smsBody.isNullOrBlank().not()
        btnLookLocation.isVisible=barcodeParsed?.geoUri.isNullOrBlank().not()
        btnSearchFlight.isVisible=barcodeParsed?.numberFlight.isNullOrBlank().not()
        btnAddContact.isVisible=BarcodeSchema.VCARD==barcodeParsed?.schema
        btnSendEmail.isVisible=barcodeParsed?.email.isNullOrBlank().not()
        btnCopyPassWifi.isVisible=barcodeParsed?.networkPassword.isNullOrBlank().not()
        //01 revisar si  se le integra con whassap enviar whatasspp
    }

    private fun handleButtonsClicked() {

        btnQrSaveImage.setOnClickListener {
            checkPermissionStorage()
        }

        btnCopyPassWifi.setOnClickListener {
            copyNetworkPasswordToClipboard()
        }
        
        btnUrl.setOnClickListener{
            openLink(barcodeParsed?.url.orEmpty())
        }

        btnSendSms.setOnClickListener{
            sendSmsOrMms(barcodeParsed?.phone)
        }

        btnLookLocation.setOnClickListener {
            showLocation()
        }

        btnSearchFlight.setOnClickListener {
            openLink("https://www.flightradar24.com/data/flights/"+barcodeParsed?.numberFlight)
        }

        btnAddContact.setOnClickListener {
            addToContacts()
        }

        btnSendEmail.setOnClickListener {
            sendEmail(barcodeParsed?.email.orEmpty())
        }
    }


    private fun copyNetworkPasswordToClipboard() {
        copyToClipboard(barcodeParsed?.networkPassword.orEmpty())
        snackBar(1)
    }

    private fun copyToClipboard(text: String) {
        val clipData = ClipData.newPlainText("", text)
        clipboardManager.setPrimaryClip(clipData)
        snackBar(1)
    }

    private fun snackBar(stringId: Int) {
        //01 completar con los string para que sea multilenguaje
        Snackbar.make(requireView(), "Copied to clipboard! \uD83D\uDCCB", Snackbar.LENGTH_LONG)
            .show()

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

    //save png or svg
    private fun checkPermissionStorage() {
        coarsePermission.runWithPermission {

            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_save_qr_options, null)
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
            val btnOptionSavePng: Button = bottomSheetView.findViewById(R.id.btnPngFormatQr)
            val btnOptionSaveSvg: Button = bottomSheetView.findViewById(R.id.btnSvgFormatQr)

            if (param1 != null) {

                btnOptionSavePng.setOnClickListener {
                    //falta almacenar el resultado sino no funciona el complete
                    val saveFun = barcodeImageGenerator.generateBitmapAsync(param1!!, 640, 640, 2)
                        .flatMapCompletable {
                            barcodeImageSaved.savePngImageToPublicDirectory(
                                requireContext(),
                                it,
                                param1!!
                            )
                        }
                    //01 revisar si esta forma esta bien
                    bottomSheetDialog.dismiss()
                    saveFunComplement(saveFun)

                }

                btnOptionSaveSvg.setOnClickListener {
                    val saveFun = barcodeImageGenerator
                        .generateSvgAsync(param1!!, 640, 640, 2)
                        .flatMapCompletable {
                            barcodeImageSaved.saveSvgImageToPublicDirectory(
                                requireContext(),
                                it,
                                param1!!
                            )
                        }
                    //01 revisar si esta forma esta bien
                    bottomSheetDialog.dismiss()
                    saveFunComplement(saveFun)
                }


            }

        }
    }

    private fun saveFunComplement(saveFun: Completable) {
        //01 revisar como mostrar el guardado sobre la pantalla
        saveFun.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { showBarcodeSaved() },
                {
                    Toast.makeText(requireContext(), "error guardar", Toast.LENGTH_SHORT).show()
                }
            ).addTo(disposable)
    }


    //accinones de btn
    private fun selectedIsFavorite() {

        val temBarcode = barcodeParsed?.let { param1?.copy(isFavorite = it.isFavorite.not()) }
        if (temBarcode != null) {
            barcodeDatabase.save(temBarcode).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    barcodeParsed?.isFavorite = temBarcode.isFavorite
                    showBarcodeIsFavorite(temBarcode.isFavorite)
                }, {
                    //error
                }
            ).addTo(disposable)
        }
    }

    private fun showEditBarcodeNameDialog() {
        val builder = AlertDialog.Builder(requireContext())
        //01 al poner diferentes idiomas
        builder.setTitle("Edit Barcode Name")

        // Configura el campo de entrada
        val input = EditText(requireContext())
        input.filters = arrayOf(InputFilter.LengthFilter(30))
        builder.setView(input)

        // Configura los botones del diálogo
        builder.setPositiveButton("Save") { dialog, _ ->
            val newBarcodeName = input.text.toString()
            // Haz algo con el nuevo nombre del código de barras
            onNameConfirmed(newBarcodeName)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun deleteBarcode() {
        val builder = AlertDialog.Builder(requireContext())
        //01 al poner diferentes idiomas
        builder.setTitle("Eliminar")


        // Configura los botones del diálogo
        builder.setPositiveButton("Confirmar") { dialog, _ ->
            // eliminar

            param1?.id?.let {
                barcodeDatabase.delete(it).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(
                        {
                            Toast.makeText(requireContext(), "eliminar", Toast.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.popBackStack()
                        },{
                            //error delete
                        }
                    ).addTo(disposable)
            }

        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun onNameConfirmed(name: String) {

        if (name.isBlank()) return

        val temBarcode = param1?.id?.let { param1?.copy(id = it, name = name) }

        if (temBarcode != null) {
            barcodeDatabase.save(temBarcode).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    param1?.name = name
                    showBarcodeName(name)
                }, {
                    //error
                }
            ).addTo(disposable)
        }
    }

    private fun openLink(url :String ){
        startActivityIfExists(Intent.ACTION_VIEW,url)
    }

    private fun sendSmsOrMms(phone:String?){
        val uri = Uri.parse("sms:${phone.orEmpty()}")

        val intent =Intent(Intent.ACTION_SENDTO,uri).apply {
            putExtra("sms_body",barcodeParsed?.smsBody.orEmpty())
        }

        startActivityIfExists(intent)
    }

    private fun showLocation(){
        val geoUri= barcodeParsed?.geoUri
        startActivityIfExists(Intent.ACTION_VIEW, geoUri.orEmpty())

    }

    private fun  addToContacts(){
        val fullName = "${barcodeParsed?.firstName.orEmpty()} ${barcodeParsed?.lastName.orEmpty()}"

        val intent=Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type=ContactsContract.Contacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.NAME,fullName)
            putExtra(ContactsContract.Intents.Insert.PHONE,barcodeParsed?.phone.orEmpty())
            putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,barcodeParsed?.phoneType.orEmpty())
            putExtra(ContactsContract.Intents.Insert.EMAIL,barcodeParsed?.email.orEmpty())
            putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE,barcodeParsed?.emailType.orEmpty())
            putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, barcodeParsed?.secondaryPhone.orEmpty())
            putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, barcodeParsed?.secondaryPhoneType.orEmpty())
            putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE, barcodeParsed?.tertiaryPhone.orEmpty())
            putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE, barcodeParsed?.tertiaryPhoneType.orEmpty())



        }


        try {
            startActivity(intent)
        }catch (e:Exception){
            FirebaseCrashlytics.getInstance().log("Error 521 (addToContacts function)")
            FirebaseCrashlytics.getInstance().recordException(e)
        }



    }

   private fun sendEmail(email :String) {
       val uri=Uri.parse("mailto:${email.orEmpty()}")
       val intent=Intent(Intent.ACTION_SEND,uri).apply {
           type="text/plain"
           putExtra(Intent.EXTRA_EMAIL, arrayOf(email.orEmpty()))
           putExtra(Intent.EXTRA_SUBJECT, barcodeParsed?.emailSubject.orEmpty())
           putExtra(Intent.EXTRA_TEXT, barcodeParsed?.emailBody.orEmpty())

       }
       startActivityIfExists(intent)
   }

    //cambio de iconos e datos en interfaz
    private fun showBarcodeIsFavorite(isFavorite: Boolean) {
        val iconid = if (isFavorite) {
            R.drawable.favorite_on
        } else {
            R.drawable.favorite_off
        }
        btnFavorito.setImageResource(iconid)
    }

    private fun showBarcodeName(name: String) {
        txtNameQr.isVisible = name.isNullOrBlank().not()
        txtNameQr.text = name.orEmpty()
    }

    private fun startActivityIfExists(action: String,uri: String){
        val intent= Intent(action, Uri.parse(uri))
        startActivity(intent)
    }
    private fun startActivityIfExists(intent: Intent){
        intent.apply {
            flags = flags or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if(intent.resolveActivity(requireContext().packageManager)!=null){
            startActivity(intent)
        }else{
            Toast.makeText(requireContext(), "01 app no encontrada", Toast.LENGTH_SHORT).show()
        }
    }


    //muestra de mensajes
    private fun showBarcodeSaved() {
        //01 completar con los string para que sea multilenguaje
        Snackbar.make(requireView(), "qr Guardado! \uD83D\uDCCB", Snackbar.LENGTH_LONG)
            .show()

    }
}