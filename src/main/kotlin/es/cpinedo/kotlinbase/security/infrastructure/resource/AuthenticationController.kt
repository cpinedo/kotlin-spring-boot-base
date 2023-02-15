package es.cpinedo.kotlinbase.security.infrastructure.resource

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import es.cpinedo.kotlinbase.core.domain.CommandQueryBus
import es.cpinedo.kotlinbase.core.domain.CoreException
import es.cpinedo.kotlinbase.core.domain.ErrorType
import es.cpinedo.kotlinbase.security.application.*
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*


@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("/auth")
class AuthenticationController(
    val commandQueryBus: CommandQueryBus
) {
    @PostMapping("/login")
    fun authenticateUser(@RequestBody loginRequest: LoginRequest): ResponseEntity<JwtResponse> {
        val result: LoginResponse = commandQueryBus.dispatch(LoginQuery(loginRequest.username, loginRequest.password))
        return ResponseEntity.ok(JwtResponse.Builders.of(result))
    }

    @PostMapping("/signup")
    fun registerUser(@RequestBody signUpRequest: SignupRequest): ResponseEntity<MessageResponse> {
        commandQueryBus.dispatch(
            SignUpCommand(
                signUpRequest.username,
                signUpRequest.password,
                signUpRequest.email,
                signUpRequest.roles
            )
        )
        return ResponseEntity.ok(MessageResponse("User registered successfully!"))
    }

    @PostMapping("/refreshtoken")
    fun refreshtoken(
        @RequestBody request: TokenRefreshRequest,
        @RequestHeader("Authorization") authorizationHeader: String
    ): ResponseEntity<JwtResponse> {
        val jwtToken: String =
            extractTokenFromAuthorizationHeader(authorizationHeader) ?: throw JwtTokenNotPresentException()
        val result: RefreshTokenResponse = commandQueryBus.dispatch(RefreshTokenQuery(request.refreshToken, jwtToken))
        return ResponseEntity.ok(JwtResponse.Builders.of(result))
    }

    @PostMapping("/recover-password")
    fun recoverPassword(
        @RequestBody request: RecoverPasswordRequest
    ): ResponseEntity<MessageResponse> {
        commandQueryBus.dispatch(RecoverPasswordCommand(request.mail))
        return ResponseEntity.ok(MessageResponse("ok"))
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER')")
    fun changePassword(
        @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<MessageResponse> {
        commandQueryBus.dispatch(ChangePasswordCommand(request.newPassword, request.oldPassword))
        return ResponseEntity.ok(MessageResponse("ok"))
    }

    @PostMapping("/delete-account")
    @PreAuthorize("hasRole('USER')")
    fun changePassword(): ResponseEntity<MessageResponse> {
        commandQueryBus.dispatch(DeleteAccountCommand())
        return ResponseEntity.ok(MessageResponse("ok"))
    }

    @GetMapping("/auth/google")
    @PreAuthorize("hasRole('USER')")
    fun handleGoogleAuth(
        @RequestParam idTokenString: String
    ): ResponseEntity<String> {
        val verifier: GoogleIdTokenVerifier =
            GoogleIdTokenVerifier.Builder(
                NetHttpTransport(), GsonFactory()
            )
                .setAudience(listOf("407408718192.apps.googleusercontent.com"))
                .build()

        val idToken: GoogleIdToken = verifier.verify(idTokenString)
        val payload: GoogleIdToken.Payload? = idToken.payload

        // Print user identifier
        val userId: String = payload?.subject ?: throw CoreException("error", ErrorType.INTERNAL_SERVER_ERROR)
        println("User ID: $userId")

        // Get profile information from payload
        val email = payload?.email
        val emailVerified = payload?.emailVerified
        val name = payload?.get("name") as String
        val pictureUrl = payload["picture"] as String
        val locale = payload["locale"] as String
        val familyName = payload["family_name"] as String
        val givenName = payload["given_name"] as String

        return ResponseEntity.ok("$email")
    }

    class JwtTokenNotPresentException : CoreException("Missing jwt token", ErrorType.BAD_REQUEST)

    private fun extractTokenFromAuthorizationHeader(header: String): String? {
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7, header.length)
    }

    data class ChangePasswordRequest(val newPassword: String, val oldPassword: String)

    data class RecoverPasswordRequest(val mail: String)

    data class TokenRefreshRequest(val refreshToken: String)

    data class SignupRequest(val username: String, val password: String, val email: String, val roles: Set<String>)

    data class MessageResponse(val message: String)

    data class JwtResponse(
        val token: String,
        val refreshToken: String,
        val tokenExpTime: Long,
        val refreshTokenExpTime: Long
    ) {
        val type: String = "Bearer"

        object Builders {
            fun of(loginResponse: LoginResponse): JwtResponse {
                return JwtResponse(
                    loginResponse.token.token,
                    loginResponse.refreshToken.token!!,
                    loginResponse.token.expirationTime,
                    loginResponse.refreshToken.expiryDate!!.toEpochMilli()
                )
            }

            fun of(loginResponse: RefreshTokenResponse): JwtResponse {
                return JwtResponse(
                    loginResponse.token.token,
                    loginResponse.refreshToken.token!!,
                    loginResponse.token.expirationTime,
                    loginResponse.refreshToken.expiryDate!!.toEpochMilli()
                )
            }
        }
    }

    data class LoginRequest(val username: String, val password: String)

}