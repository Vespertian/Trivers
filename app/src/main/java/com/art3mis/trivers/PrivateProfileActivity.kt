package com.art3mis.trivers

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


open class PrivateProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_profile)
        val objectIntent: Intent = intent
        var emailP = objectIntent.getStringExtra("Cr")
        var passwordP = objectIntent.getStringExtra("Ct")
        information().getUserInformation(emailP, passwordP)
    }
}
