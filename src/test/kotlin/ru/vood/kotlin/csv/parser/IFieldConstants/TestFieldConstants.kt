package ru.vood.kotlin.csv.parser.IFieldConstants

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import ru.vood.kotlin.csv.parser.ICSVLine
import ru.vood.kotlin.csv.parser.IFieldConstants
import ru.vood.kotlin.csv.parser.convert
import ru.vood.kotlin.csv.parser.dto.NotParsedCsvLine
import ru.vood.kotlin.csv.parser.dto.ParsedHeader
import ru.vood.kotlin.csv.parser.error.CsvFieldError
import ru.vood.kotlin.csv.parser.error.EnumCastError
import ru.vood.kotlin.csv.parser.error.ICastError
import ru.vood.kotlin.csv.parser.error.ICsvError
import ru.vood.kotlin.csv.parser.error.UnsupportedBooleanValueError
import ru.vood.kotlin.csv.parser.error.UnsupportedClassError
import ru.vood.kotlin.csv.parser.getBoolean
import ru.vood.kotlin.csv.parser.getBooleanNullable
import ru.vood.kotlin.csv.parser.getDouble
import ru.vood.kotlin.csv.parser.getDoubleNullable
import ru.vood.kotlin.csv.parser.getEnum
import ru.vood.kotlin.csv.parser.getFloat
import ru.vood.kotlin.csv.parser.getFloatNullable
import ru.vood.kotlin.csv.parser.getInstant
import ru.vood.kotlin.csv.parser.getInstantNullable
import ru.vood.kotlin.csv.parser.getInt
import ru.vood.kotlin.csv.parser.getIntNullable
import ru.vood.kotlin.csv.parser.getLocalDate
import ru.vood.kotlin.csv.parser.getLocalDateNullable
import ru.vood.kotlin.csv.parser.getLocalDateTime
import ru.vood.kotlin.csv.parser.getLocalDateTimeNullable
import ru.vood.kotlin.csv.parser.getLong
import ru.vood.kotlin.csv.parser.getLongNullable
import ru.vood.kotlin.csv.parser.getShort
import ru.vood.kotlin.csv.parser.getShortNullable
import ru.vood.kotlin.csv.parser.getString
import ru.vood.kotlin.csv.parser.getStringNullable
import ru.vood.kotlin.csv.parser.validate
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

// Тестовые классы для имитации реальных структур
data class TestCsvLine(val data: Map<String, String>) : ICSVLine

enum class TestEnum {
    VALUE1, VALUE2
}

data object TestField : IFieldConstants<TestCsvLine> {
    override val fieldName = "testField"
}

object TestField2 : IFieldConstants<TestCsvLine> {
    override val fieldName = "testField2"
}

// Mock конвертера
object ReaderCsvConverter {
   /* inline fun <reified T> convertEither(value: String): Either<ICastError, T?> {
        val either = when (T::class) {
            String::class -> Either.Right(value as T)
            Int::class -> (value.toInt() as T).right()
            Long::class -> (value.toInt() as T).right()
//            Short::class -> value.toShortOrNull()?.right() ?:
//            IllegalStateException("Invalid Short").left()
//            Float::class -> value.toFloatOrNull()?.right() ?:
//            IllegalStateException("Invalid Float").left()
//            Double::class -> value.toDoubleOrNull()?.right() ?:
//            IllegalStateException("Invalid Double").left()
//            Boolean::class -> when (value.lowercase()) {
//                "true", "1" -> true.right()
//                "false", "0" -> false.right()
//                else -> IllegalStateException("Invalid Boolean").left()
//            }
//            LocalDate::class -> LocalDate.parse(value).right()
//            LocalDateTime::class -> LocalDateTime.parse(value).right()
//            Instant::class -> Instant.parse(value).right()
            else -> if (T::class.simpleName?.endsWith("?") == true) {
                // Nullable типы
                if (value.isEmpty()) null.right() else
                    IllegalStateException("Not implemented for nullable").left()
            } else {
                IllegalStateException("Not implemented").left()
            }
        }
        return either.mapLeft { error ->
            UnsupportedClassError(error.message ?: "Unknown error")
//            object : ICastError {
//                override fun toString() = error.message ?: "Unknown error"
//            }
        }
    }*/
}

class CsvParserTest : FunSpec({

    val validHeader = ParsedHeader(mapOf("testfield" to 0, "testfield2" to 1))

    test("getString should return valid value") {
        val line = NotParsedCsvLine(listOf("testValue", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getString()
                result shouldBe Either.Right("testValue")
            }
        }
    }

    test("getString should return error for missing column") {
        val line = NotParsedCsvLine(listOf("testValue"))
        val invalidHeader = ParsedHeader(mapOf("wrongfield" to 0))

        with(line) {
            with(invalidHeader) {
                val result = TestField.getString()
                result.shouldBeInstanceOf<Either.Left<CsvFieldError>>()
            }
        }
    }

    test("getInt should return valid integer") {
        val line = NotParsedCsvLine(listOf("42", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getInt()
                result shouldBe Either.Right(42)
            }
        }
    }

    test("getInt should return error for invalid integer") {
        val line = NotParsedCsvLine(listOf("not_a_number", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getInt()
                result.shouldBeInstanceOf<Either.Left<CsvFieldError>>()
            }
        }
    }

    test("getIntNullable should return null for empty string") {
        val line = NotParsedCsvLine(listOf("", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getIntNullable()
                result shouldBe Either.Right(null)
            }
        }
    }

    test("getIntNullable should return valid integer") {
        val line = NotParsedCsvLine(listOf("42", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getIntNullable()
                result shouldBe Either.Right(42)
            }
        }
    }

    test("getLong should return valid long") {
        val line = NotParsedCsvLine(listOf("1234567890123", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getLong()
                result shouldBe Either.Right(1234567890123L)
            }
        }
    }

    test("getLongNullable should return null or value") {
        val line1 = NotParsedCsvLine(listOf("", "other"))
        val line2 = NotParsedCsvLine(listOf("123", "other"))

        with(line1) {
            with(validHeader) {
                TestField.getLongNullable() shouldBe Either.Right(null)
            }
        }

        with(line2) {
            with(validHeader) {
                TestField.getLongNullable() shouldBe Either.Right(123L)
            }
        }
    }

    test("getShort should return valid short") {
        val line = NotParsedCsvLine(listOf("123", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getShort()
                result shouldBe Either.Right(123.toShort())
            }
        }
    }

    test("getShortNullable should handle null and values") {
        val line1 = NotParsedCsvLine(listOf("", "other"))
        val line2 = NotParsedCsvLine(listOf("123", "other"))

        with(line1) {
            with(validHeader) {
                TestField.getShortNullable() shouldBe Either.Right(null)
            }
        }

        with(line2) {
            with(validHeader) {
                TestField.getShortNullable() shouldBe Either.Right(123.toShort())
            }
        }
    }

    test("getFloat should return valid float") {
        val line = NotParsedCsvLine(listOf("3.14", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getFloat()
                result shouldBe Either.Right(3.14f)
            }
        }
    }

    test("getFloatNullable should handle null and values") {
        val line1 = NotParsedCsvLine(listOf("", "other"))
        val line2 = NotParsedCsvLine(listOf("3.14", "other"))

        with(line1) {
            with(validHeader) {
                TestField.getFloatNullable() shouldBe Either.Right(null)
            }
        }

        with(line2) {
            with(validHeader) {
                TestField.getFloatNullable() shouldBe Either.Right(3.14f)
            }
        }
    }

    test("getDouble should return valid double") {
        val line = NotParsedCsvLine(listOf("3.14159265359", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getDouble()
                result shouldBe Either.Right(3.14159265359)
            }
        }
    }

    test("getDoubleNullable should handle null and values") {
        val line1 = NotParsedCsvLine(listOf("", "other"))
        val line2 = NotParsedCsvLine(listOf("3.14", "other"))

        with(line1) {
            with(validHeader) {
                TestField.getDoubleNullable() shouldBe Either.Right(null)
            }
        }

        with(line2) {
            with(validHeader) {
                TestField.getDoubleNullable() shouldBe Either.Right(3.14)
            }
        }
    }

    test("getBoolean should return valid boolean") {
        val trueLine = NotParsedCsvLine(listOf("true", "other"))
        val falseLine = NotParsedCsvLine(listOf("false", "other"))

        with(trueLine) {
            with(validHeader) {
                TestField.getBoolean() shouldBe Either.Right(true)
            }
        }

        with(falseLine) {
            with(validHeader) {
                TestField.getBoolean() shouldBe Either.Right(false)
            }
        }
    }

    test("getBooleanNullable should handle null and values") {
        val line1 = NotParsedCsvLine(listOf("", "other"))
        val line2 = NotParsedCsvLine(listOf("true", "other"))

        with(line1) {
            with(validHeader) {
                TestField.getBooleanNullable() shouldBe Either.Right(null)
            }
        }

        with(line2) {
            with(validHeader) {
                TestField.getBooleanNullable() shouldBe Either.Right(true)
            }
        }
    }

    test("getLocalDate should return valid date") {
        val line = NotParsedCsvLine(listOf("2023-12-31", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getLocalDate()
                result shouldBe Either.Right(LocalDate.of(2023, 12, 31))
            }
        }
    }

    test("getLocalDateNullable should handle null and values") {
        val line1 = NotParsedCsvLine(listOf("", "other"))
        val line2 = NotParsedCsvLine(listOf("2023-12-31", "other"))

        with(line1) {
            with(validHeader) {
                TestField.getLocalDateNullable() shouldBe Either.Right(null)
            }
        }

        with(line2) {
            with(validHeader) {
                TestField.getLocalDateNullable() shouldBe Either.Right(LocalDate.of(2023, 12, 31))
            }
        }
    }

    test("getLocalDateTime should return valid datetime") {
        val line = NotParsedCsvLine(listOf("2023-12-31T23:59:59", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getLocalDateTime()
                result shouldBe Either.Right(LocalDateTime.of(2023, 12, 31, 23, 59, 59))
            }
        }
    }

    test("getLocalDateTimeNullable should handle null and values") {
        val line1 = NotParsedCsvLine(listOf("", "other"))
        val line2 = NotParsedCsvLine(listOf("2023-12-31T23:59:59", "other"))

        with(line1) {
            with(validHeader) {
                TestField.getLocalDateTimeNullable() shouldBe Either.Right(null)
            }
        }

        with(line2) {
            with(validHeader) {
                TestField.getLocalDateTimeNullable() shouldBe Either.Right(
                    LocalDateTime.of(2023, 12, 31, 23, 59, 59)
                )
            }
        }
    }

    test("getInstant should return valid instant") {
        val line = NotParsedCsvLine(listOf("2023-12-31T23:59:59Z", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getInstant()
                result shouldBe Either.Right(Instant.parse("2023-12-31T23:59:59Z"))
            }
        }
    }

    test("getInstantNullable should handle null and values") {
        val line1 = NotParsedCsvLine(listOf("", "other"))
        val line2 = NotParsedCsvLine(listOf("2023-12-31T23:59:59Z", "other"))

        with(line1) {
            with(validHeader) {
                TestField.getInstantNullable() shouldBe Either.Right(null)
            }
        }

        with(line2) {
            with(validHeader) {
                TestField.getInstantNullable() shouldBe Either.Right(
                    Instant.parse("2023-12-31T23:59:59Z")
                )
            }
        }
    }

    test("getStringNullable with block should execute block") {
        val line = NotParsedCsvLine(listOf("test", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getStringNullable { "customValue" }
                result shouldBe "customValue"
            }
        }
    }

    test("getEnum should return valid enum") {
        val line = NotParsedCsvLine(listOf("VALUE1", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getEnum { TestEnum.valueOf(it) }
                result shouldBe Either.Right(TestEnum.VALUE1)
            }
        }
    }

    test("getEnum should return error for invalid enum value") {
        val line = NotParsedCsvLine(listOf("INVALID_VALUE", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getEnum<TestEnum, TestCsvLine> { TestEnum.valueOf(it) }
                result.shouldBeInstanceOf<Either.Left<CsvFieldError>>()
            }
        }
    }

    test("validate should pass for valid condition") {
        val line = NotParsedCsvLine(listOf("42", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getInt()
                    .validate({ it > 0 }, { CsvFieldError(TestField, UnsupportedBooleanValueError("asd")) })

                result shouldBe Either.Right(42)
            }
        }
    }

    test("validate should fail for invalid condition") {
        val line = NotParsedCsvLine(listOf("-5", "other"))

        with(line) {
            with(validHeader) {
                val error = CsvFieldError(TestField, UnsupportedBooleanValueError("asd"))
                val result = TestField.getInt()
                    .validate({ it > 0 }, { error })

                result shouldBe Either.Left(error)
            }
        }
    }

    test("validate should preserve original error") {
        val line = NotParsedCsvLine(listOf("not_a_number", "other"))

        with(line) {
            with(validHeader) {
                val result = TestField.getInt()
                    .validate({ it > 0 }, { CsvFieldError(TestField, UnsupportedBooleanValueError("asd")) })

                result.shouldBeInstanceOf<Either.Left<CsvFieldError>>()
            }
        }
    }

    test("convert should handle different types") {
        val line = NotParsedCsvLine(listOf("test", "other"))

        with(line) {
            with(validHeader) {
                // Проверяем, что общая функция convert работает
                val stringResult: Either<ICsvError, String> = TestField.convert()
                stringResult shouldBe Either.Right("test")
            }
        }
    }

    test("CsvFieldError should contain field and error") {
        val field = TestField
        val castError = UnsupportedBooleanValueError("asd")
        val error = CsvFieldError(field, castError)

        error.fieldName shouldBe field
        error.error shouldBe castError
    }

    test("EnumCastError should contain all properties") {
        val error = EnumCastError(
            errorClass = IllegalArgumentException::class,
            errorMsg = "Test error",
            klass = TestEnum::class
        )

        error.errorClass shouldBe IllegalArgumentException::class
        error.errorMsg shouldBe "Test error"
        error.klass shouldBe TestEnum::class
    }
})