package com.art3mis.trivers

import android.content.ClipDescription
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.jetbrains.anko.alert
import java.io.IOException
import android.widget.EditText
import android.widget.ProgressBar
import org.jetbrains.anko.alert

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.*

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream


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
/*    private lateinit var btnChoose: Button
    private lateinit var imgView: ImageView
    private val GALLERY =1
    private val CAMARA =2
    private var fileURI:Uri? =null
    private var bitmap: Bitmap? = null
    private var imageReference: StorageReference? = null
    private val TAG = "RegisterActivity"*/




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
//        imageReference = FirebaseStorage.getInstance().reference.child("images")
//        dbreference = database.reference.child("User")

        name = ""
        lastName = ""
        age = ""
        email = ""
        password = ""
        rangoMinimo = ""
        rangoMaximo = ""
        description = ""
        /*btnChoose =findViewById(R.id.btnChoose)
        imgView=findViewById(R.id.imageView)
        btnChoose!!.setOnClickListener{mostrarImgs()}*/

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

                            information().registerInformation(user!!, name, lastName, age, rangoMinimo, rangoMaximo, description)

                            /*if(fileURI!=null){
                                val nombreImg=user!!.toString()+"pFoto"
                                val baos =ByteArrayOutputStream()
                                bitmap!!.compress(Bitmap.CompressFormat.JPEG,50,baos)
                                val data:ByteArray=baos.toByteArray()

                                val fileRef = imageReference!!.child(nombreImg + "." + getFileExtension(fileURI!!))
                                fileRef.putBytes(data)
                                        .addOnSuccessListener { taskSnapshot ->
                                            Log.e(TAG, "Uri: " + taskSnapshot.downloadUrl)
                                            Log.e(TAG, "Name: " + taskSnapshot.metadata!!.name)
                                            Toast.makeText(this, "File Uploaded ", Toast.LENGTH_LONG).show()
                                        }
                                        .addOnFailureListener { exception ->
                                            Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                                        }

                            }*/

                            alert("Por favor verifica tu correo electrónico para poder Iniciar Sesión") {
                                title("Registro completado")
                                okButton {actionLoginActivity()}
                            }.show()
                        } else{
                            alert("No se pudo crear la cuenta") {
                                title("Error")
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

 /*   private fun mostrarImgs() {
        val pDialog = AlertDialog.Builder(this)
        pDialog.setTitle("Seleccionar")
        val pDialogItem= arrayOf("Galería","Camara")
        pDialog.setItems(pDialogItem){
            dialog, which -> when(which){
            0 ->escogerImg()
            1 ->tomarImg()
        }

        }
    }

    private fun tomarImg() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,CAMARA)
    }

    private fun escogerImg() {
        val galeriaIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galeriaIntent, GALLERY)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY){
            if (data!=null){
                fileURI = data.data
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,fileURI)
                    imgView.setImageBitmap(bitmap)

                }catch (e:IOException){
                    e.printStackTrace()
                    Toast.makeText(this@RegisterActivity,"¡Error!",Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if(requestCode== CAMARA){
            bitmap= data!!.extras!!.get("data") as Bitmap
            imgView.setImageBitmap(bitmap)
        }
    }

    private fun getFileExtension(uri: Uri): String {
        val contentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }*/

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
