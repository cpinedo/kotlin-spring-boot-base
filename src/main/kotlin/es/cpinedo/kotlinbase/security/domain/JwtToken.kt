package es.cpinedo.kotlinbase.security.domain

data class JwtToken(val token: String, val expirationTime: Long)