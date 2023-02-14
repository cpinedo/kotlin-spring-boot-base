package es.cpinedo.core.domain

interface Handler<T, R : Request<T>> {
    operator fun invoke(request: R): T
}