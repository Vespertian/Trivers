package com.art3mis.trivers

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_match.*

class MatchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)
        navigation()
    }

    fun navigation(){
        navigation.selectedItemId = R.id.navigation_match
        navigation.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when {
                    item.itemId == R.id.navigation_profile -> startActivity(Intent(this@MatchActivity, PrivateProfileActivity::class.java))
                    item.itemId == R.id.navigation_match -> startActivity(Intent(this@MatchActivity, MatchActivity::class.java))
                    item.itemId == R.id.navigation_trivias -> startActivity(Intent(this@MatchActivity, TriviasTemasActivity::class.java))
                    item.itemId != null -> return true
                }
                return false
            }
        })
    }
}
