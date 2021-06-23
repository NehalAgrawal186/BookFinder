package com.nehal.bookfinder.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.nehal.bookfinder.R
import com.nehal.bookfinder.util.ConnectionManager

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var etForgotEmail: EditText
    lateinit var btnForgotNext: Button
    lateinit var progressBar: ProgressBar
    lateinit var fauth: FirebaseAuth
    lateinit var password: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password_screen)


        etForgotEmail = findViewById(R.id.etForgotEmail)
        btnForgotNext = findViewById(R.id.btnForgotNext)
        progressBar = findViewById(R.id.progressBar)
        password = findViewById(R.id.password)
        fauth = FirebaseAuth.getInstance()

        progressBar.visibility = View.GONE

        password.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        btnForgotNext.setOnClickListener {

            val email = etForgotEmail.text.toString()

            if (email.isNotEmpty()) {

                progressBar.visibility = View.GONE

                if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {

                    fauth.sendPasswordResetEmail(email).addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            startActivity(Intent(this, LoginActivity::class.java))
                            Toast.makeText(this, "Email Sent!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val dialog = AlertDialog.Builder(
                        this@ForgotPasswordActivity
                    )
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
                    }
                    dialog.create()
                    dialog.show()
                }

            } else {
                etForgotEmail.error = "Please fill out this field"
            }

        }
    }
}