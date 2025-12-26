package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.left
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
interface IFieldConstants {

    val fieldName: String


    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getShort(): Either<ICsvError, Short> =
        convert<Short>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getInt(): Either<ICsvError, Int> =
        convert<Int>(this)


    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getLong(): Either<ICsvError, Long> =
        convert<Long>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getFloat(): Either<ICsvError, Float> =
        convert<Float>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getDouble(): Either<ICsvError, Double> =
        convert<Double>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getBoolean(): Either<ICsvError, Boolean> =
        convert<Boolean>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getString(): Either<ICsvError, String> =
        convert<String>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getInstant(): Either<ICsvError, Instant> =
        convert<Instant>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getLocalDateTime(): Either<ICsvError, LocalDateTime> =
        convert<LocalDateTime>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getLocalDate(): Either<ICsvError, LocalDate> =
        convert<LocalDate>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getByteNullable(): Either<ICsvError, Byte?> =
        convert<Byte?>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getShortNullable(): Either<ICsvError, Short?> =
        convert<Short?>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getIntNullable(): Either<ICsvError, Int?> =
        convert<Int?>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getLongNullable(): Either<ICsvError, Long?> =
        convert<Long?>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getFloatNullable(): Either<ICsvError, Float?> =
        convert<Float?>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getDoubleNullable(): Either<ICsvError, Double?> =
        convert<Double?>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getBooleanNullable(): Either<ICsvError, Boolean?> =
        convert<Boolean?>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getStringNullable(block: () -> String?): String? = block()

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getStringNullable(): Either<ICsvError, String?> =
        convert<String?>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getInstantNullable(): Either<ICsvError, Instant?> =
        convert<Instant?>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getLocalDateTimeNullable(): Either<ICsvError, LocalDateTime?> =
        convert<LocalDateTime?>(this)

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    fun getLocalDateNullable(): Either<ICsvError, LocalDate?> =
        convert<LocalDate?>(this)

}

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
 inline fun <reified T> IFieldConstants.convert(
    field: IFieldConstants,
): Either<ICsvError, T> {
    val key = field.fieldName.lowercase()
    return ReaderCsvConverter.convertEither<T>(
        notParsedCsvLine.strValues[parsedHeader.headerWithIndex.getValue(key)],
    ).fold({
        CsvFieldError(field, it).left()
    }, {
        it.right()
    }
    )

}

context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified E: Enum<E>> IFieldConstants.getEnum(convert: (String) -> E): Either<ICsvError, E> {
    val key = this.fieldName.lowercase()
    val catch = Either.catch { convert(notParsedCsvLine.strValues[parsedHeader.headerWithIndex.getValue(key)]) }
        .mapLeft {
            CsvFieldError(this, EnumCastError(it::class, it.message, E::class))

        }
    return catch

}
