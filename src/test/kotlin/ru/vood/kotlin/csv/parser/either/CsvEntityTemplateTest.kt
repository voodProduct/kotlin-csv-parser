package ru.vood.kotlin.csv.parser.either

import arrow.core.Either
import arrow.core.left
import arrow.core.nonEmptyListOf
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.*
import ru.vood.kotlin.csv.parser.ReaderCsvImpl
import ru.vood.kotlin.csv.parser.dto.NotParsedCsvLine
import ru.vood.kotlin.csv.parser.dto.ParsedHeader
import ru.vood.kotlin.csv.parser.error.ILineError
import ru.vood.kotlin.csv.parser.error.LineParseError
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset

val readerCsvImpl = ReaderCsvImpl()

class CsvEntityTemplateTest : FunSpec({


    test("Full parsing flow") {

//        val count = 500_000
        val count = 3
        val toList = readerCsvImpl
            .readCSV(
                stringFlow = infiniteFlowClient().take(count),
                delimiter = ";",
                entity = ClientEntityTemplateTestEither()
            )
            .map {
                //                println(it)
                it
            }
            .toList()
        toList.size shouldBe count - 1

        assert(toList)

    }

    test("Full parsing flow with index") {

//        val count = 500_000
        val count = 3
        val toList = readerCsvImpl
            .readCSVWithIndex(
                stringFlow = infiniteFlowClient().take(count),
                delimiter = ";",
                entity = ClientEntityTemplateTestEither()
            )
            .map {
                //                println(it)
                it
            }
            .toList()
        toList.size shouldBe count - 1

        toList[0].index shouldBe 0
        toList[1].index shouldBe 1

        assert(toList.map { it.value })
    }

    test("Full parsing stream") {

//        val count = 500_000
        val count = 3
        val stringFlow: List<String> = infiniteFlowClient().take(count).toList()

        val toList = readerCsvImpl
            .readCSVWithIndex(
                inputStream = stringFlow.toInputStream(),
                delimiter = ";",
                entity = ClientEntityTemplateTestEither()
            )
            .map {
                //                println(it)
                it
            }
            .toList()

        toList
            .forEach { println(it) }

        toList.size shouldBe count - 1

        assert(toList.map { it.value })

    }

}) {

    companion object {
        val headerTest = "name;age1;age2;age3;eyeColourEnum"
        fun infiniteFlowClient(): Flow<String> {

            return flow {
                var counter = -2
                emit(headerTest)
                while (true) {
                    emit("name_${counter};${counter};${counter + 1};${counter + 2};GREEN")
                    counter++
                }
            }
        }

        fun List<String>.toInputStream(
            charset: Charset = Charsets.UTF_8,
            lineSeparator: String = System.lineSeparator()
        ): InputStream {
            val content = this.joinToString(lineSeparator)
            return ByteArrayInputStream(content.toByteArray(charset))
        }
    }
}

private fun assert(toList: List<Either<ILineError, ClientEntityCsv>>) {
    toList[0] shouldBe LineParseError(
        lineIndex = 2,
        errors = nonEmptyListOf(
            BadFieldValue(field = ClientFieldsEnum.AGE1, error = "must be > 0"),
            BadFieldValue(field = ClientFieldsEnum.AGE2, error = "must be > 0")
        ),
        strValues = NotParsedCsvLine(strValues = listOf("name_-2", "-2", "-1", "0", "GREEN")),
        headerWithIndex = ParsedHeader(
            headerWithIndex = mapOf(
                "name" to 0, "age1" to 1, "age2" to 2, "age3" to 3, "eyecolourenum" to 4
            )
        )
    ).left()

    toList[1] shouldBe LineParseError(
        lineIndex = 3,
        errors = nonEmptyListOf(
            BadFieldValue(field = ClientFieldsEnum.AGE1, error = "must be > 0"),
            BadFieldValue(field = ClientFieldsEnum.AGE2, error = "must be > 0")
        ),
        strValues = NotParsedCsvLine(strValues = listOf("name_-1", "-1", "0", "1", "GREEN")),
        headerWithIndex = ParsedHeader(
            headerWithIndex = mapOf(
                "name" to 0, "age1" to 1, "age2" to 2, "age3" to 3, "eyecolourenum" to 4
            )
        )
    ).left()
}
