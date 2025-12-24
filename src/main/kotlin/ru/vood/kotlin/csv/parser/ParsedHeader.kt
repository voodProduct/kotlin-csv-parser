package ru.vood.kotlin.csv.parser

@JvmInline
value class ParsedHeader(
    val headerWithIndex: Map<String, Int>,
)