package com.arabchat.app

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: MessageAdapter
    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var chatId: String

    private var listenerRegistration: ListenerRegistration? = null
    private val messagesRef by lazy {
        db.collection("chats").document(chatId).collection("messages")
    }
    private val chatRef by lazy {
        db.collection("chats").document(chatId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val chatIdExtra = intent.getStringExtra("chatId")
        if (currentUser == null || chatIdExtra == null) {
            finish()
            return
        }
        chatId = chatIdExtra

        val chatTitle = intent.getStringExtra("chatTitle") ?: getString(R.string.general_chat_title)

        rvMessages = findViewById(R.id.rvMessages)
        etMessage = findViewById(R.id.etMessage)
        val tvSend: TextView = findViewById(R.id.tvSend)
        val tvBack: TextView = findViewById(R.id.tvBack)
        val tvTitle: TextView = findViewById(R.id.tvChatTitle)
        tvTitle.text = chatTitle

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        rvMessages.layoutManager = layoutManager

        adapter = MessageAdapter(mutableListOf(), currentUser.uid)
        rvMessages.adapter = adapter

        tvBack.setOnClickListener { finish() }
        tvSend.setOnClickListener { sendMessage() }
        tvTitle.setOnClickListener {
            val profileIntent = android.content.Intent(this, ProfileActivity::class.java)
            profileIntent.putExtra("name", chatTitle)
            startActivity(profileIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        listenerRegistration = messagesRef
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limitToLast(200)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val messages = snapshot.toObjects(Message::class.java)
                adapter.submitList(messages)
                if (messages.isNotEmpty()) {
                    rvMessages.scrollToPosition(messages.size - 1)
                }
            }
    }

    override fun onStop() {
        super.onStop()
        listenerRegistration?.remove()
    }

    private fun sendMessage() {
        val text = etMessage.text.toString().trim()
        if (text.isEmpty()) return

        val user = auth.currentUser ?: return
        val senderName = if (user.isAnonymous) "ضيف" else (user.email ?: "مستخدم")

        val message = hashMapOf(
            "senderId" to user.uid,
            "senderName" to senderName,
            "text" to text,
            "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )

        messagesRef.add(message)
            .addOnFailureListener { e ->
                Toast.makeText(this, "خطأ: ${e.message}", Toast.LENGTH_LONG).show()
            }

        chatRef.update(
            mapOf(
                "lastMessage" to text,
                "lastMessageTime" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
        )

        etMessage.setText("")
    }
}
