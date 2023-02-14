package es.cpinedo.kotlinbase.core.domain

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
abstract class CommandQueryBus {
    val queryHandlers = mutableMapOf<KClass<Query<*>>, Handler<*, Query<*>>>()
    val commandHandlers = mutableMapOf<KClass<Command<*>>, Handler<*, Command<*>>>()

    inline fun <reified T, reified Q : Query<T>> registerQueryHandler(handler: Handler<T, Q>) {
        queryHandlers[Q::class as KClass<Query<*>>] = handler as Handler<*, Query<*>>
    }


    inline fun <reified T, reified C : Command<T>> registerCommandHandler(handler: Handler<T, C>) {
        commandHandlers[C::class as KClass<Command<*>>] = handler as Handler<*, Command<*>>
    }

    abstract fun <T> dispatch(query: Query<T>): T

    abstract fun <T> dispatch(command: Command<T>): T
}