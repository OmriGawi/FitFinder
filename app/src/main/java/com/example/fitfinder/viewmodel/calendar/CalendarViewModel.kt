package com.example.fitfinder.viewmodel.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.TrainingSession
import com.example.fitfinder.data.repository.calendar.CalendarRepository

class CalendarViewModel(private val repository: CalendarRepository) : ViewModel() {

    private val _calendarEvents = MutableLiveData<List<TrainingSession>>()
    val calendarEvents: LiveData<List<TrainingSession>> = _calendarEvents

    fun fetchCalendarEvents(userId: String) {
        repository.fetchCalendarEvents(userId) { events ->
            _calendarEvents.postValue(events)
        }
    }
}
