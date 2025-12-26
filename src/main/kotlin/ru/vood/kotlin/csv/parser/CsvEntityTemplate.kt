package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.left
import ru.vood.kotlin.csv.parser.dto.ParsedHeader
import ru.vood.kotlin.csv.parser.error.ICsvError
import ru.vood.kotlin.csv.parser.error.ILineError
import ru.vood.kotlin.csv.parser.error.LineDtoCreateError
import ru.vood.kotlin.csv.parser.error.LineParseError

/**
 * Базовый класс для шаблоннов маппинга при получении сущности из строки csv файла
 */
abstract class CsvEntityTemplate<T : ICSVLine>() {

    protected abstract fun either(
        headerWithIndex: ParsedHeader,
        strValues: List<String>
    ): Either<NonEmptyList<ICsvError>, T>

    fun toEntity(
        strValues: List<String>,
        lineIndex: Int,
        headerWithIndex: ParsedHeader
    ): Either<ILineError, T> = Either
        .catch {
            either(headerWithIndex, strValues)
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