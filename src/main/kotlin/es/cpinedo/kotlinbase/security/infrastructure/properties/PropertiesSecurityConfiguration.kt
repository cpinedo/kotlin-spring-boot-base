package es.cpinedo.kotlinbase.security.infrastructure.properties

import es.cpinedo.kotlinbase.security.domain.services.SecurityConfigurationService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("security")
@ConditionalOnProperty(
    value = ["security.customLimits"],
    havingValue = "true",
    matchIfMissing = false
)
data class PropertiesSecurityConfiguration(
    var userMaxLength: Int = 20,
    var userMinLength: Int = 3,
    var passwordMaxLength: Int = 0,
    var passwordMinLength: Int = 3
) : SecurityConfigurationService {
    override fun userMaxLength(): Int = userMaxLength
    override fun userMinLength(): Int = userMinLength
    override fun passwordMaxLength(): Int = passwordMaxLength
    override fun passwordMinLength(): Int = passwordMinLength
}