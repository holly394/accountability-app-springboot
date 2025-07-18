package com.github.holly.accountability.config.auth

import com.github.holly.accountability.user.AccountabilitySessionUser
import com.github.holly.accountability.user.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AccountabilityUserDetailsService(
    val userRepository: UserRepository
): UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails? {
        val databaseUser = userRepository.findByUsernameIgnoreCase(username!!) ?: throw UsernameNotFoundException(username)
        return AccountabilitySessionUser(databaseUser.id!!, databaseUser.username, databaseUser.name, databaseUser.password, listOf(SimpleGrantedAuthority("ROLE_USER")))
    }
}