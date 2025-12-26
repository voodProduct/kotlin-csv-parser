package ru.vood.kotlin.csv.parser

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import ru.vood.kotlin.csv.parser.error.ILineError

interface IReaderCsv {


    fun <T : ICSVLine> readCSVEither(
        stringFlow: Flow<String>,
        delimiter: String,
        entity: CsvEntityTemplate<T>,
    ): Flow<Either<ILineError, T>>
}
