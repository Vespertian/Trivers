package com.art3mis.trivers

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_private_profile.*
import org.jetbrains.anko.alert
import java.io.ByteArrayOutputStream
import java.io.IOException


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
    private lateinit var eEdadB:Button
    private lateinit var eEdadC:EditText
    private lateinit var eEdadCB:Button
    private lateinit var sexo: TextView
    private lateinit var sexoB: TextView
    private lateinit var navigation: BottomNavigationView
    private var fileUri: Uri? =null
    private var bitmap: Bitmap? = null
    private val GALLERY=1

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
        eEdadB=findViewById(R.id.eEdadB)
        eEdadC=findViewById(R.id.eEdadC)
        eEdadCB=findViewById(R.id.eEdadCB)
        sexo = findViewById(R.id.Sexo)
        sexoB = findViewById(R.id.SexoB)
        navigation = findViewById(R.id.navigation)
        navigation()
        actualizar()
    }

    fun navigation(){
        navigation.selectedItemId = R.id.navigation_profile
        navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when {
                item.itemId == R.id.navigation_profile -> startActivity(Intent(this@PrivateProfileActivity, PrivateProfileActivity::class.java))
                item.itemId == R.id.navigation_match -> startActivity(Intent(this@PrivateProfileActivity, MatchActivity::class.java))
                item.itemId == R.id.navigation_trivias -> startActivity(Intent(this@PrivateProfileActivity, TriviasTemasActivity::class.java))
                item.itemId != null -> return@OnNavigationItemSelectedListener true
            }
            false
        })
    }

    private fun setupActionBar(){
        var actionBar: ActionBar = this.supportActionBar!!
        if(intent.getStringExtra("uid") != null) {
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.title = "Perfil público"
            }
        } else{
            actionBar.title = "Perfil privado"
        }
    }

    private fun actualizar(){
        setupActionBar()
        if(intent.getStringExtra("uid") != null){
            setPublicUser(intent.getStringExtra("uid"))
        } else{
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dS: DataSnapshot) {
                    if (dS.child("lastName").value.toString() != "NoLastName"){
                        tNombre.text = dS.child("name").value.toString()+" "+dS.child("lastName").value.toString()
                    } else{
                        tNombre.text = dS.child("name").value.toString()
                    }
                    eDescripcion.text = dS.child("description").value.toString()
                    eDescripcionC.setText(dS.child("description").value.toString())
                    eEdad.text = dS.child("age").value.toString()
                    eEmail.text = dS.child("email").value.toString()
                    eEdadC.setText( dS.child("age").value.toString())
                    eTelefono.text = dS.child("phoneNumber").value.toString()
                    eTelefonoC.setText(dS.child("phoneNumber").value.toString())
                    eRMinimo.text = dS.child("rangoMinimo").value.toString()
                    eRMinimoE.setText(dS.child("rangoMinimo").value.toString())
                    eRMaximo.text = dS.child("rangoMaximo").value.toString()
                    if(dS.child("GeneroB").value.toString() == "Masculino"){
                        sexoB.text = "Hombres"
                    } else if (dS.child("GeneroB").value.toString() == "Femenino"){
                        sexoB.text = "Mujeres"
                    }
                    sexo.text = dS.child("Genero").value.toString()
                    eRMaximoE.setText(dS.child("rangoMaximo").value.toString())
                    imageReference=FirebaseStorage.getInstance().reference.child("imagenes/"+dS.child("fPerfi").value.toString())
                    if(imageReference.toString()!=""){
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
            navigation.visibility = View.VISIBLE
        }
    }

    fun eFoto(view: View){
        if (intent.getStringExtra("uid") == null){
            alert("¡Se eliminará la foto actual!") {
                title("¡Cuidado!")
                okButton {subirFotoP()}//Sin implementar...
                noButton()
            }.show()
        }
    }
    private fun subirFotoP(){
        val galeriaIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galeriaIntent,GALLERY)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==GALLERY){
            if (data!=null){
                fileUri = data.data
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,fileUri)
                    imgPerfil.setImageBitmap(bitmap)
                    subirImg()
                }catch (e: IOException){
                    e.printStackTrace()
                    Toast.makeText(this,"¡Error!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun subirImg(){
        val baos = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG,50,baos)
        val data:ByteArray=baos.toByteArray()
        imageReference.delete().addOnSuccessListener { aVoid ->
            actualizar()
        }
        val fileRef =  FirebaseStorage.getInstance().reference.child("imagenes").child(eEmail.text.toString()+"_pFoto."+extension())
        fileRef.putBytes(data)
                .addOnSuccessListener { taskSnapshot ->
                    //                                            Log.e(TAG, "Uri: " + taskSnapshot.downloadUrl)
                    Toast.makeText(this, "File Uploaded ", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                }
    }
    private fun extension():String{
        val cR=contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(fileUri))
    }

    open fun setPublicUser(uid: String){
        navigation.visibility = View.GONE
        lyCorreo.visibility = View.GONE
        lyBuscas.visibility = View.GONE
        lyRango.visibility = View.GONE
        eDescripcionB.visibility = View.GONE
        eTelefonoB.visibility = View.GONE
        eEdadB.visibility = View.GONE
        eREdadesB.visibility = View.GONE
        FirebaseDatabase.getInstance().getReference("Users/$uid").addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dS: DataSnapshot) {
                if (dS.child("lastName").value.toString() != "NoLastName"){
                    tNombre.text = dS.child("name").value.toString()+" "+dS.child("lastName").value.toString()
                } else{
                    tNombre.text = dS.child("name").value.toString()
                }
                eDescripcion.text = dS.child("description").value.toString()
                eEdad.text = dS.child("age").value.toString()
                eEdadC.setText( dS.child("age").value.toString())
                eTelefono.text = dS.child("phoneNumber").value.toString()
                Sexo.text = dS.child("Genero").value.toString()
                imageReference=FirebaseStorage.getInstance().reference.child("imagenes/"+dS.child("fPerfi").value.toString())
                if(imageReference.toString()!=""){
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
        })
    }

    fun eEdadButton(view: View){
        navigation.visibility = View.GONE
        eEdadButton()
    }
    private fun eEdadButton(){
        eEdad.visibility=View.GONE
        eEdadB.visibility=View.GONE
        eEdadC.visibility=View.VISIBLE
        eEdadCB.visibility=View.VISIBLE
        cEdadB.visibility=View.VISIBLE
    }

    fun eEdadButtonC(view: View){
        eEdadButtonC()
    }
    private fun eEdadButtonC(){
        eEdad.visibility=View.VISIBLE
        eEdadB.visibility=View.VISIBLE
        eEdadC.visibility=View.GONE
        eEdadCB.visibility=View.GONE
        cEdadB.visibility=View.GONE
        dbRef.child("age").setValue(eEdadC.text.toString())
        actualizar()
    }

    fun cEdadB(view:View){
        navigation.visibility = View.VISIBLE
        cEdadB()
    }
    private fun cEdadB(){
        eEdad.visibility=View.VISIBLE
        eEdadB.visibility=View.VISIBLE
        eEdadC.visibility=View.GONE
        cEdadB.visibility=View.GONE
        eEdadCB.visibility=View.GONE
    }

    fun eDescrB(view: View){
        alert("Desea cambiar su descripcion") {
            title("¡Cuidado!")
            okButton {eDescrB()}
            noButton()
        }.show()
    }
    private fun eDescrB(){
        navigation.visibility = View.GONE
        eDescripcion.visibility=View.GONE
        eDescripcionB.visibility=View.GONE
        eDescripcionC.visibility=View.VISIBLE
        eDescripcionCB.visibility=View.VISIBLE
        cDescripcionB.visibility=View.VISIBLE
    }

    fun eDescrCB(view: View){
        eDescrCB()
    }
    private fun eDescrCB(){
        eDescripcion.visibility=View.VISIBLE
        eDescripcionB.visibility=View.VISIBLE
        cDescripcionB.visibility=View.GONE
        eDescripcionC.visibility=View.GONE
        eDescripcionCB.visibility=View.GONE
        dbRef.child("description").setValue(eDescripcionC.text.toString())
        actualizar()
    }

    fun cDescripcionB(view: View){
        navigation.visibility = View.VISIBLE
        cDescripcionB()
    }
    private fun cDescripcionB(){
        eDescripcion.visibility=View.VISIBLE
        eDescripcionB.visibility=View.VISIBLE
        eDescripcionC.visibility=View.GONE
        eDescripcionCB.visibility=View.GONE
        cDescripcionB.visibility=View.GONE
    }

    fun eTelB(view: View){
        navigation.visibility = View.GONE
        eTelB()
    }
    private fun eTelB( ){
        eTelefono.visibility=View.GONE
        eTelefonoB.visibility=View.GONE
        eTelefonoC.visibility=View.VISIBLE
        eTelefonoCB.visibility=View.VISIBLE
        cTelefonoB.visibility=View.VISIBLE
    }

    fun eTelCB(view: View){
        eTelCB()
    }
    private fun eTelCB(){
        eTelefono.visibility=View.VISIBLE
        eTelefonoB.visibility=View.VISIBLE
        eTelefonoC.visibility=View.GONE
        eTelefonoCB.visibility=View.GONE
        cTelefonoB.visibility=View.GONE
        dbRef.child("phoneNumber").setValue(eTelefonoC.text.toString())
        actualizar()
    }
    fun cTelefonoB(view: View){
        navigation.visibility = View.VISIBLE
        cTelefonoB()
    }
    private fun cTelefonoB(){
        eTelefono.visibility=View.VISIBLE
        eTelefonoB.visibility=View.VISIBLE
        eTelefonoC.visibility=View.GONE
        eTelefonoCB.visibility=View.GONE
        cTelefonoB.visibility=View.GONE
    }

    fun eREdadesB(view: View){
        navigation.visibility = View.GONE
        eREdadesB()
    }
    private fun eREdadesB(){
        eRMinimo.visibility=View.GONE
        eRMaximo.visibility=View.GONE
        eREdadesB.visibility=View.GONE
        eRMaximoE.visibility=View.VISIBLE
        eRMinimoE.visibility=View.VISIBLE
        eREdadesCB.visibility=View.VISIBLE
        cREdadesB.visibility=View.VISIBLE
    }

    fun eREdadesCB(view: View){
        eREdadesCB()
    }
    private fun eREdadesCB(){
        eRMinimo.visibility=View.VISIBLE
        eRMaximo.visibility=View.VISIBLE
        eREdadesB.visibility=View.VISIBLE
        eRMaximoE.visibility=View.GONE
        eRMinimoE.visibility=View.GONE
        eREdadesCB.visibility=View.GONE
        cREdadesB.visibility=View.GONE
        dbRef.child("rangoMinimo").setValue(eRMinimoE.text.toString())
        dbRef.child("rangoMaximo").setValue(eRMaximoE.text.toString())
        actualizar()
    }
    fun cREdadesB(view: View){
        navigation.visibility = View.VISIBLE
        cREdadesB()
    }
    private fun cREdadesB(){
        eRMinimo.visibility=View.VISIBLE
        eRMaximo.visibility=View.VISIBLE
        eREdadesB.visibility=View.VISIBLE
        eRMaximoE.visibility=View.GONE
        eRMinimoE.visibility=View.GONE
        eREdadesCB.visibility=View.GONE
        cREdadesB.visibility=View.GONE
    }

    fun PPContactar(view: View){

    }
}
