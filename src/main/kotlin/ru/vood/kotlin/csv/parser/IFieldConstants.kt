package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.raise.RaiseDSL
import arrow.core.right
import ru.vood.kotlin.csv.parser.converter.CsvTypeConverter.convertBoolean
import ru.vood.kotlin.csv.parser.converter.CsvTypeConverter.convertTo
import ru.vood.kotlin.csv.parser.dto.NotParsedCsvLine
import ru.vood.kotlin.csv.parser.dto.ParsedHeader
import ru.vood.kotlin.csv.parser.error.FieldNotFountByHeaderIndexError
import ru.vood.kotlin.csv.parser.error.HeaderFieldNotFoundError
import ru.vood.kotlin.csv.parser.error.ICsvError
import ru.vood.kotlin.csv.parser.error.UnsupportedBooleanValueError
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Базовый интерфейс для enums,которые используются для обозначения искомых полей в csv-файлах.
 */
interface IFieldConstants<L : ICSVLine> {

    val fieldName: String

}

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getBooleanNullable(
    noinline convert: (String) -> Either<UnsupportedBooleanValueError, Boolean> = { value ->
        when (value.lowercase()) {
            "true", "1", "yes" -> true.right()
            "false", "0", "no" -> false.right()
            else -> UnsupportedBooleanValueError("Invalid boolean value: '$value'").left()
        }
    }
): Either<ICsvError, Boolean?> {
    return extractValue()
        .flatMap {
            if (it == "" || it == "NULL") null.right()
            else convertBoolean(it, convert)
        }
}

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getBoolean(
    noinline convert: (String) -> Either<UnsupportedBooleanValueError, Boolean> = { value ->
        when (value.lowercase()) {
            "true", "1", "yes" -> true.right()
            "false", "0", "no" -> false.right()
            else -> UnsupportedBooleanValueError("Invalid boolean value: '$value'").left()
        }
    }
): Either<ICsvError, Boolean> =
    extractValue()
        .flatMap { convertBoolean(it, convert) }

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getInt(noinline convert: (String) -> Int = { it.toInt() }): Either<ICsvError, Int> =
    get(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getIntNullable(noinline convert: (String) -> Int = { it.toInt() }): Either<ICsvError, Int?> =
    getNullable(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getDouble(noinline convert: (String) -> Double = { it.toDouble() }): Either<ICsvError, Double> =
    get(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getDoubleNullable(noinline convert: (String) -> Double = { it.toDouble() }): Either<ICsvError, Double?> =
    getNullable(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getFloat(noinline convert: (String) -> Float = { it.toFloat() }): Either<ICsvError, Float> =
    get(convert)


context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getFloatNullable(noinline convert: (String) -> Float = { it.toFloat() }): Either<ICsvError, Float?> =
    getNullable(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLong(noinline convert: (String) -> Long = { it.toLong() }): Either<ICsvError, Long> =
    get(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLongNullable(noinline convert: (String) -> Long = { it.toLong() }): Either<ICsvError, Long?> =
    getNullable(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getShort(noinline convert: (String) -> Short = { it.toShort() }): Either<ICsvError, Short> =
    get(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getShortNullable(noinline convert: (String) -> Short = { it.toShort() }): Either<ICsvError, Short?> =
    getNullable(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getString(): Either<ICsvError, String> =
    extractValue()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getStringNullable(): Either<ICsvError, String?> =
    getNullable { it }

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLocalDateTime(
    noinline convert: (String) -> LocalDateTime = {
        LocalDateTime.parse(
            it
        )
    }
): Either<ICsvError, LocalDateTime> =
    get(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLocalDateTimeNullable(
    noinline convert: (String) -> LocalDateTime = {
        LocalDateTime.parse(
            it
        )
    }
): Either<ICsvError, LocalDateTime?> =
    getNullable(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getInstant(
    noinline convert: (String) -> Instant = {
        Instant.parse(
            it
        )
    }
): Either<ICsvError, Instant> =
    get(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getInstantNullable(
    noinline convert: (String) -> Instant = {
        Instant.parse(
            it
        )
    }
): Either<ICsvError, Instant?> =
    getNullable(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLocalDate(
    noinline convert: (String) -> LocalDate = {
        LocalDate.parse(
            it
        )
    }
): Either<ICsvError, LocalDate> =
    get(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLocalDateNullable(
    noinline convert: (String) -> LocalDate = {
        LocalDate.parse(
            it
        )
    }
): Either<ICsvError, LocalDate?> =
    getNullable(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified E : Enum<E>, reified L : ICSVLine> IFieldConstants<L>.getEnum(noinline convert: (String) -> E): Either<ICsvError, E> =
    get(convert)

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified E : Enum<E>, reified L : ICSVLine> IFieldConstants<L>.getEnumNullable(noinline convert: (String) -> E): Either<ICsvError, E?> =
    getNullable(convert)


context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified T, reified L : ICSVLine> IFieldConstants<L>.get(
    noinline convert: (String) -> T
): Either<ICsvError, T> =
    extractValue()
        .flatMap { convertTo(it, convert) }

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified T, reified L : ICSVLine> IFieldConstants<L>.getNullable(
    noinline convert: (String) -> T
): Either<ICsvError, T?> =
    extractValue()
        .flatMap {
            if (it == "" || it == "NULL") null.right()
            else convertTo(it, convert)
        }


context(
    notParsedCsvLine: NotParsedCsvLine,
    parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.extractValue(): Either<ICsvError, String> {
    return this.fieldName.lowercase()
        .let {
            (parsedHeader.headerWithIndex.getOrElse(it) { null }?.right() ?: HeaderFieldNotFoundError(this).left())
                .flatMap { fieldIndex ->
                    notParsedCsvLine.strValues.getOrNull(fieldIndex)?.right()
                        ?: FieldNotFountByHeaderIndexError(this).left()
                }
        }
}

@RaiseDSL
inline fun <T> Either<ICsvError, T>.validate(
    crossinline check: (T) -> Boolean,
    crossinline raise: (T) -> ICsvError
): Either<ICsvError, T> {
    return when (this) {
        is Either.Left<ICsvError> -> this
        is Either.Right<T> -> if (check(this.value)) {
            this
        } else {
            raise(this.value).left()
        }
    }
}