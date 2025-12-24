package ru.vood.kotlin.csv.parser

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

    fun convertToTypeOrError(fieldValue: String, classTypeValue: String): Any? {
        return when (classTypeValue) {
            Byte::class.simpleName -> convert<Byte>(fieldValue)
            "${Byte::class.simpleName}?" -> convert<Byte?>(fieldValue)
            Short::class.simpleName -> convert<Short>(fieldValue)
            "${Short::class.simpleName}?" -> convert<Short?>(fieldValue)
            Int::class.simpleName -> convert<Int>(fieldValue)
            "${Int::class.simpleName}?" -> convert<Int?>(fieldValue)
            Long::class.simpleName -> convert<Long>(fieldValue)
            "${Long::class.simpleName}?" -> convert<Long?>(fieldValue)
            Float::class.simpleName -> convert<Float>(fieldValue)
            "${Float::class.simpleName}?" -> convert<Float?>(fieldValue)
            Double::class.simpleName -> convert<Double>(fieldValue)
            "${Double::class.simpleName}?" -> convert<Double?>(fieldValue)
            Boolean::class.simpleName -> convert<Boolean>(fieldValue)
            "${Boolean::class.simpleName}?" -> convert<Boolean?>(fieldValue)
            String::class.simpleName -> convert<String>(fieldValue)
            "${String::class.simpleName}?" -> convert<String?>(fieldValue)
            LocalDateTime::class.simpleName -> convert<LocalDateTime>(fieldValue)
            "${LocalDateTime::class.simpleName}?" -> convert<LocalDateTime?>(fieldValue)
            else -> throw IllegalStateException("Unknown Generic Type")
        }
    }

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
    fun String.toBoolean(): Boolean {
        return when (this) {
            "true" -> true
            "false" -> false
            "1" -> true
            "0" -> false
            else -> throw IllegalArgumentException("Ожидалось значение 1|0|true|false")
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