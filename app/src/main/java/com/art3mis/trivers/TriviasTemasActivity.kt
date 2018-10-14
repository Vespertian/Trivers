package com.art3mis.trivers

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.art3mis.trivers.Adaptador.AdaptadorTemática
import com.art3mis.trivers.Modelos.Item_Tematica
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_tematica.*
import kotlinx.android.synthetic.main.item_tematicas.view.*
import java.util.*

class TriviasTemasActivity:AppCompatActivity(){
    var itemTematicas:MutableList<Item_Tematica?> =ArrayList()
    lateinit var adapter:AdaptadorTemática

    private lateinit var usuario: FirebaseUser
    private lateinit var uAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var dbUserRef: DatabaseReference
    private lateinit var dbTriviaRef:DatabaseReference
    private lateinit var navigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tematica)
        uAuth= FirebaseAuth.getInstance()
        usuario= uAuth.currentUser!!
        database= FirebaseDatabase.getInstance()
        dbUserRef=database.getReference("Users").child(usuario.uid)
        dbTriviaRef=database.getReference("Trivias/Tematicas")
        navigation = findViewById(R.id.navigation)

        //Cargando primeras 7 temáticas de trivias
        cargarTematicas()
        navigation()

        //Inicializando Vista
        recicler_view.layoutManager =LinearLayoutManager(this)
        adapter= AdaptadorTemática(recicler_view,this,itemTematicas)
        recicler_view.adapter=adapter
//        adapter.setCargarMas(this)
    }

    fun navigation(){
        navigation.selectedItemId = R.id.navigation_trivias
        navigation.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when {
                    item.itemId == R.id.navigation_profile -> startActivity(Intent(this@TriviasTemasActivity, PrivateProfileActivity::class.java))
                    item.itemId == R.id.navigation_match -> startActivity(Intent(this@TriviasTemasActivity, MatchActivity::class.java))
                    item.itemId == R.id.navigation_trivias -> startActivity(Intent(this@TriviasTemasActivity, TriviasTemasActivity::class.java))
                    item.itemId != null -> return true
                }
                return false
            }
        })
    }

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

    fun trivias(view: View){
        Toast.makeText(this,view.tematicaTrivias.text.toString(),Toast.LENGTH_LONG).show()
    }

    private fun cargarTematicas(){
        dbTriviaRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dS: DataSnapshot) {
                for(i in dS.children){
                    val tem:String=i.key.toString()
                    val numT:String=i.value.toString()
                    print(numT)
                    val item=Item_Tematica(tem,numT)
                    itemTematicas.add(item)
                }
            }
        })

        /*
        for(i in 0..6){
            val nom=UUID.randomUUID().toString()
            val item=Item_Tematica(nom,nom.length)
            itemTematicas.add(item)
        }*/
    }

}

private fun DatabaseReference.addListenerForSingleValueEvent(valueEventListener: ValueEventListener, function: () -> Unit) {


}
