package ru.vood.kotlin.csv.parser

import arrow.core.left
import arrow.core.right
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ru.vood.kotlin.csv.parser.ReaderCsvConverter.convertEither
import ru.vood.kotlin.csv.parser.error.CastError
import ru.vood.kotlin.csv.parser.error.UnsupportedBooleanValueError
import ru.vood.kotlin.csv.parser.error.UnsupportedClassError
import java.time.LocalDate
import java.time.LocalDateTime

class ReaderCsvConverterTest : FunSpec({

    test("Boolean - OK 0") {
        val convertEither = convertEither<Boolean>("0")
        convertEither shouldBe false.right()
    }

    test("Int - OK") {
        val convertEither = convertEither<Int>("0")
        convertEither shouldBe 0.right()
    }

    test("Short - OK") {
        val convertEither = convertEither<Short>("0")
        convertEither shouldBe 0.toShort().right()
    }

    test("Long - OK") {
        val convertEither = convertEither<Long>("0")
        convertEither shouldBe 0.toLong().right()
    }

    test("Float - OK") {
        val convertEither = convertEither<Float>("0")
        convertEither shouldBe 0.toFloat().right()
    }
    test("Double - OK") {
        val convertEither = convertEither<Double>("0")
        convertEither shouldBe 0.toDouble().right()
    }
    test("String - OK") {
        val convertEither = convertEither<String>("0")
        convertEither shouldBe "0".right()
    }
    test("LocalDateTime - OK") {
        val convertEither = convertEither<LocalDateTime>("2024-12-25T15:30:45.123456789Z")

        convertEither shouldBe LocalDateTime.of(2024, 12, 25, 15, 30, 45, 123456789).right()
    }
    xtest("LocalDate - OK") {
        val convertEither = convertEither<LocalDate>("2024-12-25")
        convertEither shouldBe LocalDate.of(2024, 12, 25).right()
    }

    test("CustomType - OK") {
        val convertEither = convertEither<SomeClass>("qwerty") { SomeClass(it) }
        convertEither shouldBe SomeClass(value = "qwerty").right()
    }

    test("Boolean - error") {
        val convertEither = convertEither<Boolean>("qwerty")
        convertEither shouldBe UnsupportedBooleanValueError(errorMsg = "Ожидалось значение 1|0|true|false").left()
    }

    test("Int - error") {
        val convertEither = convertEither<Int>("qwerty")
        convertEither shouldBe CastError(
            errorClass = NumberFormatException::class,
            errorMsg = """For input string: "qwerty""""
        ).left()
    }

    test("Short - error") {
        val convertEither = convertEither<Short>("qwerty")
        convertEither shouldBe CastError(
            errorClass = NumberFormatException::class,
            errorMsg = """For input string: "qwerty""""
        ).left()
    }

    test("Long - error") {
        val convertEither = convertEither<Long>("qwerty")
        convertEither shouldBe CastError(
            errorClass = NumberFormatException::class,
            errorMsg = """For input string: "qwerty""""
        ).left()
    }

    test("Float - error") {
        val convertEither = convertEither<Float>("qwerty")
        convertEither shouldBe CastError(
            errorClass = NumberFormatException::class,
            errorMsg = """For input string: "qwerty""""
        ).left()
    }
    test("Double - error") {
        val convertEither = convertEither<Double>("qwerty")
        convertEither shouldBe CastError(
            errorClass = NumberFormatException::class,
            errorMsg = """For input string: "qwerty""""
        ).left()
    }

    test("LocalDateTime - error") {
        val convertEither = convertEither<LocalDateTime>("qwerty")

        convertEither shouldBe CastError(
            errorClass = IllegalStateException::class,
            errorMsg = """Unknown Date Format"""
        ).left()
    }
    test("LocalDate - error") {
        val convertEither = convertEither<LocalDate>("qwerty-12-25")
        convertEither shouldBe CastError(
            errorClass = IllegalStateException::class,
            errorMsg = """Unknown Date Format"""
        ).left()
    }


    test("CustomType - error") {
        val convertEither = convertEither<SomeClass>("qwerty-12-25")
        convertEither shouldBe UnsupportedClassError(
            errorMsg = """Unknown Generic Type ru.vood.kotlin.csv.parser.SomeClass"""
        ).left()
    }

})

data class SomeClass(
    val value: String
)