package com.github.holly.accountability.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {

    fun findByUsernameIgnoreCase(username: String): User?

    fun findUserById(userId: Long): User?

    fun findUsersByUsernameContainsIgnoreCase(username: String): List<User>

}
