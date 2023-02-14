package es.cpinedo.security.application

import es.cpinedo.core.domain.Handler
import es.cpinedo.security.application.ports.UserService
import mu.KotlinLogging

class ChangePasswordInternalHandler(val userService: UserService) :
    Handler<Unit, ChangePasswordInternalCommand> {
    private val logger = KotlinLogging.logger {}
    override fun invoke(request: ChangePasswordInternalCommand) {
        userService.changePassword(request.userId, request.newPassword)
    }
}