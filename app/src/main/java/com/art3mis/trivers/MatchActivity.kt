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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)
        navigation()

        dbTriviaRef = FirebaseDatabase.getInstance().getReference("Trivias")
        spinner = findViewById(R.id.spinner)
        spinner.adapter = ArrayAdapter.createFromResource(this, R.array.tematicas, android.R.layout.simple_spinner_dropdown_item)
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
    private fun leerTematicas() {
        dbTriviaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(dS: DataSnapshot) {
                val value = dS.getValue(String::class.java)
            }
        })
    }
}
