package com.example.fitfinder.viewmodel.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.Message
import com.example.fitfinder.data.repository.messages.ChatRepository

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    // Function to load messages
    fun loadMessages(matchId: String) {
        repository.getMessages(matchId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle the error
                return@addSnapshotListener
            }

            val messages = snapshot?.documents?.mapNotNull {
                it.toObject(Message::class.java)
            }
            _messages.value = messages.orEmpty()
        }
    }

    // Function to send a message
    fun sendMessage(matchId: String, message: Message) {
        repository.sendMessage(matchId, message).addOnCompleteListener {
            if (it.isSuccessful) {
                // Handle successful sending, e.g., clear the input field
            } else {
                // Handle error, e.g., show a toast message
            }
        }
    }

    // Function to mark the last message as seen
    fun markMessageAsSeen(matchId: String) {
        repository.markMessageAsSeen(matchId)
    }
}
