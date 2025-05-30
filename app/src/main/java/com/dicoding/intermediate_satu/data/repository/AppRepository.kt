package com.dicoding.intermediate_satu.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.intermediate_satu.data.network.ApiService
import com.dicoding.intermediate_satu.data.User
import com.dicoding.intermediate_satu.data.network.StoryPagingSource
import com.dicoding.intermediate_satu.data.preferences.SessionManager
import com.dicoding.intermediate_satu.data.request.LoginBody
import com.dicoding.intermediate_satu.data.request.RegisterBody
import com.dicoding.intermediate_satu.data.response.ListStoryItem
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {

    suspend fun register(name: String, email: String, password: String): Result<Boolean> {
        return try {
            val body = RegisterBody(name, email, password)
            val response = apiService.register(body)
            if (!response.error) {
                Result.success(true)
            } else {
                throw Exception(response.message)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val body = LoginBody(email, password)
            val response = apiService.login(body)
            if (!response.error) {
                sessionManager.saveUserData(
                    userId = response.loginResult.userId,
                    name = response.loginResult.name,
                    token = response.loginResult.token
                )
                val result = User(
                    response.loginResult.userId,
                    response.loginResult.name,
                    response.loginResult.token
                )
                Result.success(result)
            } else {
                throw Exception(response.message)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(): Result<User> {
        return try {
            val userId = sessionManager.userIdFlow.first()
            val name = sessionManager.nameFlow.first()
            val token = sessionManager.tokenFlow.first()
            Result.success(
                User(userId!!, name!!, token!!)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    suspend fun getStories(): Result<List<ListStoryItem>> {
//        return try {
//            val token = sessionManager.getTokenOnce()
//            if (token.isNullOrEmpty()) {
//                return Result.failure(IllegalStateException("Token is missing"))
//            }
//            println("Token: $token")
//            val response = apiService.getStory("Bearer $token")
//            Result.success(response.listStory)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Result.failure(e)
//        }
//    }

     fun getStories(): LiveData<PagingData<ListStoryItem>>{
        return Pager(
            config = PagingConfig(
                pageSize = 20
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, sessionManager)
            }
        ).liveData
    }

    suspend fun getStoriesWithLocation(): Result<List<ListStoryItem>> {
        return try {
            val token = sessionManager.getTokenOnce()
            if (token.isNullOrEmpty()) {
                return Result.failure(IllegalStateException("Token is missing"))
            }
            val response = apiService.getStoryWithLocation(token = "Bearer $token")
            Result.success(response.listStory)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun logout() {
        sessionManager.destroyUserData()
    }

    suspend fun upload(
        context: Context,
        description: String,
        file: File,
        lat: Float?,
        lon: Float?
    ): Result<Boolean> {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        val descriptionPart = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
        val latitude = lat?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val longitude = lon?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

        return try {
            val token = sessionManager.getTokenOnce()
            val response =
                apiService.postStory(
                    "Bearer $token",
                    descriptionPart,
                    filePart,
                    latitude,
                    longitude
                )

            if (!response.error) {
                Result.success(true)
            } else {
                val errorBody = response.message
                throw Exception("Upload failed: $errorBody")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, callback: (Float?, Float?) -> Unit) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location: Location? ->
            try {
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
                    callback(latitude.toFloat(), longitude.toFloat())
                } else {
                    throw Exception("Location is null")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null, null)
            }
        }.addOnFailureListener { exception ->
            Log.e("LocationError", "Error fetching location: ${exception.message}")
        }


    }

    suspend fun getLastKnownLocation(): Result<LatLng?> {
        return try {
            val location = sessionManager.locationFlow.first()
            Result.success(location)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    fun setLastKnownLocation(context: Context, lat: Float,lon: Float): Result<Boolean> {
        return try {
            sessionManager.setLatestLocation(lat, lon)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
