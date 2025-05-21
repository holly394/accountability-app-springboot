package com.github.holly.accountability.config.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class AccountabilitySessionUser(
    val id: Long,
    val name: String,
    val _username: String,
    val _password: String,
    val _authorities: List<GrantedAuthority>
): UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return _authorities
    }

    override fun getPassword(): String? {
        return _password
    }

    override fun getUsername(): String? {
        return _username
    }
}
