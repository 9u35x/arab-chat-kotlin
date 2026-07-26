package com.arabchat.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val name = intent.getStringExtra("name") ?: "مستخدم"
        val isGroup = intent.getBooleanExtra("isGroup", false)

        val tvBack: TextView = findViewById(R.id.tvBack)
        val tvProfileAvatar: TextView = findViewById(R.id.tvProfileAvatar)
        val tvProfileName: TextView = findViewById(R.id.tvProfileName)
        val tvProfileSubtitle: TextView = findViewById(R.id.tvProfileSubtitle)

        tvProfileAvatar.text = if (name.isNotEmpty()) name.take(1).uppercase() else "?"
        tvProfileName.text = name
        tvProfileSubtitle.text = if (isGroup) "مجموعة" else "محادثة فردية"

        tvBack.setOnClickListener { finish() }
    }
}
