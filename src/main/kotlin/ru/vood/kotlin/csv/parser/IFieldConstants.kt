package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import ru.vood.kotlin.csv.parser.dto.NotParsedCsvLine
import ru.vood.kotlin.csv.parser.error.CsvFieldError
import ru.vood.kotlin.csv.parser.error.ICsvError
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Базовый интерфейс для enums,которые используются для обозначения искомых полей в csv-файлах.
 */
interface IFieldConstants {

    val fieldName: String

    fun getShort(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Short> =
        convertEither<Short>(this, mapHeaderWithIndex, strValues)

    fun getInt(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Int> =
        convertEither<Int>(this, mapHeaderWithIndex, strValues)


    fun getLong(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Long> =
        convertEither<Long>(this, mapHeaderWithIndex, strValues)

    fun getFloat(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Float> =
        convertEither<Float>(this, mapHeaderWithIndex, strValues)

    fun getDouble(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Double> =
        convertEither<Double>(this, mapHeaderWithIndex, strValues)

    fun getBoolean(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Boolean> =
        convertEither<Boolean>(this, mapHeaderWithIndex, strValues)

    fun getString(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, String> =
        convertEither<String>(this, mapHeaderWithIndex, strValues)

    fun getInstant(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Instant> =
        convertEither<Instant>(this, mapHeaderWithIndex, strValues)

    fun getLocalDateTime(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, LocalDateTime> =
        convertEither<LocalDateTime>(this, mapHeaderWithIndex, strValues)

    fun getLocalDate(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, LocalDate> =
        convertEither<LocalDate>(this, mapHeaderWithIndex, strValues)

    fun getByteNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Byte?> =
        convertEither<Byte?>(this, mapHeaderWithIndex, strValues)

    fun getShortNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Short?> =
        convertEither<Short?>(this, mapHeaderWithIndex, strValues)

    fun getIntNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Int?> =
        convertEither<Int?>(this, mapHeaderWithIndex, strValues)

    fun getLongNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Long?> =
        convertEither<Long?>(this, mapHeaderWithIndex, strValues)

    fun getFloatNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Float?> =
        convertEither<Float?>(this, mapHeaderWithIndex, strValues)

    fun getDoubleNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Double?> =
        convertEither<Double?>(this, mapHeaderWithIndex, strValues)

    fun getBooleanNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Boolean?> =
        convertEither<Boolean?>(this, mapHeaderWithIndex, strValues)

    fun getStringNullable(block: () -> String?): String? = block()

    fun getStringNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, String?> =
        convertEither<String?>(this, mapHeaderWithIndex, strValues)

    fun getInstantNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, Instant?> =
        convertEither<Instant?>(this, mapHeaderWithIndex, strValues)

    fun getLocalDateTimeNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, LocalDateTime?> =
        convertEither<LocalDateTime?>(this, mapHeaderWithIndex, strValues)

    fun getLocalDateNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, LocalDate?> =
        convertEither<LocalDate?>(this, mapHeaderWithIndex, strValues)

    private inline fun <reified T> convertEither(
        field: IFieldConstants,
        mapHeaderWithIndex: Map<String, Int>,
        strValues: NotParsedCsvLine
    ): Either<ICsvError, T> {
        val key = field.fieldName.lowercase()
        return ReaderCsvConverter.convertEither<T>(
            strValues.strValues[mapHeaderWithIndex.getValue(key)],
        ).fold({
            CsvFieldError(field, it).left()
        }, {
            it.right()
        }
        )

    }
}