package es.cpinedo.kotlinbase.security.application.ports

import es.cpinedo.kotlinbase.security.domain.JwtToken
import es.cpinedo.kotlinbase.security.domain.LoginMethod
import es.cpinedo.kotlinbase.security.domain.TokenData


interface JwtUtils {
    fun generateTokenFromEmail(email: String, loginMethod: LoginMethod): JwtToken
    fun getEmailFromExpiredJwtToken(token: String): String
    fun getUserNameFromJwtToken(token: String?): String
    fun validateJwtToken(authToken: String?): Boolean
    fun buildToken(username: String, email: String, loginMethod: LoginMethod, grantedAuthorities: Array<String>): JwtToken
    fun getTokenData(token: String, ignoreExpired: Boolean): TokenData
}