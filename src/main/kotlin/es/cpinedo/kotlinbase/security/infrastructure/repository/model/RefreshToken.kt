package es.cpinedo.kotlinbase.security.infrastructure.repository.model

import es.cpinedo.kotlinbase.security.application.data.RefreshTokenData
import java.time.Instant
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "refresh_token", schema = "base")
class RefreshToken(
    @Id
    var id: UUID,

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var securityUser: SecurityUserDbEntity? = null,

    @Column(nullable = false, unique = true)
    var token: String? = null,

    @Column(nullable = false)
    var expiryDate: Instant? = null,

    @Column(nullable = false)
    var sessionExpiryDate: Instant? = null
){
    fun toRefreshTokenData(): RefreshTokenData {
        return RefreshTokenData(this.id, this.securityUser?.id, this.token, this.expiryDate, this.sessionExpiryDate)
    }
}