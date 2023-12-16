package com.example.fitfinder.ui.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitfinder.R
import com.example.fitfinder.data.model.TrainingInvite

class NewInvitesAdapter(private var invites: List<TrainingInvite>) : RecyclerView.Adapter<NewInvitesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Assuming you have defined IDs in your layout
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val btnAccept: Button = view.findViewById(R.id.btn_accept)
        val btnDecline: Button = view.findViewById(R.id.btn_decline)
        // ... other views you might need
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_new_invites, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val invite = invites[position]
        // Bind the data to your views
        holder.tvName.text = invite.senderId // Replace with actual sender name if available
        // You would probably want to fetch the sender's user info to get their name

        holder.btnAccept.setOnClickListener {
            // Handle accept action
        }

        holder.btnDecline.setOnClickListener {
            // Handle decline action
        }
    }

    override fun getItemCount() = invites.size

    fun updateData(newInvites: List<TrainingInvite>) {
        this.invites = newInvites
        notifyDataSetChanged()
    }
}
