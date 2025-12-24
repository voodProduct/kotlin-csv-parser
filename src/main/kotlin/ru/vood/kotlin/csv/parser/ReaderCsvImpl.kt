package ru.vood.kotlin.csv.parser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform

class ReaderCsvImpl : IReaderCsv {
    private val dispatcher = Dispatchers.IO.limitedParallelism(10)

    override fun <T : ICSVLine> readCSV(
        stringFlow: Flow<String>,
        delimiter: String,
        entity: CsvEntityTemplate<T>,
    ): Flow<T> {
        val processDataFlow: Flow<T> = stringFlow
            .flowOn(dispatcher)
            .filterNot { it.isBlank() || it.replace(delimiter, "").isBlank() }
            .transform { string ->
                val list = string.split(delimiter)
                entity.toEntity(list)
                    .onLeft { err ->
                        println(err.message)
//                        entity.logger.error(err.message)
                    }
                    .onRight { emit(it) }
            }

        return processDataFlow
    }
}
