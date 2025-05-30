package com.dicoding.intermediate_satu.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.intermediate_satu.R
import com.dicoding.intermediate_satu.databinding.ActivityMapsBinding
import com.dicoding.intermediate_satu.viewmodel.LocationViewModel
import com.dicoding.intermediate_satu.viewmodel.StoryViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var viewModel: StoryViewModel
    private lateinit var locationViewModel: LocationViewModel
    private var location: LatLng? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermission()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[StoryViewModel::class.java]
        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationViewModel.getLocation(this)

        locationViewModel.isLoadingLocation.observe(this) {
            binding.progressBarMap.visibility = if (it) View.VISIBLE else View.GONE
        }
        locationViewModel.location.observe(this) {
            if (it != null) {
                location = it
                setZoom(it)
            }
        }

        getStory()
    }

    private fun getStory() {
        viewModel.getStoriesWithLocation() {
            Toast.makeText(this, "Story Loaded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setZoom(location: LatLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        viewModel.stories.observe(this) {
            for (story in it) {
                val lon = story.lon as Double
                val lat = story.lat as Double
                val location = LatLng(lat, lon)
                googleMap.addMarker(MarkerOptions().position(location).title(story.name))
            }
        }
        location?.let {
            setZoom(it)
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
    }

}