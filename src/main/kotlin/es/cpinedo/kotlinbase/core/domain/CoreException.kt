package es.cpinedo.kotlinbase.core.domain

open class CoreException(override val message:String, val status: ErrorType): RuntimeException()