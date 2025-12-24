package ru.vood.kotlin.csv.parser

import arrow.core.Either

/**
 * Базовый класс для шаблоннов маппинга при получении сущности из строки csv файла
 */
abstract class CsvEntityTemplate<T : ICSVLine>(
    val headerWithIndex: Map<String, Int>
) {
    abstract fun toEntity(strValues: List<String>): Either<Throwable, T>

    abstract val header: String
    abstract val delimiter: String
//    val mapHeaderWithIndex: Map<String, Int> by lazy {
//        val headerIndexesCsv = header.split(delimiter)
//        headerIndexesCsv.withIndex().associate { it.value.lowercase() to it.index }
//    }
//    val logger: Logger = LoggerFactory.getLogger(this::class.java)


    fun prepareConvert(
        mapHeaderWithIndex: Map<String, Int>,
        strValues: List<String>,
        vararg body: (mapHeaderWithIndex: Map<String, Int>, strValues: List<String>) -> Any?
    ): List<String> {
        return buildList {
            body.forEach {
                runCatching {
                    it.invoke(mapHeaderWithIndex, strValues)
                }.getOrElse { err ->
                    err.message?.let { msg ->
                        add("Заголовок c колонками и их порядковыми номерами: ${mapHeaderWithIndex.map { "${it.key} -> ${it.value}" }.joinToString (delimiter)}")
                        add("Содержимое строки: ${strValues.joinToString(delimiter)}")
                        add(msg)
                    }
                }
            }
        }
    }

    private companion object
}