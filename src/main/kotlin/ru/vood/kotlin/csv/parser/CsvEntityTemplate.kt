package ru.vood.kotlin.csv.parser

import arrow.core.Either

/**
 * Базовый класс для шаблоннов маппинга при получении сущности из строки csv файла
 */
abstract class CsvEntityTemplate<T : ICSVLine>(
) {
    abstract fun toEntity(strValues: List<String>, headerWithIndex: ParsedHeader): Either<Throwable, T>

    fun prepareConvert(
        mapHeaderWithIndex: ParsedHeader,
        strValues: List<String>,
        vararg body: (mapHeaderWithIndex: Map<String, Int>, strValues: List<String>) -> Any?
    ): List<String> {
        return buildList {
            body.forEach {
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
    }

    private companion object
}