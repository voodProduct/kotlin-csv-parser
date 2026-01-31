package ru.vood.kotlin.csv.parser

import arrow.core.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import ru.vood.kotlin.csv.parser.HeaderUtil.parseHeader
import ru.vood.kotlin.csv.parser.dto.NotParsedCsvLine
import ru.vood.kotlin.csv.parser.dto.ParsedHeader
import ru.vood.kotlin.csv.parser.error.ILineError

class ReaderCsvImpl(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(10)
) : IReaderCsv {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun <T : ICSVLine> readCSV(
        stringFlow: Flow<String>,
        delimiter: String,
        entity: CsvEntityTemplate<T>,
    ): Flow<Either<ILineError, T>> = stringFlow
        .withIndex()
        .filterNot { it.value.isBlank() || it.value.replace(delimiter, "").isBlank() }
        .scan(null as (ParsedHeader?) to emptyList<Either<ILineError, T>>()) { acc, currentLine ->
            val (header, _) = acc
            val line = currentLine.value
            val lineIndex = currentLine.index + 1

            if (header == null) {
                val parsedHeader = parseHeader(header = line, delimiter = delimiter)
                parsedHeader to emptyList()
            } else {
                val values = NotParsedCsvLine(line.split(delimiter))
                val result = entity.toEntity(strValues = values, lineIndex = lineIndex, headerWithIndex = header)
                header to listOf(result)
            }
        }
        .drop(COUNT_LINES_WITHOUT_DATA) //пропуск начального состояния, где header == null и нет данных
        .flatMapConcat { (_, result) -> result.asFlow() }
        .flowOn(dispatcher)

    private companion object {
        const val COUNT_LINES_WITHOUT_DATA = 1
    }
}
