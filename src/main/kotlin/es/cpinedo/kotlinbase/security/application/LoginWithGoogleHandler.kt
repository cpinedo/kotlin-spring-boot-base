package es.cpinedo.kotlinbase.security.application

import es.cpinedo.kotlinbase.core.domain.CommandQueryBus
import es.cpinedo.kotlinbase.core.domain.CoreException
import es.cpinedo.kotlinbase.core.domain.ErrorType
import es.cpinedo.kotlinbase.core.domain.Handler
import es.cpinedo.kotlinbase.security.application.data.RefreshTokenData
import es.cpinedo.kotlinbase.security.application.ports.GoogleTokenValidatorService
import es.cpinedo.kotlinbase.security.application.ports.JwtUtils
import es.cpinedo.kotlinbase.security.application.ports.RefreshTokenService
import es.cpinedo.kotlinbase.security.domain.JwtToken
import es.cpinedo.kotlinbase.security.domain.LoginMethod
import es.cpinedo.kotlinbase.security.infrastructure.repository.SecurityUserRepository
import es.cpinedo.kotlinbase.security.infrastructure.repository.model.RoleType
import java.util.*

class LoginWithGoogleHandler(
    private val commandQueryBus: CommandQueryBus,
    private val googleTokenValidatorService: GoogleTokenValidatorService,
    private val jwtUtils: JwtUtils,
    private val refreshTokenService: RefreshTokenService,
    private val securityUserRepository: SecurityUserRepository,
) : Handler<LoginResponse, LoginWithGoogleQuery> {
    override fun invoke(request: LoginWithGoogleQuery): LoginResponse {
        val tokenData = googleTokenValidatorService.validateAndExtractDataFromToken(request.idToken)

        val user = securityUserRepository.findByEmail(tokenData.email)?.toUserData() ?: commandQueryBus.dispatch(
            SignUpCommand(
                alias = tokenData.email,
                password = UUID.randomUUID().toString(),
                email = tokenData.email,
                roles = setOf(RoleType.USER.name.lowercase()),
            )
        ).let { securityUserRepository.findByEmail(tokenData.email)?.toUserData() }
        ?: throw GoogleLoginProblemException("Something went wrong creating user", ErrorType.INTERNAL_SERVER_ERROR)

        val jwt: JwtToken = jwtUtils.generateTokenFromEmail(user.email, LoginMethod.GOOGLE)
        val refreshToken: RefreshTokenData = refreshTokenService.createRefreshToken(user.id)

        return LoginResponse(jwt, refreshToken)
    }
}

class GoogleLoginProblemException(message: String, status: ErrorType) : CoreException(message, status)