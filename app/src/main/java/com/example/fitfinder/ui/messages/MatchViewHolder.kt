package com.example.fitfinder.ui.messages

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitfinder.R

class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val profilePictureImageView: ImageView = view.findViewById(R.id.iv_profilePicture)
    val nameTextView: TextView = view.findViewById(R.id.tv_name)
    val lastMessageTextView: TextView = view.findViewById(R.id.tv_lastMessage)
}
