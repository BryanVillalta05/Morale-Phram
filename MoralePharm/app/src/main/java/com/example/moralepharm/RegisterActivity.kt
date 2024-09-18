package com.example.moralepharm

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var btnlogin: Button
    private lateinit var btnregis: Button
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private fun register(mail:String, pwd: String){
        auth.createUserWithEmailAndPassword(mail, pwd).addOnCompleteListener { task->
            if(task.isSuccessful){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener{ exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        btnlogin = findViewById<Button>(R.id.btnLogin)
        btnlogin.setOnClickListener{
            val mail = findViewById<EditText>(R.id.mailtxt).text.toString()
            val pwd = findViewById<EditText>(R.id.pwdtxt).text.toString()
            this.register(mail,pwd)
        }
        btnregis = findViewById<Button>(R.id.btnregis)
        btnregis.setOnClickListener{
            this.goToLogin()
        }
        this.checkUser()
        }

    override fun onResume() {
        super.onResume()
        auth.addAuthStateListener(authStateListener)
    }
    override fun onPause() {
        super.onPause()
        auth.addAuthStateListener(authStateListener)
    }
    private fun checkUser(){
        authStateListener = FirebaseAuth.AuthStateListener { auth ->

            if (auth.currentUser != null){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
