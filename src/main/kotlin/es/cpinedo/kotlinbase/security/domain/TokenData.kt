package es.cpinedo.kotlinbase.security.domain

data class TokenData(
    val email: String,
    val username: String,
    val loginMethod: LoginMethod,
    val authorities: List<String>
)