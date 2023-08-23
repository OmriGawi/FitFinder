package com.example.fitfinder.ui.profile

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitfinder.R

class AdditionalPicturesViewHolder(
    itemView: View,
    private val listener: AdditionalPicturesAdapter.OnImageRemovedListener
) : RecyclerView.ViewHolder(itemView) {
    val image: ImageView = itemView.findViewById(R.id.iv_additional)
    private val ivRemove: ImageView = itemView.findViewById(R.id.iv_remove)

    fun bind(imageUrl: String) {
        ivRemove.setOnClickListener {
            listener.onImageRemoved(adapterPosition, imageUrl)
        }
    }

    fun setEditMode(isEditMode: Boolean) {
        ivRemove.visibility = if (isEditMode) View.VISIBLE else View.GONE
    }
}



