package com.arabchat.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val auth = FirebaseAuth.getInstance()
        val tvWelcome: TextView = findViewById(R.id.tvWelcome)
        val tvLogout: TextView = findViewById(R.id.tvLogout)

        val user = auth.currentUser
        val label = when {
            user == null -> getString(R.string.home_placeholder)
            user.isAnonymous -> "مرحباً بك كضيف 👋"
            else -> "مرحباً بك، ${user.email} 👋"
        }
        tvWelcome.text = label

        val tvEnterChat: TextView = findViewById(R.id.tvEnterChat)
        tvEnterChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        tvLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
