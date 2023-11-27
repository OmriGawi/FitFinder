package com.example.fitfinder.data.repository.messages

import com.example.fitfinder.data.model.Message
import com.example.fitfinder.data.repository.BaseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatRepository: BaseRepository() {

    private val db = FirebaseFirestore.getInstance()

    fun getMessages(matchId: String): Query {
        return db.collection("matches")
            .document(matchId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
    }

    fun sendMessage(matchId: String, message: Message): Task<Void> {
        val newMessageRef = db.collection("matches")
            .document(matchId)
            .collection("messages")
            .document() // Firestore generates a new document ID

        val batch = db.batch()

        // Create a new message with the generated ID
        val newMessage = message.copy(messageId = newMessageRef.id)

        batch.set(newMessageRef, newMessage)

        // Update the last message in the match document
        val lastMessageMap = mapOf(
            "content" to message.content,
            "timestamp" to message.timestamp,
            "sender" to message.sender,
            "seen" to false
        )
        val matchRef = db.collection("matches").document(matchId)
        batch.update(matchRef, "lastMessage", lastMessageMap)

        return batch.commit()
    }

    fun markMessageAsSeen(matchId: String) {
        // Update 'seen' field in 'lastMessage' of 'matches' document
    }

    // Add more functions as needed for pagination, etc.
}
