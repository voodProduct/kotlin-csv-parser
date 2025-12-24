package ru.vood.kotlin.csv.parser

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.flow.*

val readerCsvImpl = ReaderCsvImpl()

class CsvEntityTemplateTest : FunSpec({


    test("header") {

        println(infiniteFlowClient().take(10).toList())


        val toList = readerCsvImpl
            .readCSV(
                stringFlow = infiniteFlowClient().take(10),
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
