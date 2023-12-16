package com.example.fitfinder.viewmodel.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.TrainingInvite
import com.example.fitfinder.data.repository.calendar.CalendarCreateEventRepository
import com.example.fitfinder.util.Event
import com.example.fitfinder.util.ToastyType
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class CalendarCreateEventViewModel(private val repository: CalendarCreateEventRepository) : ViewModel() {

    // LiveData to handle toast messages
    private val _toastMessageEvent = MutableLiveData<Event<Pair<String, ToastyType>>>()
    val toastMessageEvent: LiveData<Event<Pair<String, ToastyType>>> = _toastMessageEvent

    fun createTrainingInvite(
        senderId: String,
        receiverId: String?,
        sportCategory: String,
        exercises: List<String>,
        dateStr: String,
        timeStr: String,
        location: String,
        additionalEquipment: String
    ) {
        val dateTime = convertStringToTimestamp(dateStr, timeStr)
        dateTime.let {
            val invite = TrainingInvite(
                senderId = senderId,
                receiverId = receiverId ?: "",
                sportCategory = sportCategory,
                exercises = exercises,
                dateTime = it,
                location = location,
                additionalEquipment = additionalEquipment,
                createdAt = Timestamp.now()
            )
            repository.createInvite(invite) { success ->
                if (success) {
                    _toastMessageEvent.value = Event(Pair("Invite sent successfully!", ToastyType.SUCCESS))
                } else {
                    _toastMessageEvent.value = Event(Pair("Failed to send invite.", ToastyType.ERROR))
                }
            }
        }
    }

    private fun convertStringToTimestamp(dateStr: String, timeStr: String): Timestamp {
        val dateFormat = SimpleDateFormat("d MMM, yyyy h:mm a", Locale.getDefault())
        val parsedDate = dateFormat.parse("$dateStr $timeStr")!!
        return Timestamp(parsedDate)
    }
}

