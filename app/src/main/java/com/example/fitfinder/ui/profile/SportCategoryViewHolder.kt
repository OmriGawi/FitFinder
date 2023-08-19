package com.example.fitfinder.ui.profile

import android.view.View

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitfinder.R
import com.example.fitfinder.data.model.SkillLevel

class SportCategoryViewHolder(itemView: View, private val onRemoveClick: (position: Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
    private val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
    private val tvSkillLevel: TextView = itemView.findViewById(R.id.tv_skillLevel)
    private val ivRemove: ImageView = itemView.findViewById(R.id.iv_remove)

    fun bind(sportCategoryName: String, sportSkillLevel: SkillLevel) {
        tvCategory.text = sportCategoryName
        tvSkillLevel.text = sportSkillLevel.name

        ivRemove.setOnClickListener {
            onRemoveClick(adapterPosition)
        }
    }
}

