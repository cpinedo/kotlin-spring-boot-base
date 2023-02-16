package es.cpinedo.kotlinbase.security.domain.valueobjects

import es.cpinedo.kotlinbase.security.domain.services.SecurityConfigurationService

class Password (
    val password: String,
    securityConfigurationService: SecurityConfigurationService? = null
) {
    init {
        val passwordMinLength = securityConfigurationService?.passwordMinLength()
        val passwordMaxLength = securityConfigurationService?.passwordMaxLength()

        if(passwordMinLength != null) {
            require(password.length >= passwordMinLength || passwordMinLength == 0) {
                "The password must contain at least $passwordMinLength characters"
            }
        }

        if(passwordMaxLength != null) {
            require(password.length <= passwordMaxLength || passwordMaxLength == 0) {
                "The password must contain at most $passwordMaxLength characters"
            }
        }
    }

}
