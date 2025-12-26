package ru.vood.kotlin.csv.parser.dto

@JvmInline
value class NotParsedCsvLine(
    val strValues: List<String>
)