package com.gtrab.qrscanmaster

import android.os.Build
import android.os.Bundle
import android.Manifest
import android.view.MenuItem
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.gtrab.qrscanmaster.comunication.Communicator
import com.gtrab.qrscanmaster.model.Barcode
import com.gtrab.qrscanmaster.ui.home.Home
import com.gtrab.qrscanmaster.ui.infoqr.InfoQr
import com.gtrab.qrscanmaster.ui.history.History
import com.gtrab.qrscanmaster.util.PermissionRequester
import com.gtrab.qrscanmaster.util.openAppSettings
import com.gtrab.qrscanmaster.util.showSnackbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.gtrab.qrscanmaster.ui.config.ConfigMain
import com.gtrab.qrscanmaster.ui.create.FragmentCreateQrMain

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    Communicator {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle:ActionBarDrawerToggle
    private var savedInstance:Bundle?=null
    private var isAppOpenedForSettings = false
    //request permission camera
    private val coarsePermission =
        PermissionRequester(this, Manifest.permission.CAMERA, onRational = {
            drawerLayout.showSnackbar("acceso requerido",Snackbar.LENGTH_INDEFINITE,"Ok"){
                checkPermissionCamera()
            }

        }, onDenied = {
            drawerLayout.showSnackbar("acceso requerido",Snackbar.LENGTH_INDEFINITE,"Ok"){
                openAppSettings()
                isAppOpenedForSettings=true
            }

        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolBar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolBar)
        drawerLayout = findViewById(R.id.drawer_layout)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        //acciones para los botones
        navigationView.setNavigationItemSelectedListener(this)

        ViewCompat.setOnApplyWindowInsetsListener(navigationView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, 0, 0,0)
            insets
        }


        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            R.string.open_nav,
            R.string.close_nav
        )
        //asignacion de 2 variables para despues inicializarlas
        drawerLayout.addDrawerListener(toggle)
        savedInstance=savedInstanceState

        //iniciar el toggle y pintar el home
        checkPermissionCamera()
        //close y open drawerLayout
        btnRegresar()

    }

    override fun onResume() {
        super.onResume()
        if (isAppOpenedForSettings) {
            checkPermissionCamera()
            isAppOpenedForSettings = false
        }

    }


    private fun btnRegresar() {

        if (Build.VERSION.SDK_INT >= 33) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {

                exitOnBackPressed()
            }
        } else {
            onBackPressedDispatcher.addCallback(
                this,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {

                        exitOnBackPressed()
                    }
                })
        }
    }

    fun exitOnBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

            drawerLayout.closeDrawer(GravityCompat.START)

        } else {
            if(supportFragmentManager.backStackEntryCount>0){
                supportFragmentManager.popBackStack()
            }else{
                finish()
            }
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.nav_create ->{

                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentCreateQrMain())
                    .commit()
                true
            }

            R.id.nav_home -> {
                //verificar si el fragmento es diferente al mismo y actulizar si los es
                val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_container)

                if(fragment !is Home){
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Home())
                        .commit()
                }
                true // Retorna true para indicar que se ha manejado el evento de clic
            }

            R.id.nav_history -> {

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, History()).commit()
                true // Retorna true para indicar que se ha manejado el evento de clic
            }

            R.id.nav_config -> {

                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ConfigMain())
                    .commit()
                true // Retorna true para indicar que se ha manejado el evento de clic
            }

            R.id.nav_logout -> {

                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()

                true // Retorna true para indicar que se ha manejado el evento de clic
            }
            // Agrega más casos según sea necesario para otros elementos del menú
            else -> false // Retorna false para indicar que el evento de clic no ha sido manejado


        }.also {
            drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
    }

    //arreglar despues el el comunicador
    override fun passInfoQr(barcode : Barcode) {
        val infoQrFragment = InfoQr.newInstance(barcode)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, infoQrFragment).addToBackStack(null)
            .commit()
    }


    private fun checkPermissionCamera() {
        coarsePermission.runWithPermission {
            if (savedInstance == null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Home())
                    .commit()
            }
            toggle.syncState()

        }
    }
}