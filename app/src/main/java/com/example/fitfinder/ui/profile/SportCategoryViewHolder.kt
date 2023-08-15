package com.example.fitfinder.ui.profile

import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitfinder.R
import com.example.fitfinder.data.model.SkillLevel

class SportCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val dropdownCategory: AutoCompleteTextView = itemView.findViewById(R.id.dropdown_category)
    private val dropdownSkillLevel: AutoCompleteTextView = itemView.findViewById(R.id.dropdown_skillLevel)
    private val ivRemove: ImageView = itemView.findViewById(R.id.iv_remove)

    fun bind(sportCategoryName: String, sportSkillLevel: SkillLevel, availableCategories: List<String>) {
        // Adapter for category dropdown
        val categoriesAdapter = ArrayAdapter(itemView.context, R.layout.dropdown_item, availableCategories.toTypedArray())
        dropdownCategory.setAdapter(categoriesAdapter)

        // Adapter for skill level dropdown
        val skillLevelAdapter = ArrayAdapter(itemView.context, R.layout.dropdown_item, SkillLevel.values().map { it.name }.toTypedArray())
        dropdownSkillLevel.setAdapter(skillLevelAdapter)

        dropdownCategory.setText(sportCategoryName, false)
        dropdownSkillLevel.setText(sportSkillLevel.name, false)

        // You can set up a click listener for the remove icon here if you want to remove the item from the list.
        ivRemove.setOnClickListener {
            // Do remove operation here
        }
    }

}
