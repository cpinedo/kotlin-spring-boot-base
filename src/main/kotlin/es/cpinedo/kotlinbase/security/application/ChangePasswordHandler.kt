package es.cpinedo.kotlinbase.security.application

import es.cpinedo.kotlinbase.core.domain.Handler
import es.cpinedo.kotlinbase.security.application.ports.AuthenticatedUserService
import es.cpinedo.kotlinbase.security.application.ports.UserService
import mu.KotlinLogging

class ChangePasswordHandler(val userService: UserService, val authenticatedUserService: AuthenticatedUserService) :
    Handler<Unit, ChangePasswordCommand> {
    private val logger = KotlinLogging.logger {}
    override fun invoke(request: ChangePasswordCommand) {
        val authUser = authenticatedUserService.getAuthenticatedUser()

        userService.verifyPassword(authUser.id, request.currentPassword)
        userService.changePassword(authUser.id, request.newPassword)
    }
}