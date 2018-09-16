package com.art3mis.trivers

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.jetbrains.anko.alert


class RegisterActivity : AppCompatActivity(), TextWatcher {
    private lateinit var editText_Name: EditText
    private lateinit var editText_LastName: EditText
    private lateinit var editText_Age: EditText
    private lateinit var editText_EmailR: EditText
    private lateinit var editText_PasswordR: EditText
    private lateinit var progessBar: ProgressBar
    private lateinit var dbreference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private lateinit var name: String
    private lateinit var lastName: String
    private lateinit var age: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        editText_Name = findViewById(R.id.editText_Name)
        editText_LastName = findViewById(R.id.editText_LastName)
        editText_Age = findViewById(R.id.editText_Age)
        editText_EmailR = findViewById(R.id.editText_EmailR)
        editText_PasswordR = findViewById(R.id.editText_PasswordR)
        editText_Name.addTextChangedListener(this)
        editText_LastName.addTextChangedListener(this)
        editText_Age.addTextChangedListener(this)
        editText_EmailR.addTextChangedListener(this)
        editText_PasswordR.addTextChangedListener(this)

        progessBar = findViewById(R.id.progressBar)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        dbreference = database.reference.child("User")
    }

    fun register(view: View){
        createNewAccount()
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        name = editText_Name.text.toString()
        lastName = editText_LastName.text.toString()
        age = editText_Age.text.toString()
        email = editText_EmailR.text.toString()
        password = editText_PasswordR.text.toString()
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    private fun validateEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    fun actionInformationActivity(){
        startActivity(Intent(this, InformationActivity::class.java))
    }

    open fun actionLoginActivity(){
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun createNewAccount(){
        if (!name.isEmpty()&&!lastName.isEmpty()&&!age.isEmpty()&&!email.isEmpty()&&!password.isEmpty()){
            if(age.toInt()<18){
                alert("Por favor no sigas intentando") {
                    title("Error")
                    okButton {actionLoginActivity()}
                }.show()
            } else{
                if (validateEmail(email)){
                    progessBar.visibility = View.VISIBLE
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){
                        task ->

                        if (task.isComplete){
                            val user:FirebaseUser? = auth.currentUser
                            verifyEmail(user)

                            val userBD = dbreference.child(user?.uid!!)
                            userBD.child("Name").setValue(name)
                            userBD.child("lastName").setValue(lastName)
                            userBD.child("age").setValue(age)
                            actionInformationActivity()
                        }
                    }
                } else{
                    alert("Correo electrónico no válido") {
                        title("Error de Email")
                        yesButton {  }
                    }.show()
                }
            }
        } else {
            alert("Por favor ingresa todos los datos para poder continuar") {
                title("Datos incompletos")
                yesButton {}
            }.show()
        }
    }

    private fun verifyEmail(user: FirebaseUser?){
        user?.sendEmailVerification()?.addOnCompleteListener(this){
            task ->

            if (!task.isComplete){
                alert("Correo electrónico no válido") {
                    title("Error de Email")
                    yesButton {  }
                }.show()
            }
        }
    }
}
