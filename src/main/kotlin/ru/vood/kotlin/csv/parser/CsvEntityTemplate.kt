package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.left
import ru.vood.kotlin.csv.parser.dto.NotParsedCsvLine
import ru.vood.kotlin.csv.parser.dto.ParsedHeader
import ru.vood.kotlin.csv.parser.error.ICsvError
import ru.vood.kotlin.csv.parser.error.ILineError
import ru.vood.kotlin.csv.parser.error.LineDtoCreateError
import ru.vood.kotlin.csv.parser.error.LineParseError

/**
 * Базовый класс для шаблоннов маппинга при получении сущности из строки csv файла
 */
abstract class CsvEntityTemplate<T : ICSVLine>() {

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    protected abstract fun parseLine(
        headerWithIndex: ParsedHeader,
        strValues: NotParsedCsvLine
    ): Either<NonEmptyList<ICsvError>, T>

    fun toEntity(
        strValues: NotParsedCsvLine,
        lineIndex: Int,
        headerWithIndex: ParsedHeader
    ): Either<ILineError, T> = Either
        .catch {
            with(strValues) {
                with(headerWithIndex) {
                    parseLine(headerWithIndex, strValues)
                }
            }
        }.fold(
            {
                val left: Either<LineDtoCreateError, T> = LineDtoCreateError(
                    lineIndex = lineIndex,
                    strValues = strValues,
                    headerWithIndex = headerWithIndex,
                    errorClass = it::class,
                    errorMsg = it.message
                ).left()
                left
            },
            {
                val mapLeft = it.mapLeft {
                    LineParseError(
                        lineIndex = lineIndex,
                        errors = it,
                        strValues = strValues,
                        headerWithIndex = headerWithIndex
                    )
                }
                mapLeft
            }
        )


    private companion object
}