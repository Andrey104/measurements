package com.addd.measurements

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var login: String
    private lateinit var password: String
    private lateinit var answerHttp: String
    private val server: String = "http://188.225.46.31/api/"
    private val gson: Gson = GsonBuilder().create()
    private val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(server)
            .build()
    private val req = retrofit.create(MeasurementsAPI::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener { goLogin() }

    }

    private fun goLogin() {
        if (editLogin.length() == 0 || editPassword.length() == 0) {
            Toast.makeText(applicationContext, "Пустое поле Логи или Пароль", Toast.LENGTH_SHORT).show()
        } else {
            login = editLogin.text.toString()
            password = editPassword.text.toString()

            val call = req.performPostCall(login, password)


            call.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.body() == null) {
                        Toast.makeText(applicationContext,"Неправильный логин или пароль", Toast.LENGTH_SHORT).show()
                    } else {
                        val map = gson.fromJson(response.body().toString(), HashMap::class.java)
                        answerHttp = map["token"].toString()
                        textViewTest.text = answerHttp
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    textViewTest.text = "Request error"
                }
            })
        }
    }
}
