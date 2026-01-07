package ru.vood.kotlin.csv.parser

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.vood.kotlin.csv.parser.error.ILineError
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

interface IReaderCsv {


    fun <T : ICSVLine> readCSVEither(
        stringFlow: Flow<String>,
        delimiter: String,
        entity: CsvEntityTemplate<T>,
    ): Flow<Either<ILineError, T>>

    fun <T : ICSVLine> readCSVEither(
        inputStream: InputStream,
        delimiter: String,
        entity: CsvEntityTemplate<T>,
        charset: String = "UTF-8",
        bufferSize: Int = 8192,
        skipEmptyLines: Boolean = false

    ): Flow<Either<ILineError, T>> =
        readCSVEither(inputStream.toLineFlow(charset, bufferSize, skipEmptyLines), delimiter, entity)

    private fun InputStream.toLineFlow(
        charset: String = "UTF-8",
        bufferSize: Int = 8192,
        skipEmptyLines: Boolean = false
    ): Flow<String> = flow {
        val reader = BufferedReader(InputStreamReader(this@toLineFlow, charset), bufferSize)

        reader.use { bufferedReader ->
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                line?.let { currentLine ->
                    // Пропускаем пустые строки если нужно
                    if (!skipEmptyLines || currentLine.isNotBlank()) {
                        emit(currentLine)
                    }
                }
            }
        }
    }
}
