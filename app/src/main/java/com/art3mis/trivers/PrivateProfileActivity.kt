package com.art3mis.trivers

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference



open class PrivateProfileActivity : AppCompatActivity() {
    private val TAG="PrivateProfileActivity"
    private lateinit var imageReference: StorageReference
    private lateinit var usuario:FirebaseUser
    private lateinit var uAuth:FirebaseAuth
    private lateinit var database:FirebaseDatabase
    private lateinit var dbRef:DatabaseReference
    private lateinit var tNombre:TextView
    private lateinit var imgPerfil:ImageView
    private lateinit var eNombreC:TextView
    private lateinit var eDescripcion:EditText
    private lateinit var eEmail:EditText
    private lateinit var eEdad:EditText
    private lateinit var eTelefono:EditText
    private lateinit var eRMinimo:EditText
    private lateinit var eRMaximo:EditText
    private var userInformation: MutableMap<String, Any>? = HashMap()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_profile)
        uAuth= FirebaseAuth.getInstance()
        usuario= uAuth.currentUser!!
        database= FirebaseDatabase.getInstance()
        dbRef=database.getReference("Users").child(usuario.uid)
//        imageReference=FirebaseStorage.getInstance().reference.child("imagenes/"+dbRef.child("fPerfil").toString())
        tNombre=findViewById(R.id.tNombre)
        imgPerfil=findViewById(R.id.imgPerfil)
        eNombreC=findViewById(R.id.eNombreC)
        eDescripcion=findViewById(R.id.eDescripcion)
        eEdad=findViewById(R.id.eEdad)
        eEmail=findViewById(R.id.eEmail)
        eTelefono=findViewById(R.id.eTelefono)
        eRMinimo=findViewById(R.id.eRMaximo)
        eRMaximo=findViewById(R.id.eRMinimo)
        actualizar()
    }

    private fun actualizar(){
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dS: DataSnapshot) {
                tNombre.text=dS.child("name").value.toString()
                eNombreC.setText(dS.child("name").value.toString()+" "+dS.child("lastName").value.toString())
                eDescripcion.setText(dS.child("description").value.toString())
                eEdad.setText(dS.child("age").value.toString())
                eEmail.setText(dS.child("email").value.toString())
                eTelefono.setText(dS.child("phoneNumber").value.toString())
                eRMinimo.setText(dS.child("rangoMinimo").value.toString())
                eRMaximo.setText(dS.child("rangoMaximo").value.toString())
                imageReference=FirebaseStorage.getInstance().reference.child("imagenes/"+dS.child("fPerfi").value.toString())

                if(imageReference.toString()!=" "){
                    val ONE_MEGABYTE=(1024*1024).toLong()
                    imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                        val bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                        imgPerfil.setImageBitmap(bmp)
                    }.addOnFailureListener{exception ->
                        Toast.makeText(this@PrivateProfileActivity,"error",Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this@PrivateProfileActivity,"No tienes foto de perfil subida",Toast.LENGTH_LONG).show()
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    fun cambiarEmail(view: View){
        cambiarEmail()
    }
    private fun cambiarEmail(){

    }

    fun cambiarEdad(view: View){
        cambiarEdad()
    }
    private fun cambiarEdad(){

    }

    fun cambiarDesc(view: View){
        cambiarDesc()
    }
    private fun cambiarDesc(){

    }

    fun cambiarTelefono(view: View){
        cambiarTelefono()
    }
    private fun cambiarTelefono(){

    }

    fun cambiarRMinimo(view: View){
        cambiarRMinimo()
    }
    private fun cambiarRMinimo(){

    }

    fun cambiarRMaximo(view: View){
        cambiarRMaximo()
    }
    private fun cambiarRMaximo(){

    }
     fun cambiarImg(view:View){
        CambiarImagen()
    }
    private fun CambiarImagen(){

    }
}
