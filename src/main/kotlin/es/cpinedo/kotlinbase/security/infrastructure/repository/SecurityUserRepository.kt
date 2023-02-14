package es.cpinedo.kotlinbase.security.infrastructure.repository

import es.cpinedo.kotlinbase.security.infrastructure.repository.model.SecurityUserDbEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SecurityUserRepository : JpaRepository<SecurityUserDbEntity, UUID> {
    fun findByUsername(username: String): SecurityUserDbEntity?
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun findByEmail(mail: String): SecurityUserDbEntity?
}