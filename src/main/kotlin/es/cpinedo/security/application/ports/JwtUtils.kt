package es.cpinedo.security.application.ports

import es.cpinedo.security.domain.JwtToken


interface JwtUtils {
    fun generateTokenFromUsername(username: String): JwtToken
    fun getUserNameFromExpiredJwtToken(token: String): String
    fun getUserNameFromJwtToken(token: String?): String
    fun validateJwtToken(authToken: String?): Boolean
    fun buildToken(username: String, grantedAuthorities: Array<String>): JwtToken
}