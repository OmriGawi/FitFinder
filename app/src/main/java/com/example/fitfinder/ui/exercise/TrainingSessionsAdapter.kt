package com.example.fitfinder.ui.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitfinder.R
import com.example.fitfinder.data.model.TrainingSession
import java.text.SimpleDateFormat
import java.util.*

class TrainingSessionsAdapter : RecyclerView.Adapter<TrainingSessionsAdapter.TrainingSessionViewHolder>() {

    private var sessions = listOf<Pair<TrainingSession, Map<String, Any?>>>()

    class TrainingSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views in the recycler_item_training_sessions.xml
        val tvPartner: TextView = itemView.findViewById(R.id.tv_partner)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvSportCategory: TextView = itemView.findViewById(R.id.tv_sportCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingSessionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_training_sessions, parent, false)
        return TrainingSessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingSessionViewHolder, position: Int) {
        val (session, partnerDetails) = sessions[position]
        holder.tvPartner.text = "${partnerDetails["firstName"]} ${partnerDetails["lastName"]}"
        holder.tvDate.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(session.dateTime.toDate())
        holder.tvSportCategory.text = session.sportCategory
    }

    override fun getItemCount() = sessions.size

    fun updateData(newSessions: List<Pair<TrainingSession, Map<String, Any?>>>) {
        sessions = newSessions
        notifyDataSetChanged()
    }
}
