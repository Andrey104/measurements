package com.addd.measurements

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.widget.Toast
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AlertDialog
import com.addd.measurements.activity.LoginActivity


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val APP_TOKEN = "myToken"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Выход из приложения")
                    .setMessage("Вы действительно хотите выйти из приложения?")
                    .setCancelable(false)
                    .setPositiveButton("Да",
                            { dialog, id -> finish() })
                    .setNegativeButton("Нет", {dialog, id -> dialog.cancel()})
            val alert = builder.create()
            alert.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_search -> {Toast.makeText(applicationContext,"Поиск хз чего", Toast.LENGTH_SHORT).show()
            return true}

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_measurements -> {
                // Handle the camera action
            }
            R.id.nav_myObjects -> {

            }
            R.id.nav_problems -> {

            }
            R.id.nav_exit-> {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Выход из аккаунта")
                        .setMessage("Вы действительно хотите выйти из аккаунта?")
                        .setCancelable(false)
                        .setPositiveButton("Да",
                                { dialog, id ->
                                    val mSettings: SharedPreferences = getSharedPreferences(APP_TOKEN, Context.MODE_PRIVATE)
                                    val editor = mSettings.edit()
                                    editor.clear()
                                    editor.apply()
                                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                                    finish() })
                        .setNegativeButton("Нет", {dialog, id -> dialog.cancel()})
                val alert = builder.create()
                alert.show()
            }
                   }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
