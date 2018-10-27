package com.art3mis.trivers

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.art3mis.trivers.adaptador.AdaptadorTemática
import com.art3mis.trivers.modelos.Item_Tematica
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.item_tematicas.view.*
import java.util.*

class TriviasTemasActivity:AppCompatActivity(){
    var itemTematicas:MutableList<Item_Tematica> =ArrayList()
    lateinit var adapter:AdaptadorTemática      //Para los Subtemas
    lateinit var adapterTematica: ArrayAdapter<CharSequence>         //Para las Tematicas
    private lateinit var spinnerTematicas: Spinner
    private val activity=this

    private lateinit var usuario: FirebaseUser
    private lateinit var uAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var dbUserRef: DatabaseReference
    private lateinit var dbTriviaRef:DatabaseReference
    private lateinit var recicler_view: RecyclerView
    private lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tematica)
        uAuth= FirebaseAuth.getInstance()
        usuario= uAuth.currentUser!!
        database= FirebaseDatabase.getInstance()
        dbUserRef=database.getReference("Users").child(usuario.uid)
        dbTriviaRef=database.getReference("Trivias")
        navigation = findViewById(R.id.navigation)
        navigation()
        spinnerTematicas=findViewById(R.id.spinnerTematicas)
        adapterTematica= ArrayAdapter(this,R.layout.simple_spiner_tematicas)
        adapterTematica.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        recicler_view=findViewById(R.id.recicler_view)

        //Cargando las tematicas de la trivia

        cargarTematicas()
        spinnerTematicas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(parent!!.getItemAtPosition(position).toString() != "Tematicas"){
                    cargarSubTematicas(parent!!.getItemAtPosition(position).toString())
                }else{ //recier agregado
                    if(itemTematicas.isNotEmpty()) {
                        itemTematicas.clear()
                        adapter= AdaptadorTemática(recicler_view,activity,itemTematicas)
                        recicler_view.adapter=adapter
                    }
                }
            }
        }
    }

    fun navigation(){
        navigation.selectedItemId = R.id.navigation_trivias
        navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when {
                item.itemId == R.id.navigation_profile -> startActivity(Intent(this@TriviasTemasActivity, PrivateProfileActivity::class.java))
                item.itemId == R.id.navigation_match -> startActivity(Intent(this@TriviasTemasActivity, MatchActivity::class.java))
                item.itemId == R.id.navigation_trivias -> startActivity(Intent(this@TriviasTemasActivity, TriviasTemasActivity::class.java))
                item.itemId != null -> return@OnNavigationItemSelectedListener true
            }
            false
        })
    }

    fun trivias(view: View){
        trivias(database.getReference("Trivias").child(spinnerTematicas.selectedItem.toString()).child(view.tematicaTrivias.text.toString()))
//        Toast.makeText(this,view.tematicaTrivias.text.toString(),Toast.LENGTH_LONG).show()
    }
    private fun trivias(dbTematica:DatabaseReference){
        val intent11 = Intent(this, TriviasActivity()::class.java)
        intent11.putExtra("subTematica",dbTematica.key)
        intent11.putExtra("Tematica",database.getReference("Trivias").child(spinnerTematicas.selectedItem.toString()).key.toString())
        startActivity(intent11)
    }

    private fun cargarTematicas() {
        dbTriviaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dS: DataSnapshot) {
                for (i in dS.children) {
                    adapterTematica.add(i.key.toString())
                }
                adapterTematica.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTematicas.adapter = adapterTematica
            }
        })
    }

    private fun cargarSubTematicas(sTem:String){
        val dbSTRef=database.getReference("Trivias/$sTem")
        recicler_view.layoutManager = LinearLayoutManager(this)
        itemTematicas.clear()

        dbSTRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dS: DataSnapshot) {
                dbUserRef.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        for(i in dS.children){
                            val tem: String = i.key.toString()
                            val numT: String = i.childrenCount.toString()
                            val numTR:String = p0.child("Trivias/$sTem").childrenCount.toString()
                            val item = Item_Tematica(tem, numT, numTR)
                            itemTematicas.add(item)
                        }
                        //Inicializando Vista
                        adapter= AdaptadorTemática(recicler_view,activity,itemTematicas)
                        recicler_view.adapter=adapter
                    }
                })
            }
        })
//        adapter.setCargarMas(this) //no funciona :v
    }
    /* //NADA DE ACÁ PA' ABAJO FUNCIONA!!! (No he querido arreglarlo... Algún día ='D)
    for(i in 0..6){
        val nom=UUID.randomUUID().toString()
        val item=Item_Tematica(nom,nom.length)
        itemTematicas.add(item)
    }*/

    /*override fun onCargarMas() {
val totalTemas=totTemas.text.toString().toInt()
val nTemaActual=contTemas.text.toString().toInt()
if(itemTematicas!!.size < totalTemas) //Máximo número de temáticas
{
    itemTematicas!!.add(null)
    adapter.notifyItemInserted(itemTematicas.size-1)
    //Correr Hilo
    Handler().postDelayed({
        itemTematicas.removeAt(itemTematicas.size-1) //remueve nulos
        adapter.notifyItemRemoved(itemTematicas.size)
        //Insertar Valores
        val index =itemTematicas.size
        val end =index+5
        //Ejemplo
        *//*for(i in index until end){
                    val nom= UUID.randomUUID().toString()
                    val item = Item_Tematica(nom,"s")
                    itemTematicas.add(item)
                }*//*
                //Llenando voy
                dbTriviaRef.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(dS: DataSnapshot) {
                        var j=0
                        for(i in dS.children){
                            if(j>=nTemaActual) {
                                val tem: String = i.key.toString()
                                val numT: String = i.value.toString()
                                print(numT)
                                val item = Item_Tematica(tem, numT)
                                itemTematicas.add(item)
                            }
                            j++
                            if(j==end){
                                contTemas.setText(j.toString())
                                break
                            }
                        }
                    }
                })
                adapter.notifyDataSetChanged()
                adapter.setCargado()
            },3000)
        }
        else{
            Toast.makeText(this,"No hay más temáticas",Toast.LENGTH_LONG).show()
        }
    }*/

}

