package ru.vood.kotlin.csv.parser.dto

@JvmInline
value class ParsedHeader(
    val headerWithIndex: Map<String, Int>,
)