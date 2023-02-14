package es.cpinedo.kotlinbase.security.application

import es.cpinedo.kotlinbase.core.domain.Handler
import es.cpinedo.kotlinbase.security.application.ports.UserService
import mu.KotlinLogging

class ChangePasswordInternalHandler(val userService: UserService) :
    Handler<Unit, ChangePasswordInternalCommand> {
    private val logger = KotlinLogging.logger {}
    override fun invoke(request: ChangePasswordInternalCommand) {
        userService.changePassword(request.userId, request.newPassword)
    }
}