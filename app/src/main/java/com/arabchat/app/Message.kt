package com.arabchat.app

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    @ServerTimestamp
    val timestamp: Date? = null
)
