package com.example.notetaking

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notetaking.models.UserRequest
import com.example.notetaking.repository.UserRepository
import com.example.notetaking.utills.NetworkResult
import com.example.notetaking.utills.UiStatesForUserNetworkCalls
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _userResponseStateFlow =
        MutableStateFlow<UiStatesForUserNetworkCalls>(UiStatesForUserNetworkCalls.Idle)
    val userResponseStateFlow = _userResponseStateFlow

    fun registerUser(userRequest: UserRequest) {

        viewModelScope.launch {
            _userResponseStateFlow.emit(UiStatesForUserNetworkCalls.Loading)
            userRepository.registerUser(userRequest)

        }
        checkUiState()

    }

    private fun checkUiState() {
        viewModelScope.launch {
            userRepository.userResponseFlow.collect { event ->
                when (event) {
                    is NetworkResult.Success -> {

                        _userResponseStateFlow.emit(UiStatesForUserNetworkCalls.Success(event.data!!))
                    }
                    is NetworkResult.Error -> {
                        _userResponseStateFlow.emit(UiStatesForUserNetworkCalls.Error(event.message!!))

                    }
                    is NetworkResult.Loading ->{
                        ;
                    }

                }
            }
        }
    }

    fun loginUser(userRequest: UserRequest) {

        viewModelScope.launch {
            _userResponseStateFlow.emit(UiStatesForUserNetworkCalls.Loading)
            userRepository.loginUser(userRequest)
        }
        checkUiState()
    }

    fun authenticateUserRequest(
        username: String,
        email: String,
        password: String,
    ): Pair<Boolean, String> {
        var result = Pair(true, "")
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

            result = Pair(false, "Please fill all fields")

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            result = Pair(false, "Please provide valid email")
        } else if (password.length <= 5) {
            result = Pair(false, "Password should be 5 digit long")
        }

        return result
    }

}