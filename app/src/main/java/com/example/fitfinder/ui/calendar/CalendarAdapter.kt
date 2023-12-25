package com.example.fitfinder.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitfinder.R
import com.example.fitfinder.data.model.TrainingSession
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter (private var events: List<TrainingSession>) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSportCategory: TextView = view.findViewById(R.id.tv_sportCategory)
        val tvTime: TextView = view.findViewById(R.id.tv_time)
        // Add other views you might need
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_calendar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.tvSportCategory.text = event.sportCategory
        holder.tvTime.text = SimpleDateFormat("hh:mm a", Locale.US).format(event.dateTime.toDate())

        // Bind other event information to the views
    }

    override fun getItemCount() = events.size

    fun updateData(newEvents: List<TrainingSession>) {
        this.events = newEvents.sortedBy { it.dateTime.toDate() }
        notifyDataSetChanged()
    }
}