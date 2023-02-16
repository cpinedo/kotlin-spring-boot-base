package es.cpinedo.kotlinbase.security.application.ports

import es.cpinedo.kotlinbase.security.domain.GoogleTokenData

interface GoogleTokenValidatorService {
    fun validateAndExtractDataFromToken(idToken: String): GoogleTokenData
}