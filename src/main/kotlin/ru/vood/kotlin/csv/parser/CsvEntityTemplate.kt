package ru.vood.kotlin.csv.parser

import arrow.core.Either
import ru.vood.kotlin.csv.parser.error.ILineError

/**
 * Базовый класс для шаблоннов маппинга при получении сущности из строки csv файла
 */
abstract class CsvEntityTemplate<T : ICSVLine>(
) {
    abstract fun toEntity(strValues: List<String>, headerWithIndex: ParsedHeader): Either<Throwable, T>

    abstract fun toEntityEither(
        strValues: List<String>,
        lineIndex: Int,
        headerWithIndex: ParsedHeader
    ): Either<ILineError, T>
//            Either<NonEmptyList<ICsvError>, T>

    fun prepareConvert(
        mapHeaderWithIndex: ParsedHeader,
        strValues: List<String>,
        vararg body: (mapHeaderWithIndex: Map<String, Int>, strValues: List<String>) -> Any?
    ): List<String> {
        val buildList = buildList {
            body.forEach { it ->
                runCatching {
                    it.invoke(mapHeaderWithIndex.headerWithIndex, strValues)
                }.getOrElse { err ->
                    err.message?.let { msg ->
                        add(
                            "Заголовок c колонками и их порядковыми номерами: ${
                                mapHeaderWithIndex.headerWithIndex.map { "${it.key} -> ${it.value}" }
                            }"
                        )
                        add("Содержимое строки: $strValues")
                        add(msg)
                    }
                }
            }
        }
        return buildList
    }

    private companion object
}