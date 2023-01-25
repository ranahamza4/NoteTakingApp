package com.example.notetaking.repository

import android.util.Log
import com.example.notetaking.api.UserAPI
import com.example.notetaking.models.UserRequest
import com.example.notetaking.models.UserResponse
import com.example.notetaking.utills.Constants.TAG
import com.example.notetaking.utills.NetworkResult
import kotlinx.coroutines.flow.MutableSharedFlow
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val userAPI: UserAPI) {

    private var _userResponseFlow = MutableSharedFlow<NetworkResult<UserResponse>>()
    var userResponseFlow = _userResponseFlow

    suspend fun registerUser(userRequest: UserRequest) {

        val response = userAPI.signUp(userRequest)
        handleResponse(response)
    }

    private suspend fun handleResponse(response: Response<UserResponse>) {
        if (response.isSuccessful && response != null) {
            _userResponseFlow.emit(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            Log.d(TAG, "handleResponseeee: ${response.message()}")
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _userResponseFlow.emit(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _userResponseFlow.emit(NetworkResult.Error("Something Went Wrong"))
        }
    }

    suspend fun loginUser(userRequest: UserRequest) {
        val response = userAPI.signIn(userRequest)
        handleResponse(response)

    }
}