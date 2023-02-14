package es.cpinedo.security.application.ports

interface EmailService {
    fun sendSimpleMessage(to:String, subject: String, text: String)
}