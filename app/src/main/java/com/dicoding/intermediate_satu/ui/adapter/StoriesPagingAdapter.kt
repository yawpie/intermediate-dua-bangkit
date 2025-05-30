package com.dicoding.intermediate_satu.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.intermediate_satu.data.response.ListStoryItem
import com.dicoding.intermediate_satu.databinding.ItemStoriesBinding
import com.dicoding.intermediate_satu.ui.DetailActivity

class StoriesPagingAdapter :
    PagingDataAdapter<ListStoryItem, StoriesPagingAdapter.MyViewHolder>(DIFF_CALLBACK) {

    companion object DIFF_CALLBACK : DiffUtil.ItemCallback<ListStoryItem>() {
        override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
            return oldItem == newItem
        }
    }

    class MyViewHolder(private val binding: ItemStoriesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            binding.storyUserName.text = data.name
            Glide.with(binding.root.context).load(data.photoUrl).into(binding.storyImage)
            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra("imageUrl", data.photoUrl)
                intent.putExtra("username", data.name)
                intent.putExtra("description", data.description)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }
}