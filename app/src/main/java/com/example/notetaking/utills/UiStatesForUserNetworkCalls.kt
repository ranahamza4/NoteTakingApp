package com.example.notetaking.utills

import com.example.notetaking.models.UserResponse

sealed class UiStatesForUserNetworkCalls {

    data class Success(val userResponse: UserResponse) : UiStatesForUserNetworkCalls()
    data class Error(val message: String) : UiStatesForUserNetworkCalls()
    object Loading : UiStatesForUserNetworkCalls()
    object Idle : UiStatesForUserNetworkCalls()
}