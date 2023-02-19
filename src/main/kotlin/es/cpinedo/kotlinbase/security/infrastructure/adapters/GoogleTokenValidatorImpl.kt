package es.cpinedo.kotlinbase.security.infrastructure.adapters

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import es.cpinedo.kotlinbase.core.domain.CoreException
import es.cpinedo.kotlinbase.core.domain.ErrorType
import es.cpinedo.kotlinbase.security.application.ports.GoogleTokenValidatorService
import es.cpinedo.kotlinbase.security.domain.GoogleTokenData
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GoogleTokenValidatorImpl : GoogleTokenValidatorService {
    @Value("\${security.google.clientIds}")
    val clientIds: Array<String> = arrayOf()

    override fun validateAndExtractDataFromToken(idToken: String): GoogleTokenData {
        val verifier: GoogleIdTokenVerifier =
            GoogleIdTokenVerifier.Builder(
                NetHttpTransport(), GsonFactory()
            )
                .setAudience(clientIds.asList())
                .build()

        val verifiedToken: GoogleIdToken =
            verifier.verify(idToken) ?: throw InvalidGoogleTokenException("Invalid token", ErrorType.UNAUTHORIZED)
        val payload: GoogleIdToken.Payload = verifiedToken.payload

        val userId: String =
            payload.subject ?: throw InvalidGoogleTokenException("UserId not present in token", ErrorType.UNAUTHORIZED)

        val name = payload["name"] ?: ""
        val picture = payload["picture"] ?: ""
        val locale = payload["locale"] ?: ""
        val familyName = payload["family_name"] ?: ""
        val givenName = payload["given_name"] ?: ""

        return GoogleTokenData(
            userId = userId,
            email = payload.email,
            emailVerified = payload.emailVerified,
            name = name as String,
            pictureUrl = picture as String,
            locale = locale as String,
            familyName = familyName as String,
            givenName = givenName as String,
        )
    }
}

class InvalidGoogleTokenException(message: String, status: ErrorType) : CoreException(message, status)