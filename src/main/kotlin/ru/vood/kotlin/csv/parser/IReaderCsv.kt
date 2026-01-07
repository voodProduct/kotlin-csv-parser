package ru.vood.kotlin.csv.parser

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.withIndex
import ru.vood.kotlin.csv.parser.error.ILineError
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

interface IReaderCsv {


    fun <T : ICSVLine> readCSV(
        stringFlow: Flow<String>,
        delimiter: String,
        entity: CsvEntityTemplate<T>,
    ): Flow<Either<ILineError, T>>

    fun <T : ICSVLine> readCSVWithIndex(
        stringFlow: Flow<String>,
        delimiter: String,
        entity: CsvEntityTemplate<T>,
    ): Flow<IndexedValue<Either<ILineError, T>>> = readCSV(stringFlow, delimiter, entity).withIndex()

    fun <T : ICSVLine> readCSV(
        inputStream: InputStream,
        delimiter: String,
        entity: CsvEntityTemplate<T>,
        charset: String = "UTF-8",
        bufferSize: Int = 8192,
        skipEmptyLines: Boolean = false

    ): Flow<Either<ILineError, T>> =
        readCSV(inputStream.toLineFlow(charset, bufferSize, skipEmptyLines), delimiter, entity)


    fun <T : ICSVLine> readCSVWithIndex(
        inputStream: InputStream,
        delimiter: String,
        entity: CsvEntityTemplate<T>,
        charset: String = "UTF-8",
        bufferSize: Int = 8192,
        skipEmptyLines: Boolean = false
    ): Flow<IndexedValue<Either<ILineError, T>>> = readCSV(inputStream, delimiter, entity, charset, bufferSize, skipEmptyLines).withIndex()


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
