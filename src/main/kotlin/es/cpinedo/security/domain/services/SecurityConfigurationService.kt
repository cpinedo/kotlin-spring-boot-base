package es.cpinedo.security.domain.services

interface SecurityConfigurationService {
    fun userMaxLength(): Int
    fun userMinLength(): Int
    fun passwordMaxLength(): Int
    fun passwordMinLength(): Int
}