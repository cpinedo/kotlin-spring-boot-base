package es.cpinedo.security.domain

data class JwtToken(val token: String, val expirationTime: Long)