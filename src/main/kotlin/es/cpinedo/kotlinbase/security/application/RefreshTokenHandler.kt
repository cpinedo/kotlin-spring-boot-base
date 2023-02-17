package es.cpinedo.kotlinbase.security.application

import es.cpinedo.kotlinbase.core.domain.Handler
import es.cpinedo.kotlinbase.core.domain.ErrorType
import es.cpinedo.kotlinbase.security.application.data.RefreshTokenData
import es.cpinedo.kotlinbase.security.application.ports.JwtUtils
import es.cpinedo.kotlinbase.security.application.ports.RefreshTokenService
import es.cpinedo.kotlinbase.security.application.ports.UserService
import es.cpinedo.kotlinbase.security.domain.BaseSecurityException
import es.cpinedo.kotlinbase.security.domain.JwtToken

data class RefreshTokenResponse(val token: JwtToken, val refreshToken: RefreshTokenData)

class RefreshTokenHandler(
    private val refreshTokenService: RefreshTokenService,
    private val userService: UserService,
    private val jwtUtils: JwtUtils
) : Handler<RefreshTokenResponse, RefreshTokenQuery> {
    override fun invoke(request: RefreshTokenQuery): RefreshTokenResponse {
        return refreshTokenService.find(request.refreshToken)
            .filter { refreshToken ->
                val user = refreshToken.userId?.let { userService.findUserById(it) }
                user?.email?.equals(jwtUtils.getEmailFromExpiredJwtToken(request.token)) ?: false
            }
            .map(refreshTokenService::verifyExpiration)
            .map { oldRefreshToken ->
                val user = oldRefreshToken?.userId?.let { userService.findUserById(it) }
                val jwt: JwtToken = jwtUtils.generateTokenFromEmail(user!!.email, jwtUtils.getTokenData(request.token, true).loginMethod)
                val refreshToken: RefreshTokenData = refreshTokenService.refreshRefreshToken(oldRefreshToken)

                RefreshTokenResponse(jwt, refreshToken)
            }
            .orElseThrow {
                BaseSecurityException("Refresh token not found!", ErrorType.UNAUTHORIZED)
            }
    }

}