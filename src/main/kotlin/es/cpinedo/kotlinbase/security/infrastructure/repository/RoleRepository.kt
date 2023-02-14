package es.cpinedo.kotlinbase.security.infrastructure.repository

import es.cpinedo.kotlinbase.security.infrastructure.repository.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RoleRepository : JpaRepository<Role, UUID> {
    fun findByName(name: String): Role?
}