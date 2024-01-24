package com.example.fitfinder.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitfinder.R

class ProfileViewAdditionalPicturesAdapter(private val pictureUrls: List<String>) :
    RecyclerView.Adapter<ProfileViewAdditionalPicturesAdapter.PictureViewHolder>() {

    class PictureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.iv_additional)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item_profile_view_additional_pictures, parent, false)
        return PictureViewHolder(view)
    }

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        val pictureUrl = pictureUrls[position]
        Glide.with(holder.imageView.context)
            .load(pictureUrl)
            .into(holder.imageView)
    }

    override fun getItemCount() = pictureUrls.size
}
