package es.cpinedo.kotlinbase.core.domain

sealed class Either<out L, out R> {

    data class Left<out L>(val l: L) : Either<L, Nothing>()

    data class Right<out R>(val r: R) : Either<Nothing, R>()

    fun <T> fold(error: (L) -> T, success: (R) -> T): T {
        return when (this) {
            is Left -> error(l)
            is Right -> success(r)
        }
    }

    fun <R2> map(fn: (R) -> R2): Either<L, R2> {
        return when (this) {
            is Left -> this
            is Right -> Right(fn(r))
        }
    }

    fun successOrNull(): R? {
        return when (this) {
            is Left -> null
            is Right -> this.r
        }
    }

}