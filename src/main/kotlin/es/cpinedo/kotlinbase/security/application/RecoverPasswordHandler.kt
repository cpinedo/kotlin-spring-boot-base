package es.cpinedo.kotlinbase.security.application

import es.cpinedo.kotlinbase.core.domain.CommandQueryBus
import es.cpinedo.kotlinbase.core.domain.Handler
import es.cpinedo.kotlinbase.security.application.ports.EmailService
import es.cpinedo.kotlinbase.security.application.ports.UserService
import mu.KotlinLogging

class RecoverPasswordHandler(
    val userService: UserService,
    val emailService: EmailService,
    val commandQueryBus: CommandQueryBus
) : Handler<Unit, RecoverPasswordCommand> {
    private val logger = KotlinLogging.logger {}

    override fun invoke(request: RecoverPasswordCommand) {
        val userId = userService.findByEmail(request.mail)?.id
        if (userId != null) {
            logger.warn { "Password recover email sent to ${request.mail}" }
            val pass = generateRandomPassword()

            commandQueryBus.dispatch(ChangePasswordInternalCommand(pass, userId))
            emailService.sendSimpleMessage(request.mail, "Recover password", "Your temporary password is $pass")
        } else
            logger.warn { "Password recover attempt for ${request.mail}, the email is not registered." }

    }

    private fun generateRandomPassword(): String {
        val allowedChars = ("0"[0].."9"[0]).toMutableList()
        allowedChars.addAll(("A"[0].."Z"[0]).toList())
        allowedChars.addAll(("a"[0].."z"[0]).toList())
        allowedChars.add("!"[0])
        allowedChars.addAll(("<"[0].."?"[0]).toList())

        return (1..12).map { allowedChars[(allowedChars.indices).random()] }.joinToString("")
    }

}