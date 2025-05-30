package com.dicoding.intermediate_satu.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.intermediate_satu.databinding.ActivityAddStoryBinding
import com.dicoding.intermediate_satu.viewmodel.LocationViewModel
import com.dicoding.intermediate_satu.viewmodel.StoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.InputStream

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var viewModel: StoryViewModel
    private lateinit var locationViewModel: LocationViewModel
    private var latitude: Float? = null
    private var longitude: Float? = null
    private var currentImageUri: Uri? = null
    private var currentImageBitmap: Bitmap? = null
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                val bitmap = uriToBitmap(this, uri)
                bitmap?.let {
                    currentImageBitmap = it
                    showImage(it)
                }
            }
        }
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val photo: Bitmap? = result.data?.extras?.get("data") as Bitmap?
                photo?.let {
                    currentImageBitmap = it
                    showImage(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[StoryViewModel::class.java]
        locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        locationViewModel.location.observe(this) {
            if (it != null) {
                latitude = it.latitude.toFloat()
                longitude = it.longitude.toFloat()
                Toast.makeText(
                    this,
                    "Location set to ${it.latitude}, ${it.longitude}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                latitude = null
                longitude = null
                Toast.makeText(this,"Please turn on your location service", Toast.LENGTH_SHORT).show()
                binding.insertLocation.isChecked = false
            }
        }
        locationViewModel.isLoadingLocation.observe(this) {
            binding.progressBarAddStory.visibility = if (it) View.VISIBLE else View.GONE
        }

        binding.openCameraButton.setOnClickListener {
            openCamera()
        }
        binding.openGalleryButton.setOnClickListener {
            openGallery()
        }
        viewModel.isLoadingStory.observe(this) {
            binding.progressBarAddStory.visibility = if (it) View.VISIBLE else View.GONE
        }
        binding.uploadButton.setOnClickListener {
            val description = binding.edAddDescription.text.toString()
            if (description.isEmpty()) {
                binding.edAddDescription.error = "Description is required"
                return@setOnClickListener
            }
            if (currentImageBitmap == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                currentImageBitmap?.let { bitmap ->
                    val file = saveBitmapToFile(bitmap)
                    uploadFile(description, file)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Upload Failed, ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        binding.insertLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                locationViewModel.getLocation(this)
            } else {
                latitude = null
                longitude = null
            }
        }
    }

    private fun showImage(bitmap: Bitmap) {
        binding.imageToPost.setImageBitmap(bitmap)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun uploadFile(description: String, file: File) {
        viewModel.upload(this@AddStoryActivity, description, file, latitude, longitude) { error ->
            if (error) {
                Toast.makeText(this@AddStoryActivity, "Upload Failed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@AddStoryActivity, "Upload Success", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun openCamera() {
        cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
    }


    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(cacheDir, fileName)

        file.outputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
        return file
    }

    private fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream).also {
                inputStream?.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}