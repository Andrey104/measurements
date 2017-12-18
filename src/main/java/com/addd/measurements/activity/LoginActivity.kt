package com.addd.measurements.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.addd.measurements.R
import com.addd.measurements.network.NetworkAuthorization
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), NetworkAuthorization.MyCallback {
    private lateinit var APP_TOKEN: String

    override fun onCreate(savedInstanceState: Bundle?) {
        APP_TOKEN = getString(R.string.token)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val mSettings: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if (mSettings.contains(APP_TOKEN)) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener { goLogin() }
    }

    override fun onResume() {
        NetworkAuthorization.registerCallback(this)
        super.onResume()
    }

    private fun goLogin() {
        if (editLogin.length() == 0 || editPassword.length() == 0) {
            Toast.makeText(applicationContext, "Пустое поле Логин или Пароль", Toast.LENGTH_SHORT).show()
        } else {
            NetworkAuthorization.authorization(editLogin.text.toString(), editPassword.text.toString(), this)
        }
    }

    override fun resultAuth(result: Int) {
        when (result) {
            200 -> {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
            400 -> Toast.makeText(applicationContext, "Неправильный логин или пароль", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(applicationContext, "Что-то пошло не так =(", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        NetworkAuthorization.registerCallback(null)
        super.onDestroy()
    }
}