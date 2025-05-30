package com.dicoding.intermediate_satu.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dicoding.intermediate_satu.R
import com.dicoding.intermediate_satu.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity: AppCompatActivity() {

    private lateinit var binding : ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            title = "Story"
            setBackgroundDrawable(ContextCompat.getDrawable(this@DetailActivity, R.color.purple_500))
            setDisplayHomeAsUpEnabled(true)
        }
        val imageUrl = intent.getStringExtra("imageUrl")
        val name = intent.getStringExtra("username")
        val description = intent.getStringExtra("description")

        binding.detailUsername.text = name
        binding.detailDescription.text = description
        Glide.with(this)
            .load(imageUrl)
            .placeholder(ContextCompat.getDrawable(this,R.drawable.ic_place_holder))
            .into(binding.detailImage)

    }
}