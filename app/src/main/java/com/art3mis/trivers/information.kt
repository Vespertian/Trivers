package com.art3mis.trivers

import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.jetbrains.anko.alert


open class information: AppCompatActivity() {
    private lateinit var dbreference: DatabaseReference
    private lateinit var userID: String
    private var userInformation: HashMap<String, Any> = HashMap()

    fun registerInformation(user: FirebaseUser, name: String, lastName: String?, email:String, age: String, phoneNumber: String, rangoMinimo: String, rangoMaximo: String, description: String, fPerfil:String){
        dbreference = FirebaseDatabase.getInstance().getReference("Users")
        val userI: HashMap<String, Any?> = HashMap()
        userI["name"] = name
        userI["lastName"] = lastName
        userI["age"] = age
        userI["email"] = email
        userI["phoneNumber"] = phoneNumber
        userI["rangoMinimo"] = rangoMinimo
        userI["rangoMaximo"] = rangoMaximo
        userI["description"] = description
        userI["fPerfi"] = fPerfil
        dbreference.child(user!!.uid).setValue(userI).addOnCompleteListener(this) {
            task ->

            if (!task.isComplete){
                alert("No se ha podido crear tu cuenta") {
                    title("Error de registro")
                    okButton {}
                }.show()
            }
        }
    }

    fun registerInformation(user: FirebaseUser, name: String, lastName: String?, email:String, age: String, phoneNumber: String, rangoMinimo: String, rangoMaximo: String, description: String){
        dbreference = FirebaseDatabase.getInstance().getReference("Users")
        val userI: HashMap<String, Any?> = HashMap()
        userI["name"] = name
        userI["lastName"] = lastName
        userI["age"] = age
        userI["email"] = email
        userI["phoneNumber"] = phoneNumber
        userI["rangoMinimo"] = rangoMinimo
        userI["rangoMaximo"] = rangoMaximo
        userI["description"] = description
        dbreference.child(user!!.uid).setValue(userI).addOnCompleteListener(this) {
            task ->

            if (!task.isComplete){
                alert("No se ha podido crear tu cuenta") {
                    title("Error de registro")
                    okButton {}
                }.show()
            }
        }
    }

    fun getUserInformation(email: String, password: String){

    }
}