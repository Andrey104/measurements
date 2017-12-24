package com.addd.measurements.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.addd.measurements.R
import com.addd.measurements.modelAPI.ProblemRequest
import com.addd.measurements.network.NetworkControllerProblem
import kotlinx.android.synthetic.main.activity_problem.*

class ProblemActivity : AppCompatActivity(), NetworkControllerProblem.AddProblemCallback{
    override fun resultAddProblem(result: Boolean) {
        if (result) {
            Toast.makeText(this, getString(R.string.problem_added), Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        NetworkControllerProblem.registerAddProblemCallback(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem)
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { doAddProblemRequest() }
    }

    private fun doAddProblemRequest(): Boolean {
        if (editTextHeader.text.isEmpty() && editTextDescription.text.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_header_description), Toast.LENGTH_SHORT).show()
            return false
        } else if (editTextHeader.text.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_string), Toast.LENGTH_SHORT).show()
            return false
        } else if (editTextDescription.text.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_description), Toast.LENGTH_SHORT).show()
            return false
        }
        val problem = ProblemRequest(editTextHeader.text.toString(), editTextDescription.text.toString())
        NetworkControllerProblem.addProblem(problem, intent.getStringExtra("deal"))

        return true
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
