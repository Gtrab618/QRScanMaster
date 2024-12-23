package com.gtrab.qrscanmaster

import android.os.Build
import android.os.Bundle
import android.Manifest
import android.content.res.Configuration
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.replace
import com.gtrab.qrscanmaster.comunication.Communicator
import com.gtrab.qrscanmaster.model.Barcode
import com.gtrab.qrscanmaster.ui.home.Home
import com.gtrab.qrscanmaster.ui.infoqr.InfoQr
import com.gtrab.qrscanmaster.ui.history.History
import com.gtrab.qrscanmaster.util.PermissionRequester
import com.gtrab.qrscanmaster.util.openAppSettings
import com.google.android.material.navigation.NavigationView
import com.gtrab.qrscanmaster.ui.about.About
import com.gtrab.qrscanmaster.ui.config.ConfigMain
import com.gtrab.qrscanmaster.ui.create.FragmentCreateQrMain
import com.gtrab.qrscanmaster.ui.dialogs.PermissionDialogFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    Communicator, PermissionDialogFragment.PermissionDialogListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private var savedInstance: Bundle? = null
    private var isAppOpenedForSettings = false
    private var denied = false
    private lateinit var navigationView: NavigationView

    //request permission camera
    private val coarsePermission =
        PermissionRequester(this, Manifest.permission.CAMERA, onRational = {
            showPermissionDialog(R.string.main_dlg_title, R.string.main_dlg_message)

        }, onDenied = {
            denied = true
            showPermissionDialog(R.string.main_dlg_title, R.string.main_dlg_message_denied)
        })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //para dar una mejor vista exiente el layout sobre al systembar y arriba lo que permite un fondo transparente para un ascpeto visual mejor
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolBar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolBar)
        drawerLayout = findViewById(R.id.drawer_layout)

        navigationView = findViewById<NavigationView>(R.id.nav_view)
        //acciones para los botones
        navigationView.setNavigationItemSelectedListener(this)




        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            R.string.open_nav,
            R.string.close_nav
        )
        //asignacion de 2 variables para despues inicializarlas
        drawerLayout.addDrawerListener(toggle)
        savedInstance = savedInstanceState

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

    private fun showPermissionDialog(intTitle: Int, intMessage: Int) {

        val title = getString(intTitle)
        val message = getString(intMessage)

        val dialog = PermissionDialogFragment.newInstance(
            title,
            message
        )
        dialog.show(supportFragmentManager, "")
    }

    override fun onDialogResult(result: Boolean) {
        if (result) {

            if (!denied) {
                checkPermissionCamera()
            } else {

                openAppSettings()
                isAppOpenedForSettings = true
            }

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
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment != null) {

                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()

                } else {

                    if (currentFragment.javaClass.simpleName != "Home") {
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragment_container, Home())
                            .commit()
                        navigationView.setCheckedItem(R.id.nav_home)
                    } else {
                        finish()
                    }
                }
            }

        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        when (p0.itemId) {
            R.id.nav_create -> {

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FragmentCreateQrMain())
                    .commit()
                true
            }

            R.id.nav_home -> {
                //verificar si el fragmento es diferente al mismo y actulizar si los es
                val fragment: Fragment? =
                    supportFragmentManager.findFragmentById(R.id.fragment_container)

                if (fragment !is Home) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, Home())
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

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ConfigMain())
                    .commit()
                true // Retorna true para indicar que se ha manejado el evento de clic
            }

            R.id.nav_about ->{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, About())
                    .commit()
            }

            // Agrega más casos según sea necesario para otros elementos del menú
            else -> false // Retorna false para indicar que el evento de clic no ha sido manejado


        }.also {
            drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
    }

    //arreglar despues el el comunicador
    override fun passInfoQr(barcode: Barcode) {
        val infoQrFragment = InfoQr.newInstance(barcode)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, infoQrFragment).addToBackStack(null)
            .commit()
    }


    private fun checkPermissionCamera() {
        coarsePermission.runWithPermission {
            if (savedInstance == null) {

                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, Home(), null)
                    .commit()
            }
            toggle.syncState()

        }
    }
}