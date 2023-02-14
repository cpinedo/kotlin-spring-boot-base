package es.cpinedo.kotlinbase.security.infrastructure.adapters

import es.cpinedo.kotlinbase.core.domain.ErrorType
import es.cpinedo.kotlinbase.core.domain.CoreException
import es.cpinedo.kotlinbase.security.application.data.RefreshTokenData
import es.cpinedo.kotlinbase.security.application.ports.RefreshTokenService
import es.cpinedo.kotlinbase.security.infrastructure.configuration.SecurityConfigurationValues
import es.cpinedo.kotlinbase.security.infrastructure.repository.RefreshTokenRepository
import es.cpinedo.kotlinbase.security.infrastructure.repository.SecurityUserRepository
import es.cpinedo.kotlinbase.security.infrastructure.repository.model.RefreshToken
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class RefreshTokenServiceImpl(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val securityUserRepository: SecurityUserRepository,
    private val configurationValues: SecurityConfigurationValues
) : RefreshTokenService {

    override fun find(refreshTokenId: String): Optional<RefreshTokenData> {
        return refreshTokenRepository.findByToken(refreshTokenId).map { it.toRefreshTokenData() }
    }

    override fun createRefreshToken(userId: UUID): RefreshTokenData {
        var refreshToken = RefreshToken(UUID.randomUUID())
        refreshToken.securityUser = securityUserRepository.findById(userId).orElse(null)
        refreshToken.expiryDate = Instant.now().plusMillis(configurationValues.refreshTokenDurationMs)
        refreshToken.token = UUID.randomUUID().toString()
        refreshToken.sessionExpiryDate = Instant.now().plusMillis(configurationValues.sessionDurationMs)
        refreshToken = refreshTokenRepository.save(refreshToken)
        return refreshToken.toRefreshTokenData()
    }

    override fun refreshRefreshToken(oldRefreshToken: RefreshTokenData): RefreshTokenData {
        refreshTokenRepository.findById(oldRefreshToken.id).let {
            val expiryDate = Instant.now().plusMillis(configurationValues.refreshTokenDurationMs)
            if(expiryDate.isBefore(oldRefreshToken.sessionExpiryDate))
                it.get().expiryDate = expiryDate
            else
                it.get().expiryDate = oldRefreshToken.sessionExpiryDate
            it.get().token = UUID.randomUUID().toString()
            return refreshTokenRepository.save(it.get()).toRefreshTokenData()
        }
    }

    override fun verifyExpiration(token: RefreshTokenData): RefreshTokenData? {
        if ((token.expiryDate == null) || (token.expiryDate < Instant.now()) || (token.sessionExpiryDate == null) || (token.sessionExpiryDate < Instant.now())) {
            refreshTokenRepository.findById(token.id).let {
                refreshTokenRepository.delete(it.get())
            }
            throw TokenRefreshException("Refresh token was expired. Please make a new signin request")
        }
        return token
    }

    class TokenRefreshException(message: String) : CoreException(message, ErrorType.UNAUTHORIZED)
}