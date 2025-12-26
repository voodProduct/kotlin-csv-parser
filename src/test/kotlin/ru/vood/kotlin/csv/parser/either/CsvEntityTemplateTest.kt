package ru.vood.kotlin.csv.parser.either

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.*
import ru.vood.kotlin.csv.parser.ClientEntityTemplateTest
import ru.vood.kotlin.csv.parser.ReaderCsvImpl

val readerCsvImpl = ReaderCsvImpl()

class CsvEntityTemplateTest : FunSpec({


    test("header") {

//        val count = 500_000
        val count = 3
//        println(infiniteFlowClient().take(count).toList())

        val toList = readerCsvImpl
            .readCSVEither(
                stringFlow = infiniteFlowClient().take(count),
                delimiter = ";",
                entity = ClientEntityTemplateTestEither()
            )
            .map {
                println(it)
                it
            }
            .toList()

//        println(toList)

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
        val headerTest = "name;age1;age2;age3"
        fun infiniteFlowClient(): Flow<String> {

            return flow {
                var counter = 0
                emit(headerTest)
                while (true) {
//                    emit("name_${counter};${counter}q;${counter+1}q;${counter+2}q")
                    emit("name_${counter};${counter};${counter+1};${counter+2}")
                    counter++
                }
            }
        }
    }
}
