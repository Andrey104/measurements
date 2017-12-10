package com.addd.measurements.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.addd.measurements.MeasurementsAPI
import com.addd.measurements.R
import com.addd.measurements.fragments.MeasurementsFragment
import com.addd.measurements.fragments.MyObjectsFragment
import com.addd.measurements.fragments.ProblemsFragment
import com.addd.measurements.modelAPI.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var APP_PREFERENCES: String
    private lateinit var APP_USER_INFO: String
    private lateinit var APP_TOKEN: String
    private var userInfo: User = User()
    private val serviceAPI = MeasurementsAPI.Factory.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        APP_PREFERENCES = getString(R.string.my_settings)
        APP_TOKEN = getString(R.string.token)
        APP_USER_INFO = getString(R.string.user_info)
        title = getString(R.string.measurements)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        var fragmentClass: Class<*>?
        fragmentClass = MeasurementsFragment::class.java
        startFragment(fragmentClass)

        informationUser()
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
                    .setNegativeButton("Нет", { dialog, id -> dialog.cancel() })
            val alert = builder.create()
            alert.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_search -> {
                Toast.makeText(applicationContext, "Поиск хз чего", Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragmentClass: Class<*>?
        when (item.itemId) {
            R.id.nav_measurements -> {
                fragmentClass = MeasurementsFragment::class.java
                changeFragment(fragmentClass, item)
            }
            R.id.nav_myObjects -> {
                fragmentClass = MyObjectsFragment::class.java
                changeFragment(fragmentClass, item)
            }
            R.id.nav_problems -> {
                fragmentClass = ProblemsFragment::class.java
                changeFragment(fragmentClass, item)
            }
            R.id.nav_exit -> {
                exitFromApp()
            }
        }



        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun exitFromApp() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Выход из аккаунта")
                .setMessage("Вы действительно хотите выйти из аккаунта?")
                .setCancelable(false)
                .setPositiveButton("Да",
                        { dialog, id ->
                            val mSettings: SharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                            val editor = mSettings.edit()
                            editor.clear()
                            editor.apply()
                            startActivity(Intent(applicationContext, LoginActivity::class.java))
                            finish()
                        })
                .setNegativeButton("Нет", { dialog, id -> dialog.cancel() })
        val alert = builder.create()
        alert.show()
    }

    private fun changeFragment(fragmentClass: Class<*>?, item: MenuItem) {
        var fragment: Fragment? = null

        try {
            fragment = fragmentClass!!.newInstance() as Fragment?
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Вставляем фрагмент, заменяя текущий фрагмент
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        // Выделяем выбранный пункт меню в шторке
        item.isChecked = true
        // Выводим выбранный пункт в заголовке
        title = item.title
    }

    private fun startFragment(fragmentClass: Class<*>?) {
        var fragment: Fragment? = null

        try {
            fragment = fragmentClass!!.newInstance() as Fragment?
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    private fun informationUser() {
        val mSettings: SharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if (mSettings.contains(APP_TOKEN)) {
            val token: String = "Token " + mSettings.getString(APP_TOKEN, "")
            val call = serviceAPI.userInfo(token)

            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>?, response: Response<User>?) {
                    if (response!!.body() != null) {
                        userInfo = response.body()
                        saveUserInfo(applicationContext, userInfo)
                        val navigationView: NavigationView = findViewById(R.id.nav_view)
                        val navHeader = navigationView.getHeaderView(0)
                        val textName = navHeader.findViewById<TextView>(R.id.textUserNameDrawer)
                        textName.text = "${userInfo.firstName} ${userInfo.lastName}"
                    }
                }

                override fun onFailure(call: Call<User>?, t: Throwable?) {
                    userInfo = loadSharedPreferencesList(applicationContext)
                    val navigationView: NavigationView = findViewById(R.id.nav_view)
                    val navHeader = navigationView.getHeaderView(0)
                    val textName = navHeader.findViewById<TextView>(R.id.textUserNameDrawer)
                    textName.text = "${userInfo.firstName} ${userInfo.lastName}"
                }
            })
        }
    }

    private fun saveUserInfo(context: Context, user: User) {
        val mPrefs = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val prefsEditor = mPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(user)
        prefsEditor.putString(APP_USER_INFO, json)
        prefsEditor.commit()
    }

    private fun loadSharedPreferencesList(context: Context): User {
        var user: User
        val mPrefs = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = mPrefs.getString(APP_USER_INFO, "")
        user = if (json!!.isEmpty()) {
            User()
        } else {
            val type = object : TypeToken<User>() {
            }.type
            gson.fromJson(json, type)
        }
        return user
    }
}
