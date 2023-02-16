## How to use it

Add this configuration:
```kotlin
@SpringBootApplication(
scanBasePackages = [
"project.base.package",
"es.cpinedo.kotlinbase"]
)
@EnableJpaRepositories(
basePackages = [
"project.base.package",
"es.cpinedo.kotlinbase.security.infrastructure.repository"]
)
@EntityScan(
basePackages = [
"project.base.package",", 
"es.cpinedo.kotlinbase.security.infrastructure.repository.model"
])
```

Implement the createUserCommand Handler

```kotlin
class CreateUserHandler() : Handler<Unit, CreateUserCommand> {
    override fun invoke(request: CreateUserCommand) {
        TODO("User created business logic")
    }
}
```

Register the handlers in a command query bus implementation.

```kotlin
@Bean
    fun commandQueryBusConfiguration(
        jwtUtils: JwtUtils,
        refreshTokenService: RefreshTokenServiceImpl,
        authenticationService: AuthenticationService,
        userService: UserService,
        emailService: EmailService,
        securityUserRepository: SecurityUserRepository,
        googleTokenValidatorService: GoogleTokenValidatorService,
    ): CommandQueryBus {
        return CommandQueryBusBasic(authenticatedUserService).apply {
            registerQueryHandler(DoLoginHandler(authenticationService, jwtUtils, refreshTokenService))
            registerQueryHandler(LoginWithGoogleHandler(this, googleTokenValidatorService, jwtUtils, refreshTokenService, securityUserRepository))
            registerCommandHandler(SignUpHandler(userService, this))
            registerQueryHandler(RefreshTokenHandler(refreshTokenService, userService, jwtUtils))
            registerCommandHandler(RecoverPasswordHandler(userService, emailService, this))
            registerCommandHandler(ChangePasswordHandler(userService, authenticatedUserService))
            registerCommandHandler(ChangePasswordInternalHandler(userService))
        }
    }
```