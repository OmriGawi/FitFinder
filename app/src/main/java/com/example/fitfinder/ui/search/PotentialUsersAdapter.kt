package com.example.fitfinder.ui.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fitfinder.R
import com.example.fitfinder.data.model.PotentialUser

class PotentialUsersAdapter(
    private var users: List<PotentialUser>,
    private val actionCallback: CardActionCallback,
) : RecyclerView.Adapter<PotentialUsersAdapter.UserViewHolder>() {

    interface CardActionCallback {
        fun onLikeClicked()
        fun onDislikeClicked()
        fun onInfoClicked(user: PotentialUser)
    }

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProfilePicture: ImageView = view.findViewById(R.id.iv_profilePicture)
        val ivInfo: ImageView = view.findViewById(R.id.iv_info)
        val tvFullNameAndAge: TextView = view.findViewById(R.id.tv_fullNameAndAge)
        val tvDistance: TextView = view.findViewById(R.id.tv_distance)
        val tvWorkoutTimes: TextView = view.findViewById(R.id.tv_workoutTimes)
        val btnLike: ImageButton = view.findViewById(R.id.btn_like)
        val btnDislike: ImageButton = view.findViewById(R.id.btn_dislike)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_potential_user, parent, false)
        return UserViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        // Setting text views
        holder.tvFullNameAndAge.text = "${user.firstName} ${user.lastName}, ${user.age}"
        holder.tvDistance.text = "${user.distance} KM away"
        holder.tvWorkoutTimes.text = user.workoutTimes.joinToString(", ") { it.name }

        // Using Glide to populate the ivProfilePicture
        Glide.with(holder.ivProfilePicture.context)
            .load(user.profilePictureUrl)
            .into(holder.ivProfilePicture)

        holder.btnLike.setOnClickListener {
            actionCallback.onLikeClicked()
        }

        holder.btnDislike.setOnClickListener {
            actionCallback.onDislikeClicked()
        }

        holder.ivInfo.setOnClickListener {
            actionCallback.onInfoClicked(user)
        }
    }

    fun getUserAtPosition(position: Int): PotentialUser = users[position]


    override fun getItemCount(): Int = users.size

    @SuppressLint("NotifyDataSetChanged")
    fun setUsers(newUsers: List<PotentialUser>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
