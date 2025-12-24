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
                entity = ClientEntityTemplateTest(
                    header = value,
                    delimiter = ";"
                )

            )
            .map {
                println(it)
                it
            }
            .toList()

        println(toList)

        toList.size shouldBe count

        /*
                infiniteFlowClient()
                    .take(10)
                    .map {q-> clientEntityTemplateTest.toEntity()
                    }
        */


    }
}) {

    companion object {
        val value = "name;age"
        fun infiniteFlowClient(): Flow<String> {

            return flow {
                var counter = 0
//            emit(value)
                while (true) {
                    emit("name_${counter};${counter}")
                    counter++
                }
            }
        }
    }
}
