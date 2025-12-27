package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.raise.RaiseDSL
import arrow.core.right
import ru.vood.kotlin.csv.parser.dto.NotParsedCsvLine
import ru.vood.kotlin.csv.parser.dto.ParsedHeader
import ru.vood.kotlin.csv.parser.error.CsvFieldError
import ru.vood.kotlin.csv.parser.error.EnumCastError
import ru.vood.kotlin.csv.parser.error.ICsvError
import java.time.LocalDate

/**
 * Базовый интерфейс для enums,которые используются для обозначения искомых полей в csv-файлах.
 */
interface IFieldConstants<L : ICSVLine> {

    val fieldName: String


//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getShort(): Either<ICsvError, Short> =
//        convert<Short>(this)


//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getInstant(): Either<ICsvError, Instant> =
//        convert<Instant>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getLocalDateTime(): Either<ICsvError, LocalDateTime> =
//        convert<LocalDateTime>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getLocalDate(): Either<ICsvError, LocalDate> =
//        convert<LocalDate>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getByteNullable(): Either<ICsvError, Byte?> =
//        convert<Byte?>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getShortNullable(): Either<ICsvError, Short?> =
//        convert<Short?>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getIntNullable(): Either<ICsvError, Int?> =
//        convert<Int?>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getLongNullable(): Either<ICsvError, Long?> =
//        convert<Long?>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getFloatNullable(): Either<ICsvError, Float?> =
//        convert<Float?>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getDoubleNullable(): Either<ICsvError, Double?> =
//        convert<Double?>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getBooleanNullable(): Either<ICsvError, Boolean?> =
//        convert<Boolean?>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getStringNullable(block: () -> String?): String? = block()
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getStringNullable(): Either<ICsvError, String?> =
//        convert<String?>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getInstantNullable(): Either<ICsvError, Instant?> =
//        convert<Instant?>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getLocalDateTimeNullable(): Either<ICsvError, LocalDateTime?> =
//        this.convert<LocalDateTime?>()

}

//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getLong(): Either<ICsvError, Long> =
//        convert<Long>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getFloat(): Either<ICsvError, Float> =
//        convert<Float>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getDouble(): Either<ICsvError, Double> =
//        convert<Double>(this)
//
//    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
//    fun getBoolean(): Either<ICsvError, Boolean> =
//        convert<Boolean>(this)
//
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
inline fun<T> Either<ICsvError, T>.validate(
    crossinline check: (T) -> Boolean,
    crossinline raise: (T) -> ICsvError
) : Either<ICsvError, T>{
    return when (this) {
        is Either.Left<ICsvError> -> this
        is Either.Right<T> -> if (check(this.value)) {
            this
        } else {
            raise(this.value).left()
        }
    }
}