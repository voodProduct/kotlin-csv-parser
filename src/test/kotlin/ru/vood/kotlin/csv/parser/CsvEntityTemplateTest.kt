package ru.vood.kotlin.csv.parser

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.*

val readerCsvImpl = ReaderCsvImpl()

class CsvEntityTemplateTest : FunSpec({


    test("header") {

        val count = 3
        println(infiniteFlowClient().take(count).toList())

        val toList = readerCsvImpl
            .readCSV(
                stringFlow = infiniteFlowClient().take(count),
                delimiter = ";",
                entity = ClientEntityTemplateTest()
            )
            .map {
                println(it)
                it
            }
            .toList()

        println(toList)

        toList.size shouldBe count - 1

        /*
                infiniteFlowClient()
                    .take(10)
                    .map {q-> clientEntityTemplateTest.toEntity()
                    }
        */


    }
}) {

    companion object {
        val headerTest = "name;age"
        fun infiniteFlowClient(): Flow<String> {

            return flow {
                var counter = 0
                emit(headerTest)
                while (true) {
                    emit("name_${counter};${counter}")
                    counter++
                }
            }
        }
    }
}
