package com.example.fitfinder.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitfinder.R
import com.example.fitfinder.data.model.Match
import com.example.fitfinder.data.model.PotentialUser

class MatchesAdapter(private var data: List<Pair<Match, PotentialUser>>) : RecyclerView.Adapter<MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_chats, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val (match, potentialUser) = data[position]

        Glide.with(holder.profilePictureImageView.context).load(potentialUser.profilePictureUrl).into(holder.profilePictureImageView)
        holder.nameTextView.text = "${potentialUser.firstName} ${potentialUser.lastName}"
        holder.lastMessageTextView.text = match.lastMessage?.content ?: "No messages yet"
    }

    override fun getItemCount() = data.size

    fun updateData(newData: List<Pair<Match, PotentialUser>>) {
        data = newData
        notifyDataSetChanged()
    }
}
