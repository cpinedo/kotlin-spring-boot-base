package es.cpinedo.security.infrastructure.mail

import es.cpinedo.security.application.ports.EmailService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service


@Service
class EmailServiceImpl(val emailSender: JavaMailSender) : EmailService {

    private val logger = KotlinLogging.logger {}

    @Value("\${spring.mail.username}")
    private val mailUser: String? = null

    override fun sendSimpleMessage(to: String, subject: String, text: String) {
        logger.info { "Sending a recover password email to $to" }
        val message = SimpleMailMessage()
        message.setFrom(mailUser!!)
        message.setTo(to)
        message.setSubject(subject)
        message.setText(text)
        emailSender.send(message)
    }
}