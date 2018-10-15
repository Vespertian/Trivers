package com.art3mis.trivers

import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.art3mis.trivers.adaptador.AdaptadorTrivias
import com.art3mis.trivers.modelos.Item_Trivias
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.item_trivias.view.*
import java.util.*

class TriviasActivity: AppCompatActivity() {
    var itemsTrivias:MutableList<Item_Trivias> =ArrayList()
    private lateinit var adpTr:AdaptadorTrivias
    private lateinit var usuario: FirebaseUser
    private lateinit var uAuth: FirebaseAuth
    private val activity=this
    private lateinit var dbUserRef: DatabaseReference
    private lateinit var database:FirebaseDatabase
    private lateinit var dbRefTrivia:DatabaseReference
    private lateinit var recicler_view:RecyclerView
    private lateinit var intent2:Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trivias)
        intent2=getIntent()
        uAuth= FirebaseAuth.getInstance()
        usuario= uAuth.currentUser!!
        database= FirebaseDatabase.getInstance()
        dbUserRef=database.getReference("Users").child(usuario.uid)
        dbRefTrivia=database.getReference("Trivias").child(intent2.getStringExtra("Tematica")).child(intent.getStringExtra("subTematica"))
        //Toast.makeText(this,dbRefTrivia.key.toString(),Toast.LENGTH_SHORT).show()//Listo! Faltan los Viewers de las trivias!! Y La Trivia en sí!!! PEro litoieowijr
        recicler_view=findViewById(R.id.recicler_view)
        CargarTrivias()
    }

    private fun CargarTrivias(){
        recicler_view.layoutManager =LinearLayoutManager(this)
        dbRefTrivia.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dSTrivia: DataSnapshot) {
                dbUserRef.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(dSUser: DataSnapshot) {
                        for(d in dSTrivia.children){
                            Toast.makeText(baseContext,dSUser.child("Trivias").exists().toString(),Toast.LENGTH_SHORT).show()

                            if(!dSUser.child("Trivias/"+intent2.getStringExtra("Tematica")+"/"+intent2.getStringExtra("subTematica")).hasChild(d.key.toString())){
                                val nom=d.key
                                val num=d.childrenCount.toString()
                                val item=Item_Trivias(nom,num)
                                itemsTrivias.add(item)
                            }
                        }
                        if(itemsTrivias.isNotEmpty()) {
                            adpTr = AdaptadorTrivias(recicler_view, activity, itemsTrivias)
                            recicler_view.adapter = adpTr
                        }
                    }
                })
            }
        })
    }

    fun Responder(view: View){
//        Toast.makeText(this,view.nombreTrivia.text.toString(),Toast.LENGTH_SHORT).show()
        Responder(dbRefTrivia.child(view.nombreTrivia.text.toString()))
    }
    private fun Responder(dbTrivia:DatabaseReference){
        val intent = Intent(this, PreguntasActivity()::class.java)
        intent.putExtra("Trivia",intent2.getStringExtra("Tematica")+"/"+intent2.getStringExtra("subTematica")+"/"+dbTrivia.key)
        Toast.makeText(this,intent2.getStringExtra("Tematica")+"/"+intent2.getStringExtra("subTematica")+"/"+dbTrivia.key,Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }
}