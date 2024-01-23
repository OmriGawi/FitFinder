package com.example.fitfinder.ui.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitfinder.R
import com.example.fitfinder.data.model.TrainingSession
import java.text.SimpleDateFormat
import java.util.*

class UnfilledReportsAdapter(
    private var reportsWithDetails: List<Pair<TrainingSession, Map<String, Any?>>>,
    private val onFillReportClicked: (TrainingSession) -> Unit
) : RecyclerView.Adapter<UnfilledReportsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val ivPicture: ImageView = view.findViewById(R.id.iv_picture)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val tvSportCategory: TextView = view.findViewById(R.id.tv_sportCategory)
        val btnFillReport: Button = view.findViewById(R.id.btn_fillReport)
        // ... other views
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (report, partnerDetails) = reportsWithDetails[position]
        holder.tvName.text = "${partnerDetails["firstName"]} ${partnerDetails["lastName"]}"
        holder.tvDate.text = formatDate(report.dateTime.toDate())
        holder.tvSportCategory.text = report.sportCategory

        // Load the profile picture using Glide or another image loading library
        Glide.with(holder.ivPicture.context)
            .load(partnerDetails["profilePictureUrl"] as String?)
            .into(holder.ivPicture)

        holder.btnFillReport.setOnClickListener {
            onFillReportClicked(report)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_unfilled_reports, parent, false)
        return ViewHolder(view)
    }

    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(date)
    }

    override fun getItemCount() = reportsWithDetails.size

    fun updateData(newReportsWithDetails: List<Pair<TrainingSession, Map<String, Any?>>>) {
        this.reportsWithDetails = newReportsWithDetails
        notifyDataSetChanged()
    }
}


