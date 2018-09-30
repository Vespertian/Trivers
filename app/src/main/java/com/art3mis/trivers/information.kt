package com.art3mis.trivers

import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import org.jetbrains.anko.alert

open class information: AppCompatActivity() {
    private lateinit var dbreference: DatabaseReference
    private lateinit var userID: String
    private lateinit var userInformation: String

    fun registerInformation(user: FirebaseUser, name: String, lastName: String,email:String, age: String, phoneNumber: String, rangoMinimo: String, rangoMaximo: String, description: String,fPerfil:String){
        dbreference = FirebaseDatabase.getInstance().getReference("Users")
        val arrayList = arrayListOf<String>()
        arrayList.add(name)
        arrayList.add(lastName)
        arrayList.add(age)
        arrayList.add(email)
        arrayList.add(phoneNumber)
        arrayList.add(rangoMinimo)
        arrayList.add(rangoMaximo)
        arrayList.add(description)
        arrayList.add(fPerfil)
        dbreference.child(user!!.uid).setValue(arrayList).addOnCompleteListener(this) {
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
        val arrayList = arrayListOf<String>()
        arrayList.add(name)
        arrayList.add(lastName)
        arrayList.add(age)
        arrayList.add(email)
        arrayList.add(phoneNumber)
        arrayList.add(rangoMinimo)
        arrayList.add(rangoMaximo)
        arrayList.add(description)
        dbreference.child(user!!.uid).setValue(arrayList).addOnCompleteListener(this) {
            task ->

            if (!task.isComplete){
                alert("No se ha podido crear tu cuenta") {
                    title("Error de registro")
                    okButton {}
                }.show()
            }
        }
    }

    fun getUserInformation(email: String, password: String, auth: FirebaseAuth){
        userID = auth.currentUser!!.uid
        dbreference = FirebaseDatabase.getInstance().getReference(userID)
        // Read from the database
        dbreference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userInformation = dataSnapshot.getValue(String::class.java)!!
                alert(userInformation) {
                    title("Información de usuario:")
                    okButton {}
                }.show()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }
}