package com.example.fitfinder.ui.profile

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitfinder.R

class AdditionalPicturesViewHolder(itemView: View, val onRemoveClick: (position: Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
    val image: ImageView = itemView.findViewById(R.id.iv_additional)
    //private val removeButton: ImageView = itemView.findViewById(R.id.iv_remove)  // Assuming you have an ImageView acting as a remove button

   /* init {
        removeButton.setOnClickListener {
            onRemoveClick(adapterPosition)
        }
    }*/
}
