package com.example.fitfinder.ui.exercise

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitfinder.R

class ReportAfterTrainingAdapter(private var exercises: List<String>) : RecyclerView.Adapter<ReportAfterTrainingAdapter.ViewHolder>() {

    // Map to store user input for each exercise
    private var exerciseDetails = mutableMapOf<String, String>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvExerciseName: TextView = view.findViewById(R.id.tv_exerciseName)
        val etExerciseDetail: EditText = view.findViewById(R.id.et_exerciseDetail)

        fun bind(exercise: String, exerciseDetailsMap: MutableMap<String, String>) {
            tvExerciseName.text = exercise
            etExerciseDetail.setText(exerciseDetailsMap[exercise])

            // Update the map whenever the text changes
            etExerciseDetail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    exerciseDetailsMap[exercise] = s.toString()
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item_report_after_training, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.bind(exercise, exerciseDetails)
    }

    override fun getItemCount() = exercises.size

    // Function to update the exercises list
    fun updateData(newExercises: List<String>) {
        exercises = newExercises
        exerciseDetails = exercises.associateWith { "" }.toMutableMap()
        notifyDataSetChanged()
    }


    // Function to retrieve the user input for each exercise
    fun getExerciseDetails(): Map<String, String> = exerciseDetails
}
