package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import ru.vood.kotlin.csv.parser.error.CastError
import ru.vood.kotlin.csv.parser.error.ICastError
import ru.vood.kotlin.csv.parser.error.ICsvError
import ru.vood.kotlin.csv.parser.error.UnsupportedBooleanValueError
import ru.vood.kotlin.csv.parser.error.UnsupportedClassError
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object ReaderCsvConverter {
    private val TIMEZONE = ZoneOffset.UTC
    private val DATE_FORMAT = mapOf(
        "yyyy-MM-dd" to LocalDate::class,
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'" to LocalDateTime::class,
    )

    @Deprecated("удалить позже")
    inline fun <reified T> convert(fieldValue: String): T {
        if (fieldValue == "" || fieldValue == "NULL") return null as T
        return when (T::class) {
            Byte::class -> fieldValue.toByte() as T
            Short::class -> fieldValue.toShort() as T
            Int::class -> fieldValue.toInt() as T
            Long::class -> fieldValue.toLong() as T
            Float::class -> fieldValue.toFloat() as T
            Double::class -> fieldValue.toDouble() as T
            Boolean::class -> fieldValue.toBoolean() as T
            String::class -> fieldValue as T
            LocalDateTime::class -> fieldValue.toLocalDateTime() as T
            LocalDate::class -> fieldValue.toLocalDateTime().toLocalDate() as T
            else -> throw IllegalStateException("Unknown Generic Type")
        }
    }

    //    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> convertEither(fieldValue: String): Either<ICastError, T> {
        if (fieldValue == "" || fieldValue == "NULL") return (null as T).right()
        val either = when (T::class) {
            Boolean::class -> fieldValue.toBoolean() as Either<ICastError, T>
            Short::class -> fieldValue.tryCast<T> { it.toShort() as T }
            Int::class -> fieldValue.tryCast { it.toInt() as T }
            Long::class -> fieldValue.tryCast { it.toLong() as T }
            Float::class -> fieldValue.tryCast { it.toFloat() as T }
            Double::class -> fieldValue.tryCast { it.toDouble() as T }
            String::class -> fieldValue.tryCast { it as T }
            LocalDateTime::class -> fieldValue.tryCast { it.toLocalDateTime() as T }
            LocalDate::class -> fieldValue.tryCast { it.toLocalDateTime() as T }
            else -> UnsupportedClassError("Unknown Generic Type ${T::class.java.canonicalName}").left()
        }
        return either
    }

    inline fun <reified T> convertEither(fieldValue: String, fCast: (String) -> T): Either<ICastError, T> {
        if (fieldValue == "" || fieldValue == "NULL") return (null as T).right()
        return fieldValue.tryCast<T> { fCast(it) }
    }

    inline fun <reified T> String.tryCast(fCast: (String) -> T): Either<ICastError, T> {
        return Either.catch {
            fCast(this)
        }.fold(
            { CastError(it::class, it.message!!).left() },
            { it.right() }
        ) as Either<ICastError, T>

    }

    fun String.toBoolean(): Either<ICastError, Boolean> {
        return when (this) {
            "true" -> true.right()
            "false" -> false.right()
            "1" -> true.right()
            "0" -> false.right()
            else -> UnsupportedBooleanValueError("Ожидалось значение 1|0|true|false").left()
        }
    }

    fun String.toLocalDateTime(): LocalDateTime {
        DATE_FORMAT.forEach { (format, timeClass) ->
            kotlin.runCatching {
                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(format).withZone(ZoneOffset.UTC)
                return when (timeClass) {
                    LocalTime::class -> timeConverter<LocalTime>(this, formatter).atDate(LocalDate.now())
                    LocalDate::class -> timeConverter<LocalDate>(this, formatter).atStartOfDay()
                    LocalDateTime::class -> timeConverter<LocalDateTime>(this, formatter)
                    else -> error("Unknown Date Format")
                }
            }
        }
        error("Unknown Date Format")
    }

    private inline fun <reified T> timeConverter(value: String, formatter: DateTimeFormatter): T {
        return when (T::class) {
            LocalTime::class -> LocalTime.parse(value, formatter) as T
            LocalDate::class -> LocalDate.parse(value, formatter) as T
            LocalDateTime::class -> LocalDateTime.parse(value, formatter) as T
            else -> error("Unknown Date Format")
        }
    }
}