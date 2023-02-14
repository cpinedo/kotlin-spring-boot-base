package es.cpinedo.kotlinbase.security.application

import es.cpinedo.kotlinbase.core.domain.Handler
import es.cpinedo.kotlinbase.security.application.data.RefreshTokenData
import es.cpinedo.kotlinbase.security.application.ports.AuthenticationService
import es.cpinedo.kotlinbase.security.application.ports.JwtUtils
import es.cpinedo.kotlinbase.security.application.ports.RefreshTokenService
import es.cpinedo.kotlinbase.security.domain.JwtToken
import es.cpinedo.kotlinbase.security.domain.SecurityUserEntity

data class LoginResponse(val token: JwtToken, val refreshToken: RefreshTokenData)

class DoLoginHandler(
    private val authenticationService: AuthenticationService,
    private val jwtUtils: JwtUtils,
    private val refreshTokenService: RefreshTokenService
) : Handler<LoginResponse, LoginQuery> {
    override fun invoke(request: LoginQuery): LoginResponse {
        val user: SecurityUserEntity = authenticationService.performAuthentication(
            request.user,
            request.password
        )
        val jwt: JwtToken = jwtUtils.generateTokenFromUsername(user.username.username)
        val refreshToken: RefreshTokenData = refreshTokenService.createRefreshToken(user.id.id)

        return LoginResponse(jwt, refreshToken)
    }
}


