package com.dicoding.intermediate_satu.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dicoding.intermediate_satu.data.network.ApiService
import com.dicoding.intermediate_satu.data.preferences.SessionManager
import com.dicoding.intermediate_satu.data.repository.AppRepository
import com.dicoding.intermediate_satu.data.response.ListStoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> get() = _stories
    private val _isLoadingStory = MutableLiveData<Boolean>()
    val isLoadingStory: LiveData<Boolean> get() = _isLoadingStory
    val storiesPaging = appRepository.getStories().cachedIn(viewModelScope)
//    var storiesPaging: LiveData<PagingData<ListStoryItem>>? = null

    //    fun getStories() {
//        _isLoadingStory.postValue(true)
//        viewModelScope.launch {
//            try {
////                val listStoryItems = appRepository.getStories()
////                _stories.postValue(listStoryItems.getOrThrow())
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                _isLoadingStory.postValue(false)
//            }
//        }
//    }
//    private val _storiesPaging = MutableLiveData<PagingData<ListStoryItem>>()
//    fun getStories() {
//    _isLoadingStory.postValue(true)
//    if (storiesPaging.value == null) {
//        storiesPaging = Pager(
//            config = PagingConfig(
//                pageSize = 5,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = { StoryPagingSource(apiService, sessionManager) }
//        ).liveData.cachedIn(viewModelScope)
//    }
//    _isLoadingStory.postValue(false)
//    }

    fun getStoriesWithLocation(callback: () -> Unit) {
        _isLoadingStory.postValue(true)
        viewModelScope.launch {
            try {
                val listStoryItems = appRepository.getStoriesWithLocation()
                _stories.postValue(listStoryItems.getOrThrow())
                callback()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingStory.postValue(false)
            }
        }
    }

    fun upload(
        context: Context,
        description: String,
        file: File,
        lat: Float?,
        lon: Float?,
        onErrorCallback: (Boolean) -> Unit
    ) {
        _isLoadingStory.postValue(true)
        viewModelScope.launch {
            try {
                val result = appRepository.upload(context, description, file, lat, lon)
                if (result.isFailure) throw result.exceptionOrNull()!!
                onErrorCallback(false)
            } catch (e: Exception) {
                e.printStackTrace()
                onErrorCallback(true)
            } finally {
                _isLoadingStory.postValue(false)
            }
        }
    }
}