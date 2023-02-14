package es.cpinedo.kotlinbase.security.infrastructure.repository.model

import es.cpinedo.kotlinbase.security.application.data.UserData
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "users", schema = "base")
class SecurityUserDbEntity(
    @Id
    @Column
    var id: UUID,

    @Column
    var username: String,

    @Column
    var email: String,

    @Column
    var password: String,

    @Column
    var erased: Boolean,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    @Column
    var roles: MutableSet<Role>
){
    fun toUserData(): UserData {
        val roles = this.roles.mapNotNull { it.name }.toSet()
        return UserData(this.id, this.username, this.email, roles, this.erased)
    }
}