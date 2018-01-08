package com.addd.measurements.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.addd.measurements.DEAL_KEY
import com.addd.measurements.R
import com.addd.measurements.modelAPI.ProblemRequest
import com.addd.measurements.network.NetworkControllerProblem
import com.addd.measurements.toast
import kotlinx.android.synthetic.main.activity_problem.*

class ProblemActivity : AppCompatActivity(), NetworkControllerProblem.AddProblemCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkControllerProblem.registerAddProblemCallback(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem)
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doAddProblemRequest() }
    }

    private fun doAddProblemRequest(): Boolean {
        if (editTextHeader.text.isEmpty() && editTextDescription.text.isEmpty()) {
            toast(R.string.enter_header_description)
            return false
        } else if (editTextHeader.text.isEmpty()) {
            toast(R.string.enter_string)
            return false
        } else if (editTextDescription.text.isEmpty()) {
            toast(R.string.enter_description)
            return false
        }
        val problem = ProblemRequest(editTextHeader.text.toString(), editTextDescription.text.toString())
        NetworkControllerProblem.addProblem(problem, intent?.getStringExtra(DEAL_KEY) ?: "0")

        return true
    }

    override fun resultAddProblem(result: Boolean) {
        if (result) {
            toast(R.string.problem_added)
            setResult(200)
            finish()
        } else {
            toast(R.string.error)
        }
    }

    override fun onResume() {
        NetworkControllerProblem.registerAddProblemCallback(this)
        super.onResume()
    }

    override fun onStop() {
        NetworkControllerProblem.registerAddProblemCallback(null)
        super.onStop()
    }
}
