package com.example.fitfinder.viewmodel.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitfinder.data.model.TrainingSession
import com.example.fitfinder.data.repository.calendar.CalendarRepository
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewModel(private val repository: CalendarRepository) : ViewModel() {
    private val _selectedDate = MutableLiveData<Date>()
    val selectedDate: LiveData<Date> get() = _selectedDate

    private val _allCalendarEvents = MutableLiveData<List<TrainingSession>>()
    private val _filteredCalendarEvents = MediatorLiveData<List<TrainingSession>>()
    val filteredCalendarEvents: LiveData<List<TrainingSession>> get() = _filteredCalendarEvents

    private val _selectedTrainingSession = MutableLiveData<TrainingSession>()
    val selectedTrainingSession: LiveData<TrainingSession> = _selectedTrainingSession

    fun selectTrainingSession(trainingSession: TrainingSession) {
        _selectedTrainingSession.value = trainingSession
    }

    fun resetSelectedDateToCurrent() {
        _selectedDate.value = Calendar.getInstance().time
    }


    init {
        // Initialize with the current date
        _selectedDate.value = Calendar.getInstance().time

        // Add sources to MediatorLiveData
        _filteredCalendarEvents.addSource(_allCalendarEvents) { filterEventsForSelectedDate() }
        _filteredCalendarEvents.addSource(_selectedDate) { filterEventsForSelectedDate() }
    }

    fun setSelectedDate(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
        _selectedDate.value = calendar.time
    }

    fun fetchCalendarEvents(userId: String) {
        repository.fetchCalendarEvents(userId) { events ->
            _allCalendarEvents.postValue(events)
        }
    }

    private fun filterEventsForSelectedDate() {
        val allEvents = _allCalendarEvents.value.orEmpty()
        val selectedDay = _selectedDate.value ?: return

        val filteredEvents = allEvents.filter { it.dateTime.toDate().isSameDay(selectedDay) }
        _filteredCalendarEvents.postValue(filteredEvents)
    }

    private fun Date.isSameDay(other: Date): Boolean {
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return fmt.format(this) == fmt.format(other)
    }
}


