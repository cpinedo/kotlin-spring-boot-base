package es.cpinedo.kotlinbase.security.infrastructure.resource

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

    @PostMapping("/google")
    fun handleGoogleAuth(
        @RequestBody googleLoginRequest: GoogleLoginRequest
    ): ResponseEntity<JwtResponse> {
        return commandQueryBus.dispatch(LoginWithGoogleQuery(googleLoginRequest.idToken))
            .let { ResponseEntity.ok(JwtResponse.Builders.of(it)) }
    }

    @PostMapping("/signup")
    fun registerUser(@RequestBody signUpRequest: SignupRequest): ResponseEntity<MessageResponse> {
        commandQueryBus.dispatch(
            SignUpCommand(
                alias = signUpRequest.alias,
                password = signUpRequest.password,
                email = signUpRequest.email,
                roles = setOf("user"),
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

    data class SignupRequest(val alias: String, val password: String, val email: String, val roles: Set<String>)

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
    data class GoogleLoginRequest(val idToken: String)

}