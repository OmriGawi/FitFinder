package com.example.fitfinder.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitfinder.R

class AdditionalPicturesAdapter(
    private val imageList: MutableList<String>,
    private val context: Context,
    private val listener: OnImageRemovedListener
) : RecyclerView.Adapter<AdditionalPicturesViewHolder>() {

    // Interface Callback
    interface OnImageRemovedListener {
        fun onImageRemoved(position: Int, imageUrl: String)
    }

    // Variables
    private var isEditMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdditionalPicturesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_profile_additional_pictures, parent, false)
        return AdditionalPicturesViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: AdditionalPicturesViewHolder, position: Int) {
        val imageUrl = imageList[position]
        Glide.with(context)
            .load(imageUrl)
            .into(holder.image)
        holder.bind(imageUrl)
        holder.setEditMode(isEditMode)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun toggleEditMode() {
        isEditMode = !isEditMode
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = imageList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newImageList: List<String>) {
        imageList.clear()
        imageList.addAll(newImageList)
        notifyDataSetChanged()
    }
}

