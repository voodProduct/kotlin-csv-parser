package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.Either.Left
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import ru.vood.kotlin.csv.parser.HeaderUtil.parseHeader
import ru.vood.kotlin.csv.parser.error.ILineError
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

class ReaderCsvImpl(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO.limitedParallelism(10)
) : IReaderCsv {

    @OptIn(ExperimentalAtomicApi::class)
    override fun <T : ICSVLine> readCSVEither(
        stringFlow: Flow<String>,
        delimiter: String,
        entity: CsvEntityTemplate<T>,
    ): Flow<Either<ILineError, T>> {
        val parsedHeader = AtomicReference<ParsedHeader?>(null)
        val processDataFlow = stringFlow
            .withIndex()
            .flowOn(dispatcher)
            .filterNot { it.value.isBlank() || it.value.replace(delimiter, "").isBlank() }
            .transform { string ->
                if (parsedHeader.load() != null) {
                    val list = string.value.split(delimiter)
                    val toEntityEither: Either<ILineError, T> = entity.toEntityEither(
                        strValues = list,
                        string.index + 1,
                        headerWithIndex = parsedHeader.load() ?: error("Эта ошибка не должна возникнуть")
                    )
                    if (toEntityEither is Left) {
                        println(toEntityEither.value)
                    }
                    this.emit(toEntityEither)


//                    entity.toEntity(
//                        strValues = list,
//                        headerWithIndex = parsedHeader.load() ?: error("Эта ошибка не должна возникнуть")
//                    )
//
//                        .onLeft { err ->
//                            println(err.message)
////                        entity.logger.error(err.message)
//                        }
//                        .onRight {
//                            this.emit(it)
//                        }
                } else {
                    parsedHeader.exchange(parseHeader(header = string.value, delimiter = delimiter))

                }
            }

        return processDataFlow
    }


}
