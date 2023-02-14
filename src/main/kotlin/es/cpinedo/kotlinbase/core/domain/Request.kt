package es.cpinedo.kotlinbase.core.domain

interface Request<out T>
interface Command<out T> : Request<T>
interface Query<out T> : Request<T>