package com.addd.measurements.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.addd.measurements.APP_TOKEN
import com.addd.measurements.R
import com.addd.measurements.network.NetworkAuthorization
import com.addd.measurements.toast
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), NetworkAuthorization.MyCallback, TextView.OnEditorActionListener {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val mSettings: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if (mSettings.contains(APP_TOKEN)) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
        editLogin.setOnEditorActionListener(this)

        btnLogin.setOnClickListener { goLogin() }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        return true
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
            300 -> toast(R.string.account_error)
            400 -> toast(R.string.wrong_login_password)
            else -> toast(R.string.something_wrong)
        }
    }

    override fun onStop() {
        NetworkAuthorization.registerCallback(null)
        super.onStop()
    }
}