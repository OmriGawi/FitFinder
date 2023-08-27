package com.example.fitfinder.ui.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitfinder.R
import com.example.fitfinder.data.model.SportCategory

class SportCategoriesAdapter(
    private val userSportCategories: MutableList<SportCategory>,
    private val listener: OnSportCategoryRemovedListener
) : RecyclerView.Adapter<SportCategoryViewHolder>() {

    // Interface callback
    interface OnSportCategoryRemovedListener {
        fun onSportCategoryRemoved(position: Int, sportCategory: SportCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportCategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_profile, parent, false)
        return SportCategoryViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: SportCategoryViewHolder, position: Int) {
        holder.bind(userSportCategories[position])
    }

    override fun getItemCount(): Int = userSportCategories.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newData: List<SportCategory>) {
        userSportCategories.clear()
        userSportCategories.addAll(newData)
        notifyDataSetChanged()
    }

}

