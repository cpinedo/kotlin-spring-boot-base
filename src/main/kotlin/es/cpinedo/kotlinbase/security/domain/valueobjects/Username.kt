package es.cpinedo.kotlinbase.security.domain.valueobjects

import es.cpinedo.kotlinbase.security.domain.services.SecurityConfigurationService

class Username(
    val username: String,
    securityConfigurationService: SecurityConfigurationService? = null
) {
    object Constants {
        const val DEFAULT_USER_MIN_LENGTH: Int = 3
        const val DEFAULT_USER_MAX_LENGTH: Int = 20
    }

    init {
        val userMinLength = (securityConfigurationService?.userMinLength() ?: Constants.DEFAULT_USER_MIN_LENGTH)
        val userMaxLength = (securityConfigurationService?.userMaxLength() ?: Constants.DEFAULT_USER_MAX_LENGTH)

        require(username.length >= userMinLength || userMinLength == 0) {
            "The username must contain at least $userMinLength characters"
        }

        require(username.length <= userMaxLength || userMaxLength == 0) {
            "The username must contain at most $userMaxLength characters"
        }
    }
}