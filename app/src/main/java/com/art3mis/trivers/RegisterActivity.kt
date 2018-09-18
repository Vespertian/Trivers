package com.art3mis.trivers

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.jetbrains.anko.alert

import android.app.AlertDialog
import android.graphics.Bitmap

import android.provider.MediaStore
import android.widget.*
import android.net.Uri
//import kotlinx.android.synthetic.main.activity_storage.*
import  android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.*

import android.os.Environment
import android.content.ClipDescription
import android.media.MediaScannerConnection
import java.util.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.*


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
    private lateinit var btnChoose:Button
    private lateinit var btnUpload:Button
    private lateinit var perfilImageView:ImageView
    private lateinit var name: String
    private lateinit var lastName: String
    private lateinit var age: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var rangoMinimo: String
    private lateinit var rangoMaximo: String
    private lateinit var description: String

    private val TAG = "StorageActivity"
    private val GALLERY = 1
    private val CAMERA = 2

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

        btnChoose = findViewById(R.id.btnChoose)
        perfilImageView = findViewById(R.id.imageView)
        progessBar = findViewById(R.id.progressBar)
        btnChoose.setOnClickListener{showPictureDialog()}
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        dbreference = database.reference.child("User")
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

                            val userBD = dbreference.child(user?.uid!!)
                            userBD.child("Name").setValue(name)
                            userBD.child("lastName").setValue(lastName)
                            userBD.child("age").setValue(age)
                            userBD.child("rangoMinimo").setValue(rangoMinimo)
                            userBD.child("rangoMaximo").setValue(rangoMaximo)
                            userBD.child("description").setValue(description)

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

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Escoger:")
        val pictureDialogItems = arrayOf("Galería", "Cámara")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {//guardar foto
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val path = saveImage(bitmap)
                    Toast.makeText(this@RegisterActivity, "¡Listo!", Toast.LENGTH_SHORT).show()
                    perfilImageView!!.setImageBitmap(bitmap)

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@RegisterActivity, "Hubo un error...", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            perfilImageView!!.setImageBitmap(thumbnail)
            saveImage(thumbnail)
            Toast.makeText(this@RegisterActivity, "¡Listo!", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() + "imag1")
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {
            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                    .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                    arrayOf(f.getPath()),
                    arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }
}
