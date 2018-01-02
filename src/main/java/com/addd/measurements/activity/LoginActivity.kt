package com.addd.measurements.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.addd.measurements.APP_TOKEN
import com.addd.measurements.R
import com.addd.measurements.network.NetworkAuthorization
import com.addd.measurements.toast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), NetworkAuthorization.MyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {

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
            toast(R.string.emty_login_or_password)
        } else {
            NetworkAuthorization.authorization(editLogin.text.toString(), editPassword.text.toString())
        }
    }

    override fun resultAuth(result: Int) {
        when (result) {
            200 -> {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
            400 -> toast(R.string.wrong_login_password)
            else -> toast(R.string.something_wrong)
        }
    }

    override fun onStop() {
        NetworkAuthorization.registerCallback(null)
        super.onStop()
    }
}