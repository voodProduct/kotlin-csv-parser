package ru.vood.kotlin.csv.parser.converter

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import ru.vood.kotlin.csv.parser.error.CastError
import ru.vood.kotlin.csv.parser.error.ICastError
import ru.vood.kotlin.csv.parser.error.UnsupportedBooleanValueError

object CsvTypeConverter {
    val booleanFun: (value: String) -> Either<UnsupportedBooleanValueError, Boolean> = { value ->
        when (value.lowercase()) {
            "true", "1", "yes" -> true.right()
            "false", "0", "no" -> false.right()
            else -> UnsupportedBooleanValueError("Invalid boolean value: '$value'").left()
        }
    }

    val intFun: (value: String) -> Int = { it.toInt() }
    val longFun: (value: String) -> Long = { it.toLong() }
    val doubleFun: (value: String) -> Double = { it.toDouble() }
    val floatFun: (value: String) -> Float = { it.toFloat() }
    val shortFun: (value: String) -> Short = { it.toShort() }

    fun convertString(value: String): Either<ICastError, String> =
        value.right()

    fun convertBoolean(
        value: String,
        convert: (String) -> Either<UnsupportedBooleanValueError, Boolean> = booleanFun
    ): Either<UnsupportedBooleanValueError, Boolean> = convert(value)

    fun convertInt(value: String, convert: (String) -> Int = intFun): Either<CastError, Int> =
        convert<Int>(value, convert)

    fun convertLong(value: String, convert: (String) -> Long = longFun): Either<CastError, Long> =
        convert<Long>(value, convert)

    fun convertDouble(value: String, convert: (String) -> Double = doubleFun): Either<CastError, Double> =
        convert<Double>(value, convert)

    fun convertFloat(value: String, convert: (String) -> Float = floatFun): Either<ICastError, Float> =
        convert<Float>(value, convert)

    fun convertShort(value: String, convert: (String) -> Short = shortFun): Either<CastError, Short> =
        convert<Short>(value, convert)

    inline fun <reified T> convert(value: String, convert: (String) -> T): Either<CastError, T> {
        return Either.catch { convert(value) }
            .mapLeft { CastError(it::class, "Invalid ${T::class.java.canonicalName}: '$value'") }
    }
}