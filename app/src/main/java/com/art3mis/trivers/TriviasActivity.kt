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
        recicler_view=findViewById(R.id.recicler_view)
        CargarTrivias()
    }

    override fun onRestart() {
        super.onRestart()
        recreate()
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
                            Toast.makeText(baseContext,"Elige la Trivia",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(baseContext,"No hay Trivias Disponibles",Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        })
    }

    fun Responder(view: View){
        Responder(dbRefTrivia.child(view.nombreTrivia.text.toString()))
    }
    private fun Responder(dbTrivia:DatabaseReference){
        val intent = Intent(this, PreguntasActivity()::class.java)
        intent.putExtra("subTematica",intent2.getStringExtra("Tematica")+"/"+intent2.getStringExtra("subTematica"))
        intent.putExtra("Trivia",intent2.getStringExtra("Tematica")+"/"+intent2.getStringExtra("subTematica")+"/"+dbTrivia.key)
        startActivity(intent)
    }
}