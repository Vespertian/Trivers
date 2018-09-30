package com.art3mis.trivers

import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import org.jetbrains.anko.alert
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener



open class information: AppCompatActivity() {
    private lateinit var dbreference: DatabaseReference
    private lateinit var userID: String
    private var userInformation: HashMap<String, Any> = HashMap()

    fun registerInformation(user: FirebaseUser, name: String, lastName: String, email:String, age: String, phoneNumber: String, rangoMinimo: String, rangoMaximo: String, description: String,fPerfil:String){
        dbreference = FirebaseDatabase.getInstance().getReference("Users")
        val userI: HashMap<String, Any> = HashMap()
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

    fun registerInformation(user: FirebaseUser, name: String, lastName: String,email:String, age: String, phoneNumber: String, rangoMinimo: String, rangoMaximo: String, description: String){
        dbreference = FirebaseDatabase.getInstance().getReference("Users")
        val userI: HashMap<String, Any> = HashMap()
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
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        dbreference = FirebaseDatabase.getInstance().getReference("Users").child(userID)

        dbreference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    for (h in dataSnapshot.children){
                        userInformation = (dataSnapshot.getValue(HashMap::class.java) as HashMap<String, Any>?)!!
                    }
                }
            }
        })
    }
}