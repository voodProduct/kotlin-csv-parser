package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.raise.RaiseDSL
import arrow.core.right
import ru.vood.kotlin.csv.parser.converter.CsvTypeConverter.convertBoolean
import ru.vood.kotlin.csv.parser.converter.CsvTypeConverter.convertInt
import ru.vood.kotlin.csv.parser.dto.NotParsedCsvLine
import ru.vood.kotlin.csv.parser.dto.ParsedHeader
import ru.vood.kotlin.csv.parser.error.*
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
inline fun <reified L : ICSVLine> IFieldConstants<L>.getInt(noinline convert: (String) -> Int = { it.toInt() }): Either<ICsvError, Int> {
    return extractValue()
        .flatMap { convertInt(it, convert) }
}


context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getIntNullable(noinline convert: (String) -> Int = { it.toInt() }): Either<ICsvError, Int?> {
    return extractValue()
        .flatMap {
            if (it == "" || it == "NULL") null.right()
            else convertInt(it, convert)
        }
}


context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLocalDateTimeNullable(): Either<ICsvError, LocalDateTime?> =
    this.convert<LocalDateTime?, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getInstantNullable(): Either<ICsvError, Instant?> =
    convert<Instant?, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <L : ICSVLine> IFieldConstants<L>.getStringNullable(block: () -> String?): String? = block()



context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getDoubleNullable(): Either<ICsvError, Double?> =
    convert<Double?, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getFloatNullable(): Either<ICsvError, Float?> =
    convert<Float?, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLongNullable(): Either<ICsvError, Long?> =
    convert<Long?, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getShortNullable(): Either<ICsvError, Short?> =
    convert<Short?, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLocalDate(): Either<ICsvError, LocalDate> =
    convert<LocalDate, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLocalDateTime(): Either<ICsvError, LocalDateTime> =
    convert<LocalDateTime, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getInstant(): Either<ICsvError, Instant> =
    convert<Instant, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getShort(): Either<ICsvError, Short> =
    convert<Short, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLong(): Either<ICsvError, Long> =
    convert<Long, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getFloat(): Either<ICsvError, Float> =
    convert<Float, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getDouble(): Either<ICsvError, Double> =
    convert<Double, L>()


context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getString(): Either<ICsvError, String> =
    this.convert<String, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLocalDateNullable(): Either<ICsvError, LocalDate?> =
    this.convert<LocalDate?, L>()

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


context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
@Deprecated("к удалению")
inline fun <reified T, reified L : ICSVLine> IFieldConstants<L>.convert(): Either<ICsvError, T> {
    val key = this.fieldName.lowercase()
    return (parsedHeader.headerWithIndex.getOrElse(key) { null }?.right() ?: HeaderFieldNotFoundError(this).left())
        .flatMap { fieldIndex ->
            notParsedCsvLine.strValues.getOrNull(fieldIndex)?.right()
                ?: FieldNotFountByHeaderIndexError(this).left()
        }
        .flatMap { fieldValue ->
            ReaderCsvConverter.convertEither<T>(fieldValue)
                .fold(
                    ifLeft = { CsvFieldError(this, it).left() },
                    ifRight = { it.right() }
                )
        }
}

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified E : Enum<E>, reified L : ICSVLine> IFieldConstants<L>.getEnum(convert: (String) -> E): Either<ICsvError, E> {
    val key = this.fieldName.lowercase()
    val catch = Either.catch { convert(notParsedCsvLine.strValues[parsedHeader.headerWithIndex.getValue(key)]) }
        .mapLeft {
            CsvFieldError(this, EnumCastError(it::class, it.message, E::class))

        }
    return catch

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