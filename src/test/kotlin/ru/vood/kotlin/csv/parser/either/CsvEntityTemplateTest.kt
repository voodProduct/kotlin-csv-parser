package ru.vood.kotlin.csv.parser.either

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.*
import ru.vood.kotlin.csv.parser.ReaderCsvImpl

val readerCsvImpl = ReaderCsvImpl()

class CsvEntityTemplateTest : FunSpec({


    test("header") {

//        val count = 500_000
        val count = 3
        val toList = readerCsvImpl
            .readCSVEither(
                stringFlow = infiniteFlowClient().take(count),
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

//        println(toList)

        toList.size shouldBe count - 1

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
    }
}
