package com.art3mis.trivers

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import org.jetbrains.anko.alert


class LoginActivity : AppCompatActivity(), TextWatcher, GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    private lateinit var editText_Email: EditText
    private lateinit var editText_Password: EditText
    private lateinit var progessBarL: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var password: String
    private lateinit var email: String
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var dbreference: DatabaseReference
    private var RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        editText_Email = findViewById(R.id.editText_Email)
        editText_Password = findViewById(R.id.editText_Password)
        editText_Email.addTextChangedListener(this)
        editText_Password.addTextChangedListener(this)

        progessBarL = findViewById(R.id.progressBarL)
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.WEB_CLIENT_ID))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    fun google_login(view: View){
        signIn()
        progessBarL.visibility = View.VISIBLE
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {

            }

        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        alert("Inició con el correo: ${user!!.email}") {
                            title("Iniciando sesión...")
                            okButton {action_Information()
                                progessBarL.visibility = View.INVISIBLE}
                        }.show()
                    }
                }
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        email = ""
        password = ""
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
                    if (auth.currentUser!!.isEmailVerified){
                        progessBarL.visibility = View.INVISIBLE
                        action_PrivateProfile()
                    } else{
                        alert("Hemos enviado un correo a tu email, por favor verifícalo ") {
                            title("Error al iniciar sesión")
                            yesButton {  }
                        }.show()
                    }
                } else{
                    alert("Correo electrónico o contraseña no válidos") {
                        title("Error al iniciar sesión")
                        yesButton {  }
                    }.show()
                }
                progessBarL.visibility = View.INVISIBLE
            }
        } else{
            alert("Correo electrónico o contraseña no válidos") {
                title("Error al iniciar sesión")
                yesButton {  }
            }.show()
        }
    }

    private fun action_PrivateProfile(){
        val intent = Intent(this, PrivateProfileActivity::class.java)
        startActivity(intent)
    }

    private fun action_Information(){
        dbreference = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser!!.uid)
        val intent = Intent(this, RegisterActivity::class.java)
        dbreference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dS: DataSnapshot) {
                if (dS.child("lastName").value.toString() == "NoLastName") {
                    finish()
                    action_PrivateProfile()
                } else {
                    intent.putExtra("Google_Login", true)
                    if (auth.currentUser!!.phoneNumber != null) {
                        intent.putExtra("Phone", true)
                    } else {
                        intent.putExtra("Phone", false)
                    }
                    finish()
                    startActivity(intent)
                }
            }
        })
    }

    fun ForgotPassword(view: View){
        startActivity(Intent(this, ForgotPassActivity::class.java))
    }
}
