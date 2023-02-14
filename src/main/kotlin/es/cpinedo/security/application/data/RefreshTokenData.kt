package es.cpinedo.security.application.data

import java.time.Instant
import java.util.*

data class RefreshTokenData(
    val id: UUID,
    val userId: UUID?,
    val token: String?,
    val expiryDate: Instant?,
    val sessionExpiryDate: Instant?
)