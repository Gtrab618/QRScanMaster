package com.example.qrscanmaster

import android.os.Build
import android.os.Bundle
import android.view.Gravity
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
import com.example.qrscanmaster.comunication.Communicator
import com.example.qrscanmaster.ui.home.Home
import com.example.qrscanmaster.ui.setting.Setting
import com.example.qrscanmaster.ui.share.Share
import com.google.android.material.navigation.NavigationView
import org.jetbrains.annotations.NotNull

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener,
    Communicator {
    private lateinit var drawerLayout:DrawerLayout
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolBar= findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolBar)
        drawerLayout=findViewById(R.id.drawer_layout)

        val navigationView= findViewById<NavigationView>(R.id.nav_view)
        //acciones para los botones
        navigationView.setNavigationItemSelectedListener(this)

        btnRegresar()

        val toggle= ActionBarDrawerToggle(this,drawerLayout,toolBar,R.string.open_nav,R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState==null){
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Home()).commit()
        }




    }

    private  fun btnRegresar(){

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
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){

            drawerLayout.closeDrawer(GravityCompat.START)

        }else{
            finish()
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.nav_home -> {

                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Home()).commit()
                true // Retorna true para indicar que se ha manejado el evento de clic
            }
            R.id.nav_setting -> {

                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Setting()).commit()
                true // Retorna true para indicar que se ha manejado el evento de clic
            }
            R.id.nav_share -> {

                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Share()).commit()
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

    override fun passInfoQr(data: String) {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show()
    }

}