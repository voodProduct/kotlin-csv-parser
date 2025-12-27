package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.RaiseDSL
import arrow.core.right
import ru.vood.kotlin.csv.parser.dto.NotParsedCsvLine
import ru.vood.kotlin.csv.parser.dto.ParsedHeader
import ru.vood.kotlin.csv.parser.error.CsvFieldError
import ru.vood.kotlin.csv.parser.error.EnumCastError
import ru.vood.kotlin.csv.parser.error.ICsvError
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
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLocalDateTimeNullable(): Either<ICsvError, LocalDateTime?> =
    this.convert<LocalDateTime?, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getInstantNullable(): Either<ICsvError, Instant?> =
    convert<Instant?, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <L : ICSVLine> IFieldConstants<L>.getStringNullable(block: () -> String?): String? = block()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getBooleanNullable(): Either<ICsvError, Boolean?> =
    convert<Boolean?, L>()

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
inline fun <reified L : ICSVLine> IFieldConstants<L>.getIntNullable(): Either<ICsvError, Int?> =
    convert<Int?, L>()

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
inline fun <reified L : ICSVLine> IFieldConstants<L>.getBoolean(): Either<ICsvError, Boolean> =
    convert<Boolean, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getString(): Either<ICsvError, String> =
    this.convert<String, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getInt(): Either<ICsvError, Int> =
    this.convert<Int, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLocalDateNullable(): Either<ICsvError, LocalDate?> =
    this.convert<LocalDate?, L>()

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified T, reified L : ICSVLine> IFieldConstants<L>.convert(): Either<ICsvError, T> {
    val key = this.fieldName.lowercase()
    return ReaderCsvConverter.convertEither<T>(
        notParsedCsvLine.strValues[parsedHeader.headerWithIndex.getValue(key)],
    ).fold({
        CsvFieldError(this, it).left()
    }, {
        it.right()
    }
    )

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