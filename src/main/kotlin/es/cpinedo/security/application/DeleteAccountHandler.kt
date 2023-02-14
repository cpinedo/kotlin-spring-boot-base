package es.cpinedo.security.application

import es.cpinedo.core.domain.Handler
import es.cpinedo.security.application.DeleteAccountCommand
import es.cpinedo.security.application.ports.AuthenticatedUserService
import es.cpinedo.security.application.ports.UserService

class DeleteAccountHandler(val userService: UserService, val authenticatedUserService: AuthenticatedUserService) :
    Handler<Unit, DeleteAccountCommand> {
    override fun invoke(request: DeleteAccountCommand) {
        val user = authenticatedUserService.getAuthenticatedUser()
        userService.deleteUser(user.id)
    }
}