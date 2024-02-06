package com.example.fitfinder.ui.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fitfinder.R
import com.example.fitfinder.data.model.ExerciseProgress

class ProgressAdapter : RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder>() {

    private var data = listOf<ExerciseProgress>()

    class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvFirstTime: TextView = itemView.findViewById(R.id.tv_firstTime)
        val tvLastTime: TextView = itemView.findViewById(R.id.tv_lastTime)
        val tvProgress: TextView = itemView.findViewById(R.id.tv_progress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_progress, parent, false)
        return ProgressViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) {
        val exerciseProgress = data[position]
        holder.tvName.text = exerciseProgress.exerciseName
        holder.tvFirstTime.text = exerciseProgress.firstValue.toString()
        holder.tvLastTime.text = exerciseProgress.lastValue.toString()
        // Set the progress text and color based on the percentage
        val progressPercentage = exerciseProgress.progressPercentage
        holder.tvProgress.text = "${progressPercentage}%"

        // Set text color based on whether progress is positive or negative
        val progressColor = if (progressPercentage > 0) {
            // Green color if progress is 100% or more
            ContextCompat.getColor(holder.itemView.context, android.R.color.holo_green_dark)
        } else if (progressPercentage < 0){
            // Red color if progress is less than 100%
            ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark)
        } else {
            ContextCompat.getColor(holder.itemView.context, android.R.color.black)
        }

        holder.tvProgress.setTextColor(progressColor)
    }

    override fun getItemCount() = data.size

    fun updateData(newData: List<ExerciseProgress>) {
        data = newData
        notifyDataSetChanged()
    }
}

