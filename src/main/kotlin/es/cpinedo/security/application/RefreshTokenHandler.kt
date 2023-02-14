package es.cpinedo.security.application

import es.cpinedo.core.domain.Handler
import es.cpinedo.core.domain.ErrorType
import es.cpinedo.security.application.data.RefreshTokenData
import es.cpinedo.security.application.ports.JwtUtils
import es.cpinedo.security.application.ports.RefreshTokenService
import es.cpinedo.security.application.ports.UserService
import es.cpinedo.security.domain.BaseSecurityException
import es.cpinedo.security.domain.JwtToken

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
                user?.username?.equals(jwtUtils.getUserNameFromExpiredJwtToken(request.token)) ?: false
            }
            .map(refreshTokenService::verifyExpiration)
            .map { oldRefreshToken ->
                val user = oldRefreshToken?.userId?.let { userService.findUserById(it) }
                val jwt: JwtToken = jwtUtils.generateTokenFromUsername(user!!.username)
                val refreshToken: RefreshTokenData = refreshTokenService.refreshRefreshToken(oldRefreshToken)

                RefreshTokenResponse(jwt, refreshToken)
            }
            .orElseThrow {
                BaseSecurityException("Refresh token not found!", ErrorType.UNAUTHORIZED)
            }
    }

}