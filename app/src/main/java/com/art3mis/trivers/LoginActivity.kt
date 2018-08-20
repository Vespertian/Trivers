package com.art3mis.trivers

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class LoginActivity : AppCompatActivity(), TextWatcher {

    private lateinit var editText_Email: EditText
    private lateinit var editText_Password: EditText
    private lateinit var progessBarL: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var password: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        editText_Email = findViewById(R.id.editText_Email)
        editText_Password = findViewById(R.id.editText_Password)
        editText_Email.addTextChangedListener(this)
        editText_Password.addTextChangedListener(this)

        progessBarL = findViewById(R.id.progressBarL)
        auth = FirebaseAuth.getInstance()
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        email = editText_Email.text.toString()
        password = editText_Password.text.toString()
    }

    fun registerActivity(View: View){
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun email_login(view: View){
        loginUser()
    }

    private fun loginUser(){
        if (!email.isEmpty()&&!password.isEmpty()){
            progessBarL.visibility = View.VISIBLE

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){
                task ->

                if(task.isSuccessful){
                    action()
                } else{
                    alert("Correo electrónico o contraseña no válidos") {
                        title("Error al iniciar sesión")
                        yesButton {  }
                    }.show()
                }
            }
        }
    }

    private fun action(){
        startActivity(Intent(this, PrivateProfileActivity::class.java))
    }

    fun ForgotPassword(view: View){
        startActivity(Intent(this, ForgotPassActivity::class.java))
    }
}
