package ru.vood.kotlin.csv.parser.either

import ru.vood.kotlin.csv.parser.ICSVLine

data class ClientEntityCsv2(
    val name: String,
    val age1: Int,
    val age2: Int,
    val age3: Int,
) : ICSVLine {
    init {
        require(age1 < 1) { "age1 must be less than 1" }
    }
}