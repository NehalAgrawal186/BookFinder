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
import com.google.firebase.firestore.FirebaseFirestore
import com.nehal.bookfinder.R
import com.nehal.bookfinder.util.ConnectionManager

class LoginActivity : AppCompatActivity() {
    lateinit var etEmailAddress: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotPassword: TextView
    lateinit var txtRegisterYourself: TextView
    lateinit var progressBar: ProgressBar
    lateinit var fauth: FirebaseAuth
    lateinit var fstore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_screen)

        etEmailAddress = findViewById(R.id.etEmailaddress)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegisterYourself = findViewById(R.id.txtRegisterYourself)
        fauth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()

        progressBar.visibility = View.GONE

        if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
            if (fauth.currentUser != null) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        } else {
            val dialog = AlertDialog.Builder(
                this@LoginActivity
            )
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@LoginActivity)
            }
            dialog.create()
            dialog.show()
        }
        btnLogin.setOnClickListener {

            val email = etEmailAddress.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                progressBar.visibility = View.VISIBLE
                if (ConnectionManager().checkConnectivity(this@LoginActivity)) {

                    fauth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userid = fauth.currentUser?.uid
                                if (userid != null) {
                                    fstore.collection("users").document(userid)
                                        .update("password", password)
                                }
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            } else {
                                progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this,
                                    task.exception?.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                } else {
                    val dialog = AlertDialog.Builder(
                        this@LoginActivity
                    )
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@LoginActivity)
                    }
                    dialog.create()
                    dialog.show()
                }

            } else {
                showError(etEmailAddress)
                showError(etPassword)
            }
        }

        txtRegisterYourself.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            finish()
        }
    }
        private fun showError(inputField: EditText) {
            if (inputField.text.isEmpty()) {
                inputField.error = "Please fill out this field"
            }
        }


    }
