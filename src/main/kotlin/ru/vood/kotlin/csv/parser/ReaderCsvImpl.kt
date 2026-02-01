package ru.vood.kotlin.csv.parser

import arrow.core.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.vood.kotlin.csv.parser.HeaderUtil.parseHeader
import ru.vood.kotlin.csv.parser.dto.NotParsedCsvLine
import ru.vood.kotlin.csv.parser.dto.ParsedHeader
import ru.vood.kotlin.csv.parser.error.ILineError

class ReaderCsvImpl(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(10)
) : IReaderCsv {

    override fun <T : ICSVLine> readCSV(
        stringFlow: Flow<String>,
        delimiter: String,
        entity: CsvEntityTemplate<T>,
    ): Flow<Either<ILineError, T>> = flow {
        var parsedHeader: ParsedHeader? = null
        var lineIndex = 0L

        stringFlow.collect { line ->
            lineIndex++

            if (line.isEmpty() || line.all { it == delimiter[0] || it.isWhitespace() }) {
                return@collect
            }

            if(parsedHeader == null) {
                parsedHeader = parseHeader(header = line, delimiter = delimiter)
            }else {
                val values = NotParsedCsvLine(line.split(delimiter))
                emit(
                    entity.toEntity(
                        strValues = values,
                        lineIndex = lineIndex,
                        headerWithIndex = parsedHeader
                    )
                )
            }
        }
    }.flowOn(dispatcher)
}
