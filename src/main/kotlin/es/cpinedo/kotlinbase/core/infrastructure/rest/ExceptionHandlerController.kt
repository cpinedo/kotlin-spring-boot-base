package es.cpinedo.kotlinbase.core.infrastructure.rest

import es.cpinedo.kotlinbase.core.domain.CoreException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class ExceptionHandlerController : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [CoreException::class])
    protected fun handleSecurityConflict(ex: RuntimeException, request: WebRequest): ResponseEntity<Any> {
        val source: String = ex::class.simpleName ?: "UNKNOWN"
        if (ex !is CoreException)
            throw ex
        return handleExceptionInternal(
            ex, ExceptionBody(ex.message, source),
            HttpHeaders(), HttpStatus.valueOf(ex.status.name), request
        )
    }

    data class ExceptionBody(val message: String, val source: String)
}