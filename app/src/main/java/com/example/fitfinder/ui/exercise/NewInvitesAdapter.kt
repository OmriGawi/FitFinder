package com.example.fitfinder.ui.exercise

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitfinder.R
import com.example.fitfinder.data.model.TrainingInvite

class NewInvitesAdapter(
    private var invitesWithDetails: List<Pair<TrainingInvite, Map<String, Any?>>>,
    private val onDeclineInvite: (String) -> Unit,
    private val onAcceptInvite: (TrainingInvite) -> Unit,
    private val onInfoClicked: (TrainingInvite) -> Unit
    ) : RecyclerView.Adapter<NewInvitesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val ivProfile: ImageView = view.findViewById(R.id.iv_picture)
        val btnAccept: Button = view.findViewById(R.id.btn_accept)
        val btnDecline: Button = view.findViewById(R.id.btn_decline)
        val ivInfo: ImageView = view.findViewById(R.id.iv_info)
        // ... other views
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_new_invites, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (invite, userDetails) = invitesWithDetails[position]
        holder.tvName.text = "${userDetails["firstName"]} ${userDetails["lastName"]}"
        // Load the profile picture using Glide or another image loading library
        Glide.with(holder.ivProfile.context)
            .load(userDetails["profilePictureUrl"] as String?)
            .into(holder.ivProfile)

        holder.btnAccept.setOnClickListener {
            onAcceptInvite(invitesWithDetails[position].first)
        }

        holder.btnDecline.setOnClickListener {
            onDeclineInvite(invite.id)
        }

        holder.ivInfo.setOnClickListener {
            onInfoClicked(invitesWithDetails[position].first)
        }
    }

    override fun getItemCount() = invitesWithDetails.size

    fun updateData(newInvitesWithDetails: List<Pair<TrainingInvite, Map<String, Any?>>>) {
        this.invitesWithDetails = newInvitesWithDetails
        notifyDataSetChanged()
    }
}
