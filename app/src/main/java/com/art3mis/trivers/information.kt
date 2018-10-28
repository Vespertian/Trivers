package com.art3mis.trivers

import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.jetbrains.anko.alert


open class information: AppCompatActivity() {
    private lateinit var dbreference: DatabaseReference
    private lateinit var userID: String

    fun registerInformation(user: FirebaseUser, name: String, lastName: String?, email:String, age: String, phoneNumber: String, rangoMinimo: String, rangoMaximo: String, description: String, fPerfil:String, Genero: String, GeneroB: String){
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
        userI["Genero"] = Genero
        userI["GeneroB"] = GeneroB
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

    fun registerInformation(user: FirebaseUser, name: String, lastName: String?, email:String, age: String, phoneNumber: String, rangoMinimo: String, rangoMaximo: String, description: String, Genero: String, GeneroB: String){
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
        userI["Genero"] = Genero
        userI["GeneroB"] = GeneroB
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
}