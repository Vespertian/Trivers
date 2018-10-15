package com.art3mis.trivers
import android.content.Intent
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_preguntas.*
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.art3mis.trivers.adaptador.AdaptadorPreguntas
import com.art3mis.trivers.modelos.Item_Preguntas
import com.google.firebase.database.*
import java.util.*

class PreguntasActivity:AppCompatActivity() {

    var itemsPreguntas:MutableList<Item_Preguntas> =ArrayList()
    private lateinit var adpTr:AdaptadorPreguntas
    private lateinit var usuario: FirebaseUser
    private lateinit var uAuth: FirebaseAuth
    private val activity=this
    private lateinit var dbUserRef: DatabaseReference
    private lateinit var database:FirebaseDatabase
    private lateinit var dbRefTrivia:DatabaseReference
    private lateinit var recicler_view:RecyclerView
    private lateinit var intent2: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preguntas)
        intent2=getIntent()
        uAuth= FirebaseAuth.getInstance()
        usuario= uAuth.currentUser!!
        database= FirebaseDatabase.getInstance()
        dbUserRef=database.getReference("Users").child(usuario.uid)
        dbRefTrivia=database.getReference("Trivias").child(intent2.getStringExtra("Trivia"))
        recicler_view= this.findViewById(R.id.recicler_view)
        nombreTrivia.text=dbRefTrivia.key.toString()
        CargarPreguntas()
    }

    private fun CargarPreguntas(){
        recicler_view.layoutManager =LinearLayoutManager(this)
        dbRefTrivia.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dS: DataSnapshot) {
                for (i in dS.children){
                    val pregunta=i.child("Pregunta").value.toString()
                    val respuesta=i.child("RC").value.toString()
                    val respuesta1=i.child("R1").value.toString()
                    val respuesta2=i.child("R2").value.toString()
                    val respuesta3=i.child("R3").value.toString()
                    val respuesta4=i.child("R4").value.toString()
                    val item=Item_Preguntas(pregunta,respuesta1,respuesta2,respuesta3,respuesta4,respuesta)
                    itemsPreguntas.add(item)
                }
                adpTr  = AdaptadorPreguntas(recicler_view, activity, itemsPreguntas)
                recicler_view.adapter = adpTr
            }
        })
    }
    fun Responder(view: View){
        var punt=0
        var r=true
        for(i in 0..(recicler_view.childCount)-1){
            val rc=recicler_view.getChildAt(i).findViewById<TextView>(R.id.respuesta).text
            val rBG=recicler_view.getChildAt(i).findViewById<RadioGroup>(R.id.Respuestas)
            val rD = rBG.findViewById<RadioButton>(rBG.checkedRadioButtonId)
            val rCo=when(rc){
                "R1" ->rBG.findViewById<RadioButton>(R.id.R1)
                "R2" ->rBG.findViewById<RadioButton>(R.id.R2)
                "R3" ->rBG.findViewById<RadioButton>(R.id.R3)
                else ->rBG.findViewById<RadioButton>(R.id.R4)
            }
            if(rD!=null){
                if(rCo==rD){
                    punt++
                }
            }else{
                r=false
                break
            }
//            Toast.makeText(baseContext,rD.text.toString(),Toast.LENGTH_SHORT).show()
        }
        if(r && recicler_view.childCount!=0){
            val pt=recicler_view.childCount
            Toast.makeText(this,((punt.toDouble()/pt)*100).toString(),Toast.LENGTH_SHORT).show()
            dbUserRef.child("Trivias/"+intent2.getStringExtra("Trivia")).setValue(((punt.toDouble()/pt)*100).toInt().toString()+"%")
        }else{
            Toast.makeText(baseContext,"No ha respondido todas las preguntas",Toast.LENGTH_SHORT).show()
        }
    }
}