package es.cpinedo.security.application.ports

import es.cpinedo.security.application.data.RefreshTokenData
import java.util.*

interface RefreshTokenService {
    fun find(refreshTokenId: String): Optional<RefreshTokenData>

    fun createRefreshToken(userId: UUID): RefreshTokenData

    fun refreshRefreshToken(oldRefreshToken: RefreshTokenData): RefreshTokenData

    fun verifyExpiration(token: RefreshTokenData): RefreshTokenData?
}
