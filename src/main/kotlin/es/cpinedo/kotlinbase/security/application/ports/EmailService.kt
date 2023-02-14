package es.cpinedo.kotlinbase.security.application.ports

interface EmailService {
    fun sendSimpleMessage(to:String, subject: String, text: String)
}