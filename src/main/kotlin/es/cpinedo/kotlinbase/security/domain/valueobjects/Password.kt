package es.cpinedo.kotlinbase.security.domain.valueobjects

import es.cpinedo.kotlinbase.security.domain.services.SecurityConfigurationService

class Password (
    val password: String,
    securityConfigurationService: SecurityConfigurationService? = null
) {
    object Constants {
        const val DEFAULT_PASSWORD_MIN_LENGTH: Int = 3
        const val DEFAULT_PASSWORD_MAX_LENGTH: Int = 0
    }

    init {
        val passwordMinLength = (securityConfigurationService?.passwordMinLength() ?: Constants.DEFAULT_PASSWORD_MIN_LENGTH)
        val passwordMaxLength = (securityConfigurationService?.passwordMaxLength() ?: Constants.DEFAULT_PASSWORD_MAX_LENGTH)

        require(password.length >= passwordMinLength || passwordMinLength == 0) {
            "The password must contain at least $passwordMinLength characters"
        }

        require(password.length <= passwordMaxLength || passwordMaxLength == 0) {
            "The password must contain at most $passwordMaxLength characters"
        }
    }

}
