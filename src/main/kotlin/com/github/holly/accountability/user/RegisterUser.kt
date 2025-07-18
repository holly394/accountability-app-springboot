package com.github.holly.accountability.user

data class RegisterUser(
    val username: String,
    val name: String,
    val email: String,
    val password: String,
    val passwordRepeated: String,
)
