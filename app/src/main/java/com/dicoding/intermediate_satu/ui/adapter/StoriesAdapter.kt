package com.dicoding.intermediate_satu.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.intermediate_satu.R
import com.dicoding.intermediate_satu.data.response.ListStoryItem
import com.dicoding.intermediate_satu.ui.DetailActivity

class StoriesAdapter(private val items: List<ListStoryItem>) :
    RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val storyImageView: ImageView = view.findViewById(R.id.story_image)
        val nameTextView: TextView = view.findViewById(R.id.story_user_name)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stories, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTextView.text = items[position].name
        Glide.with(holder.itemView.context).load(items[position].photoUrl)
            .into(holder.storyImageView)
        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in)
        holder.itemView.startAnimation(animation)

        holder.itemView.setOnClickListener { 
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("imageUrl", items[position].photoUrl)
            intent.putExtra("username", items[position].name)
            intent.putExtra("description", items[position].description)
            holder.itemView.context.startActivity(intent)

        }
    }


}