package com.art3mis.trivers

//import android.support.v7.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.jetbrains.anko.alert
import java.io.ByteArrayOutputStream
import java.io.IOException


class RegisterActivity : AppCompatActivity(), TextWatcher {


    private lateinit var editText_Name: EditText
    private lateinit var editText_LastName: EditText
    private lateinit var editText_Age: EditText
    private lateinit var editText_PhoneNumber: EditText
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
    private lateinit var phoneNumber: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var rangoMinimo: String
    private lateinit var rangoMaximo: String
    private lateinit var description: String
    private lateinit var imgView: ImageView
    private lateinit var GMas: RadioButton
    private lateinit var GFem: RadioButton
    private lateinit var GMasB: RadioButton
    private lateinit var GFemB: RadioButton
    private val GALLERY =1
    private val CAMARA =2
    private var fileUri: Uri? =null
    private var bitmap: Bitmap? = null
    private lateinit var imageReference: StorageReference
    private val TAG = "RegisterActivity"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        editText_Name = findViewById(R.id.editText_Name)
        editText_LastName = findViewById(R.id.editText_LastName)
        editText_Age = findViewById(R.id.editText_Age)
        editText_PhoneNumber = findViewById(R.id.editText_PhoneNumber)
        editText_EmailR = findViewById(R.id.editText_EmailR)
        editText_PasswordR = findViewById(R.id.editText_PasswordR)
        editText_RangoMinimo = findViewById(R.id.editText_RangoMinimo)
        editText_RangoMaximo = findViewById(R.id.editText_RangoMaximo)
        editText_Description = findViewById(R.id.editText_Description)
        GMas = findViewById(R.id.GMas)
        GFem = findViewById(R.id.GFem)
        GMasB = findViewById(R.id.GMasB)
        GFemB = findViewById(R.id.GFemB)
        editText_Name.addTextChangedListener(this)
        editText_LastName.addTextChangedListener(this)
        editText_Age.addTextChangedListener(this)
        editText_PhoneNumber.addTextChangedListener(this)
        editText_EmailR.addTextChangedListener(this)
        editText_PasswordR.addTextChangedListener(this)
        editText_RangoMinimo.addTextChangedListener(this)
        editText_RangoMaximo.addTextChangedListener(this)
        editText_Description.addTextChangedListener(this)

        progessBar = findViewById(R.id.progressBar)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

//        imageReference=FirebaseStorage.getInstance().reference.child("imagenes")

        dbreference = database.reference.child("User")
        imgView=findViewById(R.id.imageView)
        name = ""
        lastName = ""
        age = ""
        phoneNumber = ""
        email = ""
        password = ""
        rangoMinimo = ""
        rangoMaximo = ""
        description = ""

        if (intent.getBooleanExtra("Google_Login", false)) {
            editText_Name.visibility = View.GONE
            editText_LastName.visibility = View.GONE
            if (intent.getBooleanExtra("Phone", false)) {
                editText_PhoneNumber.visibility = View.GONE
            } else {
                editText_PhoneNumber.visibility = View.VISIBLE
            }
            editText_EmailR.visibility = View.GONE
            editText_PasswordR.visibility = View.GONE
        }
    }

    fun register(view: View){
        createNewAccount()
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        name = editText_Name.text.toString()
        lastName = editText_LastName.text.toString()
        age = editText_Age.text.toString()
        phoneNumber = editText_PhoneNumber.text.toString()
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
        phoneNumber = ""
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
    private fun validatePassword(pass:String):Boolean{
        val num=pass.length
        if(num>=6)
            return true
        alert("Contraseña demasiado corta") {
            title("Error")
            okButton {}
        }.show()
        return false
    }

    fun actionLoginActivity(){
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun createNewAccount(){
        if (intent.getBooleanExtra("Google_Login", false)){
            if(!age.isEmpty()&&!rangoMinimo.isEmpty()&&!rangoMaximo.isEmpty()&&!description.isEmpty()){
                if (age.toInt()<18){
                    auth.currentUser!!.delete()
                    alert("Por favor no sigas intentando") {
                        title("Error")
                        okButton {actionLoginActivity()}
                    }.show()
                } else{
                    val user = auth.currentUser
                    val nombreImg= user!!.email+"_pFoto"
                    if (intent.getBooleanExtra("Phone", false)){
                        phoneNumber = user.phoneNumber.toString()
                    }
                    if(rangoMinimo.toInt()<18){
                        rangoMinimo="18"
                    }
                    if(rangoMaximo.toInt()<18){
                        rangoMaximo="18"
                    }
                    if(fileUri!=null){
                        imageReference = FirebaseStorage.getInstance().reference.child("imagenes")

                        val baos = ByteArrayOutputStream()
                        bitmap!!.compress(Bitmap.CompressFormat.JPEG,50,baos)
                        val data:ByteArray=baos.toByteArray()

                        val fileRef = imageReference!!.child(nombreImg + "." + extension())
                        fileRef.putBytes(data)
                                .addOnSuccessListener { taskSnapshot ->
                                    //                                            Log.e(TAG, "Uri: " + taskSnapshot.downloadUrl)
                                    Log.e(TAG, "Name: " + taskSnapshot.metadata!!.name)
                                    Toast.makeText(this, "File Uploaded", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                                }

                        val gen = when {
                            GMas.isChecked -> "Hombre"
                            GFem.isChecked -> "Mujer"
                            else -> "Nada"
                        }
                        val genB = when {
                            GMasB.isChecked -> "Hombres"
                            GFemB.isChecked -> "Mujeres"
                            else -> "Nada"
                        }
                        information().registerInformation(user!!, user.displayName!!, "NoLastName", user.email!!, age, phoneNumber, rangoMinimo, rangoMaximo, description,nombreImg + "." + extension(), gen, genB)
                        alert {
                            title("Registro completado")
                            okButton {action_PrivateProfile()}
                        }.show()
                    }else {
                        val gen = when {
                            GMas.isChecked -> "Hombre"
                            GFem.isChecked -> "Mujer"
                            else -> "Nada"
                        }
                        val genB = when {
                            GMasB.isChecked -> "Hombres"
                            GFemB.isChecked -> "Mujeres"
                            else -> "Nada"
                        }
                        information().registerInformation(user!!, user.displayName!!, "NoLastName", user.email!!, age, phoneNumber, rangoMinimo, rangoMaximo, description, gen, genB)
                        alert {
                            title("Registro completado")
                            okButton {action_PrivateProfile()}
                        }.show()
                    }
                }
            } else{
                alert("Por favor ingresa todos los datos para poder continuar") {
                    title("Datos incompletos")
                    yesButton {}
                }.show()
            }
        } else{
            if (!name.isEmpty()&&!lastName.isEmpty()&&!age.isEmpty()&&!email.isEmpty()&&!password.isEmpty()&&!rangoMinimo.isEmpty()&&!rangoMaximo.isEmpty()&&!description.isEmpty()){
                if(age.toInt()<18){
                    auth.currentUser!!.delete()
                    alert("Por favor no sigas intentando") {
                        title("Error")
                        okButton {actionLoginActivity()}
                    }.show()
                } else{
                    if (validateEmail(email)&&validatePassword(password)){
                        progessBar.visibility = View.VISIBLE
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){
                            task ->

                            if (task.isComplete){
                                val user:FirebaseUser? = auth.currentUser
                                verifyEmail(user)

                                if(rangoMinimo.toInt()<18){
                                    rangoMinimo="18"
                                }
                                if(rangoMaximo.toInt()<18){
                                    rangoMaximo="18"
                                }

                                val nombreImg= user!!.email.toString()+"_pFoto"
                                if(fileUri!=null){
                                    imageReference = FirebaseStorage.getInstance().reference.child("imagenes")

                                    val baos = ByteArrayOutputStream()
                                    bitmap!!.compress(Bitmap.CompressFormat.JPEG,50,baos)
                                    val data:ByteArray=baos.toByteArray()

                                    val fileRef = imageReference!!.child(nombreImg + "." + extension())
                                    fileRef.putBytes(data)
                                            .addOnSuccessListener { taskSnapshot ->
                                                //                                            Log.e(TAG, "Uri: " + taskSnapshot.downloadUrl)
                                                Log.e(TAG, "Name: " + taskSnapshot.metadata!!.name)
                                                Toast.makeText(this, "File Uploaded ", Toast.LENGTH_LONG).show()
                                            }
                                            .addOnFailureListener { exception ->
                                                Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                                            }
                                    val gen = when {
                                        GMas.isChecked -> "Hombre"
                                        GFem.isChecked -> "Mujer"
                                        else -> "Nada"
                                    }
                                    val genB = when {
                                        GMasB.isChecked -> "Hombres"
                                        GFemB.isChecked -> "Mujeres"
                                        else -> "Nada"
                                    }
                                    information().registerInformation(user!!, name, lastName, email, age, phoneNumber, rangoMinimo, rangoMaximo, description,nombreImg + "." + extension(), gen, genB)
                                }else {
                                    val gen = when {
                                        GMas.isChecked -> "Hombre"
                                        GFem.isChecked -> "Mujer"
                                        else -> "Nada"
                                    }
                                    val genB = when {
                                        GMasB.isChecked -> "Hombres"
                                        GFemB.isChecked -> "Mujeres"
                                        else -> "Nada"
                                    }
                                    information().registerInformation(user!!, name, lastName, email, age, phoneNumber, rangoMinimo, rangoMaximo, description, gen, genB)
                                    alert("Por favor verifica tu correo electrónico para poder Iniciar Sesión") {
                                        title("Registro completado")
                                        okButton {actionLoginActivity()}
                                    }.show()
                                }
                            } else{
                                alert("No se pudo crear la cuenta") {
                                    title("Error")
                                    okButton {}
                                }.show()
                            }
                            progessBar.visibility = View.INVISIBLE
                        }
                    } else if(!validateEmail(email)){
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

    }

    fun action_PrivateProfile(){
        startActivity(Intent(this, PrivateProfileActivity::class.java))
    }

    fun escoger(view: View){
        escogerI()
    }
    private fun escogerI(){
        val intent =Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        val pDialog = AlertDialog.Builder(this)
        pDialog.setTitle("Seleccionar")
        val pDialogItem= arrayOf("Galería","Camara")
        pDialog.setItems(pDialogItem)
        {dialog, which ->
            when(which){
                0 ->escogerImg()
                1 ->tomarImg()
            }
        }
        pDialog.create().show()
    }
    private fun tomarImg() {
        Toast.makeText(this,"Sin implementar... Próximamente",Toast.LENGTH_SHORT).show()
        /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,CAMARA)*/
    }
    private fun escogerImg() {
        val galeriaIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galeriaIntent, GALLERY)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY){
            if (data!=null){
                fileUri = data.data
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,fileUri)
                    imgView.setImageBitmap(bitmap)

                }catch (e: IOException){
                    e.printStackTrace()
                    Toast.makeText(this,"¡Error!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if(requestCode== CAMARA){
            if (data!=null){//Hay que guardar la imagen y vovler a buscarla para subirla... FALTA TODO ESO.
                try{
                    bitmap= data!!.extras!!.get("data") as Bitmap
                    imgView.setImageBitmap(bitmap)
                }catch (e: IOException){
                    e.printStackTrace()
                    Toast.makeText(this,"¡Error!", Toast.LENGTH_SHORT).show()
                }


            }

        }
    }

    private fun extension():String{
        val cR=contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(fileUri))
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
