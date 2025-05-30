package com.dicoding.intermediate_satu.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.intermediate_satu.databinding.ActivityStoriesBinding
import com.dicoding.intermediate_satu.ui.adapter.LoadingStateAdapter
import com.dicoding.intermediate_satu.ui.adapter.StoriesPagingAdapter
import com.dicoding.intermediate_satu.viewmodel.StoryViewModel
import com.dicoding.intermediate_satu.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoriesBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StoriesPagingAdapter
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var userViewModel: UserViewModel
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.CAMERA] == false ||
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == false || permissions[Manifest.permission.ACCESS_FINE_LOCATION] == false ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == false
            ) {
                Toast.makeText(this, "Permissions are required to proceed.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView = binding.storyRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StoriesPagingAdapter()
        recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        storyViewModel = ViewModelProvider(this)[StoryViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        getData()
        checkAndRequestPermission()

        binding.addStory.setOnClickListener {
            addStory()
        }
        binding.logoutButton.setOnClickListener {
            logout()
        }
        binding.mapButton.setOnClickListener {
            openMap()
        }
        binding.refresh.setOnRefreshListener {
            adapter.refresh()
            binding.refresh.isRefreshing = false
        }
        userViewModel.logoutEvent.observe(this) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun checkAndRequestPermission() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onResume() {
        super.onResume()
        adapter.refresh()
    }

    private fun getData() {
        storyViewModel.storiesPaging.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun addStory() {
        val intent = Intent(this, AddStoryActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        userViewModel.logout()
    }

    private fun openMap() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}