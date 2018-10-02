package com.art3mis.trivers

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
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
import kotlinx.android.synthetic.main.activity_private_profile.view.*
import org.jetbrains.anko.alert


open class PrivateProfileActivity : AppCompatActivity() {
    //private val TAG="PrivateProfileActivity"
    private lateinit var imageReference: StorageReference
    private lateinit var usuario:FirebaseUser
    private lateinit var uAuth:FirebaseAuth
    private lateinit var database:FirebaseDatabase
    private lateinit var dbRef:DatabaseReference
    private lateinit var tNombre:TextView
    private lateinit var imgPerfil:ImageView
    private lateinit var eDescripcion:TextView
    private lateinit var eEmail:TextView
    private lateinit var eEdad:TextView
    private lateinit var eTelefono:TextView
    private lateinit var eRMinimo:TextView
    private lateinit var eRMaximo:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_profile)
        uAuth= FirebaseAuth.getInstance()
        usuario= uAuth.currentUser!!
        database= FirebaseDatabase.getInstance()
        dbRef=database.getReference("Users").child(usuario.uid)
        tNombre=findViewById(R.id.tNombre)
        imgPerfil=findViewById(R.id.imgPerfil)
        eDescripcion=findViewById(R.id.eDescripcion)
        eEdad=findViewById(R.id.eEdad)
        eEmail=findViewById(R.id.eEmail)
        eTelefono=findViewById(R.id.eTelefono)
        eRMinimo=findViewById(R.id.eRMinimo)
        eRMaximo=findViewById(R.id.eRMaximo)
        actualizar()
    }

    private fun actualizar(){
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dS: DataSnapshot) {
                if (dS.child("lastName").value.toString() != "NoLastName"){
                    tNombre.text = dS.child("name").value.toString()+" "+dS.child("lastName").value.toString()
                } else{
                    tNombre.text = dS.child("name").value.toString()
                }
                eDescripcion.text = dS.child("description").value.toString()
                eEdad.text = dS.child("age").value.toString()
                eEmail.text = dS.child("email").value.toString()
                eTelefono.text = dS.child("phoneNumber").value.toString()
                eRMinimo.text = dS.child("rangoMinimo").value.toString()
                eRMaximo.text = dS.child("rangoMaximo").value.toString()
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
}
