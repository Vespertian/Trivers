package com.art3mis.trivers

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_match.*


class MatchActivity : AppCompatActivity() {
    private lateinit var dbTriviaRef: DatabaseReference
    private lateinit var spinner: Spinner
    lateinit var adapterTematica: ArrayAdapter<CharSequence>         //Para las Tematicas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)
        navigation()

        dbTriviaRef = FirebaseDatabase.getInstance().getReference("Trivias/Tematicas")
        spinner = findViewById(R.id.spinner)
        adapterTematica= ArrayAdapter<CharSequence>(this,R.layout.simple_spiner_tematicas)
        adapterTematica.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cargarTematicas()
    }

    private fun cargarTematicas() {
        dbTriviaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dS: DataSnapshot) {
                adapterTematica.add("Todo")
                for (i in dS.children) {
                    adapterTematica.add(i.key.toString())
                }
                adapterTematica.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapterTematica
            }
        })
    }

    fun navigation(){
        navigation.selectedItemId = R.id.navigation_match
        navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when {
                item.itemId == R.id.navigation_profile -> startActivity(Intent(this@MatchActivity, PrivateProfileActivity::class.java))
                item.itemId == R.id.navigation_match -> startActivity(Intent(this@MatchActivity, MatchActivity::class.java))
                item.itemId == R.id.navigation_trivias -> startActivity(Intent(this@MatchActivity, TriviasTemasActivity::class.java))
                item.itemId != null -> return@OnNavigationItemSelectedListener true
            }
            false
        })
    }
}
