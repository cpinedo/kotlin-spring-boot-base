package es.cpinedo.kotlinbase.security.infrastructure.repository

import es.cpinedo.kotlinbase.security.infrastructure.repository.model.RefreshToken
import es.cpinedo.kotlinbase.security.infrastructure.repository.model.SecurityUserDbEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface RefreshTokenRepository : JpaRepository<RefreshToken, UUID> {
//    fun findById(id: UUID?): Optional<RefreshToken>
    fun findByToken(token: String?): Optional<RefreshToken>
    fun deleteBySecurityUser(securityUser: SecurityUserDbEntity): Int
}