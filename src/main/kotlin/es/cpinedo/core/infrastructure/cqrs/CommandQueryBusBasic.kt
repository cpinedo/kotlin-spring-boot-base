package es.cpinedo.core.infrastructure.cqrs

import es.cpinedo.core.domain.*
import es.cpinedo.security.application.ports.AuthenticatedUserService
import mu.KotlinLogging
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class CommandQueryBusBasic(private val authenticatedUserService: AuthenticatedUserService) : CommandQueryBus() {
    private val logger = KotlinLogging.logger {}

    override fun <T> dispatch(query: Query<T>): T {
        logDispatch(query)

        return queryHandlers[query::class as KClass<Query<*>>]?.invoke(query) as T
            ?: throw HandlerNotImplementedException("No handler for query")
    }


    override fun <T> dispatch(command: Command<T>): T {
        logDispatch(command)

        return commandHandlers[command::class as KClass<Command<*>>]?.invoke(command) as T
            ?: throw HandlerNotImplementedException("No handler for command")
    }

    class HandlerNotImplementedException(message: String) : CoreException(message, ErrorType.NOT_IMPLEMENTED)

    private fun <T> logDispatch(commandOrQuery: Request<T>) {
        val what = if (!commandOrQuery::class.isData) commandOrQuery::class.simpleName else commandOrQuery.toString()
        val who = authenticatedUserService.runCatching { getAuthenticatedUser() }.getOrDefault("anonymous")
        logger.info("Dispatching $what by $who")
    }
}