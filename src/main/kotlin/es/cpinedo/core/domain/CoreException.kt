package es.cpinedo.core.domain

open class CoreException(override val message:String, val status: ErrorType): RuntimeException()