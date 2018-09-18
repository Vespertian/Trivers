package com.art3mis.trivers

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


open class PrivateProfileActivity : AppCompatActivity() {
    lateinit var authP: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_profile)
        /*authP = FirebaseAuth.getInstance()
        val objectIntent: Intent = intent
        var emailP = objectIntent.getStringExtra("Nombre")
        var passwordP = objectIntent.getStringExtra("Contraseña")
        information().getUserInformation(emailP, passwordP, authP)*/
    }
}
