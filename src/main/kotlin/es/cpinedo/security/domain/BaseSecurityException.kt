package es.cpinedo.security.domain

import es.cpinedo.core.domain.CoreException
import es.cpinedo.core.domain.ErrorType

class BaseSecurityException(message:String, status: ErrorType) : CoreException(message, status)