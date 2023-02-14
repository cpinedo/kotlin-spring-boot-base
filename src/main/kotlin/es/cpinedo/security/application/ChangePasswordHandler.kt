package es.cpinedo.security.application

import es.cpinedo.core.domain.Handler
import es.cpinedo.security.application.ports.AuthenticatedUserService
import es.cpinedo.security.application.ports.UserService
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