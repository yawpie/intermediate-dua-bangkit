package com.dicoding.intermediate_satu.viewmodel

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.intermediate_satu.data.User
import com.dicoding.intermediate_satu.data.repository.AppRepository
import com.dicoding.intermediate_satu.ui.LoginActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val appRepository: AppRepository,
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _logoutEvent = MutableLiveData<Unit>()
    val logoutEvent: LiveData<Unit> get() = _logoutEvent



    fun register(context: Context, name: String, email: String, password: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val result = appRepository.register(name, email, password)
                if (result.isFailure) throw result.exceptionOrNull()!!
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    context, "Register Failed, ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                _isLoading.postValue(false)
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    suspend fun login(context: Context, email: String, password: String) : User? {
        _isLoading.postValue(true)

        return try {
            val userData = appRepository.login(email, password).onSuccess {
                _user.postValue(it)
            }.getOrThrow()
            userData
        } catch (e: Exception) {
            e.printStackTrace()
            _user.postValue(null)
            null
        } finally {
            _isLoading.postValue(false)
        }
    }

    fun getUserFromRepo(context: Context) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val user = appRepository.getUser().getOrNull()
                if (user == null) {
                    _user.postValue(null)
                } else {
                    _user.postValue(user)
                }
            } catch (e: Exception) {
                e.printStackTrace()

            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun logout() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                appRepository.logout()
                _user.postValue(null)
                _logoutEvent.postValue(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

}