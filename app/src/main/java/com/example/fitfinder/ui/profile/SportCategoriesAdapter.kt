package com.example.fitfinder.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitfinder.R
import com.example.fitfinder.data.model.SportCategory

class SportCategoriesAdapter(
    private val userSportCategories: MutableList<SportCategory>
) : RecyclerView.Adapter<SportCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_profile, parent, false)
        return SportCategoryViewHolder(view) { position ->
            removeItemAt(position)
        }
    }


    override fun onBindViewHolder(holder: SportCategoryViewHolder, position: Int) {
        val sportCategory = userSportCategories[position]
        holder.bind(sportCategory.name, sportCategory.skillLevel)
    }

    override fun getItemCount(): Int = userSportCategories.size

    private fun removeItemAt(position: Int) {
        userSportCategories.removeAt(position)
        notifyItemRemoved(position)
    }
}

