package es.cpinedo.kotlinbase.security.domain

import es.cpinedo.kotlinbase.core.domain.CoreException
import es.cpinedo.kotlinbase.core.domain.ErrorType

class BaseSecurityException(message:String, status: ErrorType) : CoreException(message, status)