package es.cpinedo.kotlinbase.security.infrastructure.adapters

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import es.cpinedo.kotlinbase.core.domain.CoreException
import es.cpinedo.kotlinbase.core.domain.ErrorType
import es.cpinedo.kotlinbase.security.application.ports.GoogleTokenValidatorService
import es.cpinedo.kotlinbase.security.domain.GoogleTokenData
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

        return GoogleTokenData(
            userId = userId,
            email = payload.email,
            emailVerified = payload.emailVerified,
            name = payload["name"] as String,
            pictureUrl = payload["picture"] as String,
            locale = payload["locale"] as String,
            familyName = payload["family_name"] as String,
            givenName = payload["given_name"] as String,
        )
    }
}

class InvalidGoogleTokenException(message: String, status: ErrorType) : CoreException(message, status)