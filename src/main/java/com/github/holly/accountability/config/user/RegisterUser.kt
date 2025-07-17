package com.github.holly.accountability.config.user

data class RegisterUser(
    val username: String,
    val name: String,
    val email: String,
    val password: String,
    val passwordRepeated: String,
)
