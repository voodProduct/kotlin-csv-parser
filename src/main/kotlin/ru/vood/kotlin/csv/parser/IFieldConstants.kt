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
 * Базовый интерфейс для enums, которые используются для обозначения искомых полей в csv-файлах.
 *
 * @param L Тип DTO в которую будет преобразована строка
 */
interface IFieldConstants<L : ICSVLine> {
    /**
     * Название поля в CSV файле.
     * Это значение используется для поиска соответствующего столбца в заголовке CSV.
     */
    val fieldName: String
}

/**
 * Извлекает nullable логическое значение из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [Boolean]. По умолчанию поддерживает значения:
 *                "true", "1", "yes" → `true`
 *                "false", "0", "no" → `false`
 * @return [Either] с результатом: [Right] содержит nullable [Boolean] или `null`,
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
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

/**
 * Извлекает не-null логическое значение из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [Boolean]. По умолчанию поддерживает значения:
 *                "true", "1", "yes" → `true`
 *                "false", "0", "no" → `false`
 * @return [Either] с результатом: [Right] содержит [Boolean],
 *         [Left] содержит ошибку [ICsvError] если поле не найдено, значение некорректно или null.
 */
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

/**
 * Извлекает целочисленное значение из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [Int]. По умолчанию использует [String.toInt].
 * @return [Either] с результатом: [Right] содержит [Int],
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getInt(noinline convert: (String) -> Int = { it.toInt() }): Either<ICsvError, Int> =
    get(convert)

/**
 * Извлекает nullable целочисленное значение из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [Int]. По умолчанию использует [String.toInt].
 * @return [Either] с результатом: [Right] содержит nullable [Int] или `null`,
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getIntNullable(noinline convert: (String) -> Int = { it.toInt() }): Either<ICsvError, Int?> =
    getNullable(convert)

/**
 * Извлекает значение с плавающей точкой двойной точности из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [Double]. По умолчанию использует [String.toDouble].
 * @return [Either] с результатом: [Right] содержит [Double],
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getDouble(noinline convert: (String) -> Double = { it.toDouble() }): Either<ICsvError, Double> =
    get(convert)

/**
 * Извлекает nullable значение с плавающей точкой двойной точности из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [Double]. По умолчанию использует [String.toDouble].
 * @return [Either] с результатом: [Right] содержит nullable [Double] или `null`,
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getDoubleNullable(noinline convert: (String) -> Double = { it.toDouble() }): Either<ICsvError, Double?> =
    getNullable(convert)

/**
 * Извлекает значение с плавающей точкой одинарной точности из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [Float]. По умолчанию использует [String.toFloat].
 * @return [Either] с результатом: [Right] содержит [Float],
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getFloat(noinline convert: (String) -> Float = { it.toFloat() }): Either<ICsvError, Float> =
    get(convert)

/**
 * Извлекает nullable значение с плавающей точкой одинарной точности из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [Float]. По умолчанию использует [String.toFloat].
 * @return [Either] с результатом: [Right] содержит nullable [Float] или `null`,
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getFloatNullable(noinline convert: (String) -> Float = { it.toFloat() }): Either<ICsvError, Float?> =
    getNullable(convert)

/**
 * Извлекает длинное целочисленное значение из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [Long]. По умолчанию использует [String.toLong].
 * @return [Either] с результатом: [Right] содержит [Long],
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLong(noinline convert: (String) -> Long = { it.toLong() }): Either<ICsvError, Long> =
    get(convert)

/**
 * Извлекает nullable длинное целочисленное значение из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [Long]. По умолчанию использует [String.toLong].
 * @return [Either] с результатом: [Right] содержит nullable [Long] или `null`,
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLongNullable(noinline convert: (String) -> Long = { it.toLong() }): Either<ICsvError, Long?> =
    getNullable(convert)

/**
 * Извлекает короткое целочисленное значение из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [Short]. По умолчанию использует [String.toShort].
 * @return [Either] с результатом: [Right] содержит [Short],
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getShort(noinline convert: (String) -> Short = { it.toShort() }): Either<ICsvError, Short> =
    get(convert)

/**
 * Извлекает nullable короткое целочисленное значение из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [Short]. По умолчанию использует [String.toShort].
 * @return [Either] с результатом: [Right] содержит nullable [Short] или `null`,
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getShortNullable(noinline convert: (String) -> Short = { it.toShort() }): Either<ICsvError, Short?> =
    getNullable(convert)

/**
 * Извлекает строковое значение из CSV строки по имени поля.
 *
 * @return [Either] с результатом: [Right] содержит [String],
 *         [Left] содержит ошибку [ICsvError] если поле не найдено.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getString(): Either<ICsvError, String> =
    extractValue()

/**
 * Извлекает nullable строковое значение из CSV строки по имени поля.
 *
 * @return [Either] с результатом: [Right] содержит nullable [String] или `null`,
 *         [Left] содержит ошибку [ICsvError] если поле не найдено.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getStringNullable(): Either<ICsvError, String?> =
    getNullable { it }

/**
 * Извлекает значение даты-времени из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [LocalDateTime]. По умолчанию использует [LocalDateTime.parse].
 * @return [Either] с результатом: [Right] содержит [LocalDateTime],
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLocalDateTime(
    noinline convert: (String) -> LocalDateTime = {
        LocalDateTime.parse(
            it
        )
    }
): Either<ICsvError, LocalDateTime> =
    get(convert)

/**
 * Извлекает nullable значение даты-времени из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [LocalDateTime]. По умолчанию использует [LocalDateTime.parse].
 * @return [Either] с результатом: [Right] содержит nullable [LocalDateTime] или `null`,
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLocalDateTimeNullable(
    noinline convert: (String) -> LocalDateTime = {
        LocalDateTime.parse(
            it
        )
    }
): Either<ICsvError, LocalDateTime?> =
    getNullable(convert)

/**
 * Извлекает значение момента времени (Instant) из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [Instant]. По умолчанию использует [Instant.parse].
 * @return [Either] с результатом: [Right] содержит [Instant],
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getInstant(
    noinline convert: (String) -> Instant = {
        Instant.parse(
            it
        )
    }
): Either<ICsvError, Instant> =
    get(convert)

/**
 * Извлекает nullable значение момента времени (Instant) из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [Instant]. По умолчанию использует [Instant.parse].
 * @return [Either] с результатом: [Right] содержит nullable [Instant] или `null`,
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getInstantNullable(
    noinline convert: (String) -> Instant = {
        Instant.parse(
            it
        )
    }
): Either<ICsvError, Instant?> =
    getNullable(convert)

/**
 * Извлекает значение даты из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [LocalDate]. По умолчанию использует [LocalDate.parse].
 * @return [Either] с результатом: [Right] содержит [LocalDate],
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLocalDate(
    noinline convert: (String) -> LocalDate = {
        LocalDate.parse(
            it
        )
    }
): Either<ICsvError, LocalDate> =
    get(convert)

/**
 * Извлекает nullable значение даты из CSV строки по имени поля.
 *
 * @param convert Функция преобразования строки в [LocalDate]. По умолчанию использует [LocalDate.parse].
 * @return [Either] с результатом: [Right] содержит nullable [LocalDate] или `null`,
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified L : ICSVLine> IFieldConstants<L>.getLocalDateNullable(
    noinline convert: (String) -> LocalDate = {
        LocalDate.parse(
            it
        )
    }
): Either<ICsvError, LocalDate?> =
    getNullable(convert)

/**
 * Извлекает значение перечисления из CSV строки по имени поля.
 *
 * @param E Тип перечисления
 * @param convert Функция преобразования строки в [E].
 * @return [Either] с результатом: [Right] содержит значение перечисления [E],
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified E : Enum<E>, reified L : ICSVLine> IFieldConstants<L>.getEnum(noinline convert: (String) -> E): Either<ICsvError, E> =
    get(convert)

/**
 * Извлекает nullable значение перечисления из CSV строки по имени поля.
 *
 * @param E Тип перечисления
 * @param convert Функция преобразования строки в [E].
 * @return [Either] с результатом: [Right] содержит nullable значение перечисления [E] или `null`,
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или значение некорректно.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified E : Enum<E>, reified L : ICSVLine> IFieldConstants<L>.getEnumNullable(noinline convert: (String) -> E): Either<ICsvError, E?> =
    getNullable(convert)

/**
 * Извлекает значение произвольного типа из CSV строки по имени поля.
 *
 * @param T Тип возвращаемого значения
 * @param convert Функция преобразования строки в [T].
 * @return [Either] с результатом: [Right] содержит значение типа [T],
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или преобразование не удалось.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified T, reified L : ICSVLine> IFieldConstants<L>.get(
    noinline convert: (String) -> T
): Either<ICsvError, T> =
    extractValue()
        .flatMap { convertTo(it, convert) }

/**
 * Извлекает nullable значение произвольного типа из CSV строки по имени поля.
 *
 * @param T Тип возвращаемого значения
 * @param convert Функция преобразования строки в [T].
 * @return [Either] с результатом: [Right] содержит nullable значение типа [T] или `null`,
 *         [Left] содержит ошибку [ICsvError] если поле не найдено или преобразование не удалось.
 */
context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
inline fun <reified T, reified L : ICSVLine> IFieldConstants<L>.getNullable(
    noinline convert: (String) -> T
): Either<ICsvError, T?> =
    extractValue()
        .flatMap {
            if (it == "" || it == "NULL") null.right()
            else convertTo(it, convert)
        }

/**
 * Извлекает строковое значение из CSV строки по имени поля.
 *
 * @return [Either] с результатом: [Right] содержит строковое значение,
 *         [Left] содержит ошибку [ICsvError] если поле не найдено в заголовке или в строке.
 */
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

/**
 * Выполняет валидацию значения, полученного из CSV.
 *
 * @param check Функция проверки условия. Возвращает `true` если значение валидно.
 * @param raise Функция создания ошибки в случае невалидного значения.
 * @return [Either] с результатом: [Right] содержит исходное значение если оно валидно,
 *         [Left] содержит ошибку [ICsvError] если значение не прошло проверку.
 */
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