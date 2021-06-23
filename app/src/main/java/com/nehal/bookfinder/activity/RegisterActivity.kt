package com.nehal.bookfinder.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nehal.bookfinder.R
import com.nehal.bookfinder.model.User
import com.nehal.bookfinder.util.ConnectionManager


class RegisterActivity :AppCompatActivity(){
    lateinit var btnRegister: Button
    lateinit var etName: EditText
    lateinit var etPhoneNumber: EditText
    lateinit var etPassword: EditText
    lateinit var etEmail: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var progressBar: ProgressBar
    lateinit var fauth: FirebaseAuth
    lateinit var fstore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_screen)



        etName = findViewById(R.id.etName)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)
        fauth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()

        progressBar.visibility = View.GONE

        btnRegister.setOnClickListener {

            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val phoneno = etPhoneNumber.text.toString()
            val password = etPassword.text.toString()
            val confirmpassword = etConfirmPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && phoneno.isNotEmpty() && password.isNotEmpty() && confirmpassword.isNotEmpty()) {
                if (password.equals(confirmpassword) && phoneno.length == 10 && Patterns.PHONE.matcher(
                        phoneno
                    ).matches()
                ) {
                    if (ConnectionManager().checkConnectivity(this@RegisterActivity)) {

                        progressBar.visibility = View.VISIBLE
                        fauth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = User(
                                        name,
                                        email,
                                        phoneno,
                                        password
                                    )
                                    val userid = fauth.currentUser?.uid
                                    if (userid != null) {
                                        fstore.collection("users").document(userid).set(user)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    startActivity(
                                                        Intent(
                                                            this,
                                                            HomeActivity::class.java
                                                        )
                                                    )
                                                    finish()
                                                    Toast.makeText(
                                                        this,
                                                        "Registered Successfully",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Some error occurred",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }

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
                            this@RegisterActivity
                        )
                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Connection is not Found")
                        dialog.setPositiveButton("Open Settings") { text, listener ->
                            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingsIntent)
                            finish()
                        }
                        dialog.setNegativeButton("Exit") { text, listener ->
                            ActivityCompat.finishAffinity(this@RegisterActivity)
                        }
                        dialog.create()
                        dialog.show()
                    }
                } else {
                    if (phoneno.length != 10 || !Patterns.PHONE.matcher(phoneno).matches()) {
                        etPhoneNumber.error = "Invalid"
                        Toast.makeText(
                            this,
                            "Invalid Phone No",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Password and confirm password do not match",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

            } else {
                showError(etName)
                showError(etEmail)
                showError(etPhoneNumber)
                showError(etPassword)
                showError(etConfirmPassword)
            }
        }


    }

    private fun showError(inputField: EditText) {
        if (inputField.text.isEmpty()) {
            inputField.error = "Please fill out this field"
        }
    }


}