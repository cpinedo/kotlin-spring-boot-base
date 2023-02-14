package es.cpinedo.security.infrastructure.repository.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "roles", schema = "base")
class Role (@Id var id: UUID, @Column var role: RoleType, @Column var name: String?)

