package es.cpinedo.kotlinbase.security.domain.valueobjects

import es.cpinedo.kotlinbase.security.domain.services.SecurityConfigurationService

class Username(
    val username: String,
    securityConfigurationService: SecurityConfigurationService? = null
) {
    init {
        val userMinLength = securityConfigurationService?.userMinLength()
        val userMaxLength = securityConfigurationService?.userMaxLength()

        if (userMinLength != null) {
            require(username.length >= userMinLength || userMinLength == 0) {
                "The username must contain at least $userMinLength characters"
            }
        }

        if (userMaxLength != null) {
            require(username.length <= userMaxLength || userMaxLength == 0) {
                "The username must contain at most $userMaxLength characters"
            }
        }
    }
}