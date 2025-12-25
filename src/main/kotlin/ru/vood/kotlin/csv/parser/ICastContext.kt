package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.fold
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public inline fun <Error, A> castCsvLine( block: ICastContext.() -> A): Either<Error, A> {
//    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
//    return fold(block, { Either.Left(it) }, { Either.Right(it) })

    TODO()
}


interface ICastContext {



    fun castField()

    fun castDto()

}