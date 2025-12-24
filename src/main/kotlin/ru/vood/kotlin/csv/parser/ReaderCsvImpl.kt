package ru.vood.kotlin.csv.parser

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import ru.vood.kotlin.csv.parser.HeaderUtil.parseHeader
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

class ReaderCsvImpl(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(10)
) : IReaderCsv {

    @OptIn(ExperimentalAtomicApi::class)
    override fun <T : ICSVLine> readCSV(
        stringFlow: Flow<String>,
        delimiter: String,
        entity: CsvEntityTemplate<T>,
    ): Flow<T> {
        val parsedHeader = AtomicReference<ParsedHeader?>(null)
        val processDataFlow = stringFlow
            .flowOn(dispatcher)
            .filterNot { it.isBlank() || it.replace(delimiter, "").isBlank() }
            .transform { string ->
                if (parsedHeader.load() != null) {
                    val list = string.split(delimiter)
                    entity.toEntity(list, parsedHeader.load() ?: error("Эта ошибка не должна возникнуть"))
                        .onLeft { err ->
                            println(err.message)
//                        entity.logger.error(err.message)
                        }
                        .onRight {
                            this.emit(it)
                        }
                } else {
                    parsedHeader.exchange(parseHeader(header = string, delimiter = delimiter))

                }


//                val list = string.split(delimiter)
//                entity.toEntity(list, mapHeaderWithIndex)
//                    .onLeft { err ->
//                        println(err.message)
////                        entity.logger.error(err.message)
//                    }
//                    .onRight { emit(it) }
            }

        return processDataFlow
    }
}
