package com.example.fitfinder.ui.profile

import android.view.View

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitfinder.R
import com.example.fitfinder.data.model.SportCategory

class SportCategoryViewHolder(
    itemView: View,
    private val listener: SportCategoriesAdapter.OnSportCategoryRemovedListener
) : RecyclerView.ViewHolder(itemView) {
    private val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
    private val tvSkillLevel: TextView = itemView.findViewById(R.id.tv_skillLevel)
    private val ivRemove: ImageView = itemView.findViewById(R.id.iv_remove)

    fun bind(sportCategory: SportCategory) {
        tvCategory.text = sportCategory.name
        tvSkillLevel.text = sportCategory.skillLevel.name

        ivRemove.setOnClickListener {
            listener.onSportCategoryRemoved(adapterPosition, sportCategory)
        }
    }

}

