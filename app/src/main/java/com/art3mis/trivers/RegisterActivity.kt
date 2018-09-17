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
    private lateinit var editText_RangoMinimo: EditText
    private lateinit var editText_RangoMaximo: EditText
    private lateinit var editText_Description: EditText
    private lateinit var progessBar: ProgressBar
    private lateinit var dbreference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private lateinit var name: String
    private lateinit var lastName: String
    private lateinit var age: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var rangoMinimo: String
    private lateinit var rangoMaximo: String
    private lateinit var description: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        editText_Name = findViewById(R.id.editText_Name)
        editText_LastName = findViewById(R.id.editText_LastName)
        editText_Age = findViewById(R.id.editText_Age)
        editText_EmailR = findViewById(R.id.editText_EmailR)
        editText_PasswordR = findViewById(R.id.editText_PasswordR)
        editText_RangoMinimo = findViewById(R.id.editText_RangoMinimo)
        editText_RangoMaximo = findViewById(R.id.editText_RangoMaximo)
        editText_Description = findViewById(R.id.editText_Description)
        editText_Name.addTextChangedListener(this)
        editText_LastName.addTextChangedListener(this)
        editText_Age.addTextChangedListener(this)
        editText_EmailR.addTextChangedListener(this)
        editText_PasswordR.addTextChangedListener(this)
        editText_RangoMinimo.addTextChangedListener(this)
        editText_RangoMaximo.addTextChangedListener(this)
        editText_Description.addTextChangedListener(this)

        progessBar = findViewById(R.id.progressBar)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        dbreference = FirebaseDatabase.getInstance().getReference("Users")
        name = ""
        lastName = ""
        age = ""
        email = ""
        password = ""
        rangoMinimo = ""
        rangoMaximo = ""
        description = ""
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
        rangoMinimo = editText_RangoMinimo.text.toString()
        rangoMaximo = editText_RangoMaximo.text.toString()
        description = editText_Description.text.toString()
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        name = ""
        lastName = ""
        age = ""
        email = ""
        password = ""
        rangoMinimo = ""
        rangoMaximo = ""
        description = ""
    }

    private fun validateEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    fun actionLoginActivity(){
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun createNewAccount(){
        if (!name.isEmpty()&&!lastName.isEmpty()&&!age.isEmpty()&&!email.isEmpty()&&!password.isEmpty()&&!rangoMinimo.isEmpty()&&!rangoMaximo.isEmpty()&&!description.isEmpty()){
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
                            val arrayList = arrayListOf<String>()
                            arrayList.add(name)
                            arrayList.add(lastName)
                            arrayList.add(age)
                            arrayList.add(rangoMinimo)
                            arrayList.add(rangoMaximo)
                            arrayList.add(description)
                            dbreference.child(user!!.uid).setValue(arrayList).addOnCompleteListener {
                                task ->

                                if (!task.isComplete){
                                    alert("No se ha podido crear tu cuenta") {
                                        title("Error de registro")
                                        okButton {actionLoginActivity()}
                                    }.show()
                                }
                            }

                            alert("Por favor verifica tu correo electrónico para poder Iniciar Sesión") {
                                title("Registro completado")
                                okButton {actionLoginActivity()}
                            }.show()
                        } else{
                            alert("No se ha podido crear tu cuenta") {
                                title("Error de registro")
                                okButton {}
                            }.show()
                        }
                        progessBar.visibility = View.INVISIBLE
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
