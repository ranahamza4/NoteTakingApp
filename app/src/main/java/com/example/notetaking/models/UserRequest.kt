package com.example.notetaking.models

data class UserRequest(
    val email: String,
    val password: String,
    val username: String
)