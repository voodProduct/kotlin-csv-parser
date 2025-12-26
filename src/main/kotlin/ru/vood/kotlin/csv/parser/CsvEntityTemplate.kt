package ru.vood.kotlin.csv.parser

import arrow.core.Either
import ru.vood.kotlin.csv.parser.error.ILineError

/**
 * Базовый класс для шаблоннов маппинга при получении сущности из строки csv файла
 */
abstract class CsvEntityTemplate<T : ICSVLine>() {

    abstract fun toEntityEither(
        strValues: List<String>,
        lineIndex: Int,
        headerWithIndex: ParsedHeader
    ): Either<ILineError, T>

    private companion object
}