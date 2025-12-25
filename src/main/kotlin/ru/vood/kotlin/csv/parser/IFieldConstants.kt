package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.left
import arrow.core.right
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

    @Deprecated("удалить позже")
    fun getShort(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Short =
        convert<Short>(this, mapHeaderWithIndex, strValues)

    fun getShortEither(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Either<ICsvError, Short> =
        convertEither<Short>(this, mapHeaderWithIndex, strValues)


    @Deprecated("удалить позже")
    fun getInt(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Int =
        convert<Int>(this, mapHeaderWithIndex, strValues)


    fun getIntEither(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Either<ICsvError, Int> =
        convertEither<Int>(this, mapHeaderWithIndex, strValues)


    fun getLong(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Long =
        convert<Long>(this, mapHeaderWithIndex, strValues)

    fun getFloat(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Float =
        convert<Float>(this, mapHeaderWithIndex, strValues)

    fun getDouble(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Double =
        convert<Double>(this, mapHeaderWithIndex, strValues)

    fun getBoolean(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Boolean =
        convert<Boolean>(this, mapHeaderWithIndex, strValues)

    fun getString(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): String =
        convert<String>(this, mapHeaderWithIndex, strValues)

    fun getInstant(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Instant =
        convert<Instant>(this, mapHeaderWithIndex, strValues)

    fun getLocalDateTime(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): LocalDateTime =
        convert<LocalDateTime>(this, mapHeaderWithIndex, strValues)

    fun getLocalDate(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): LocalDate =
        convert<LocalDate>(this, mapHeaderWithIndex, strValues)

    fun getByteNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Byte? =
        convert<Byte?>(this, mapHeaderWithIndex, strValues)

    fun getShortNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Short? =
        convert<Short?>(this, mapHeaderWithIndex, strValues)

    fun getIntNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Int? =
        convert<Int?>(this, mapHeaderWithIndex, strValues)

    fun getLongNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Long? =
        convert<Long?>(this, mapHeaderWithIndex, strValues)

    fun getFloatNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Float? =
        convert<Float?>(this, mapHeaderWithIndex, strValues)

    fun getDoubleNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Double? =
        convert<Double?>(this, mapHeaderWithIndex, strValues)

    fun getBooleanNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Boolean? =
        convert<Boolean?>(this, mapHeaderWithIndex, strValues)

    fun getStringNullable(block: () -> String?): String? = block()

    fun getStringNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): String? =
        convert<String?>(this, mapHeaderWithIndex, strValues)

    fun getInstantNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Instant? =
        convert<Instant?>(this, mapHeaderWithIndex, strValues)

    fun getLocalDateTimeNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): LocalDateTime? =
        convert<LocalDateTime?>(this, mapHeaderWithIndex, strValues)

    fun getLocalDateNullable(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): LocalDate? =
        convert<LocalDate?>(this, mapHeaderWithIndex, strValues)

    @Deprecated("удалить позже")
    private inline fun <reified T> convert(
        field: IFieldConstants,
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): T = runCatching {
        val key = field.fieldName.lowercase()
        ReaderCsvConverter.convert<T>(
            strValues[mapHeaderWithIndex.getValue(key)]
        )
    }.getOrElse { err ->
        val errorMessage = buildString {
            append("Ошибка в процессе преобразования значения поля ${field.fieldName} в тип ${T::class.java}.\n")
            append("Причина: ${err.message}.\n")
        }
        error(errorMessage)
    }

    private inline fun <reified T> convertEither(
        field: IFieldConstants,
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>
    ): Either<ICsvError, T> {
        val key = field.fieldName.lowercase()
        return ReaderCsvConverter.convertEither<T>(
            strValues[mapHeaderWithIndex.getValue(key)],
        ).fold({
            CsvFieldError(field, it).left()
        }, {
            it.right()
        }
        )

    }
}