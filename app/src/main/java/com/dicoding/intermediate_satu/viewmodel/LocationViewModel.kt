package com.dicoding.intermediate_satu.viewmodel

import android.content.Context
import android.location.LocationManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.intermediate_satu.data.repository.AppRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(private val appRepository: AppRepository) :
    ViewModel() {
    private val _location = MutableLiveData<LatLng?>()
    val location: LiveData<LatLng?> get() = _location
    private val _isLoadingLocation = MutableLiveData<Boolean>()
    val isLoadingLocation: LiveData<Boolean> get() = _isLoadingLocation

    fun getLocation(context: Context) {
        _isLoadingLocation.postValue(true)
        viewModelScope.launch {
            try {
                if (isLocationServiceEnabled(context)) {
                    appRepository.getCurrentLocation(context) { latitude, longitude ->
                        if (latitude != null && longitude != null) {
                            _location.postValue(LatLng(latitude.toDouble(), longitude.toDouble()))
                            appRepository.setLastKnownLocation(context, latitude, longitude)
                        } else {
                            _location.postValue(null)
                        }
                        _isLoadingLocation.postValue(false)
                    }
                } else {
                    appRepository.getLastKnownLocation().onSuccess {
                        _location.postValue(it)
                    }.onFailure {
                        _location.postValue(null)
                    }
                    _isLoadingLocation.postValue(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun isLocationServiceEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

//        val isGpsEnabled =
//        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

}