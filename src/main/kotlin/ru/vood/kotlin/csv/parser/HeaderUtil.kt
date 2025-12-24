package ru.vood.kotlin.csv.parser

object HeaderUtil {

    fun parseHeader(header: String, delimiter: String): ParsedHeader =
        ParsedHeader(
            headerWithIndex = header
                .split(delimiter)
                .withIndex()
                .associate { it.value.lowercase() to it.index })


}