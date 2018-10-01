package com.art3mis.trivers

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_pass.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

class ForgotPassActivity : AppCompatActivity(), TextWatcher {
    private lateinit var EditText_emailFP: EditText
    private lateinit var emailFP: String
    private lateinit var auth: FirebaseAuth
    private lateinit var progessBarFP: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)
        EditText_emailFP = findViewById<EditText>(R.id.editText_emailFP)
        EditText_emailFP.addTextChangedListener(this)
        auth = FirebaseAuth.getInstance()
        progessBarFP = findViewById(R.id.progressBarFP)
        emailFP = ""
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        emailFP = ""
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        emailFP = editText_emailFP.text.toString()
    }

    fun sendFP(view: View){
        if (!emailFP.isEmpty()){
            progessBarFP.visibility = View.VISIBLE
            auth.sendPasswordResetEmail(emailFP).addOnCompleteListener(this){
                task ->

                if(task.isSuccessful){
                    alert("Hemos enviado un correo eléctronico a tu Email para continuar con el proceso") {
                        title("Email enviado")
                        yesButton {  }
                    }.show()
                    startActivity(Intent(this, LoginActivity::class.java))
                } else{
                    alert("No se pudo enviar el email") {
                        title("Error")
                        yesButton {  }
                    }.show()
                }
                progessBarFP.visibility = View.INVISIBLE
            }
        } else{
            alert("Ningún email detectado") {
                title("Error")
                yesButton {  }
            }.show()
        }
    }
}
