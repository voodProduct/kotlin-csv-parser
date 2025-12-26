package ru.vood.kotlin.csv.parser.either

import ru.vood.kotlin.csv.parser.ICSVLine

data class ClientEntityCsv(
    val name: String,
    val age1: Int,
    val age2: Int,
    val age3: Int,
    val eyeColourEnum: EyeColourEnum
) : ICSVLine {
    init {
        require(age1 < 1) { "age1 must be less than 1" }
    }
}

enum class EyeColourEnum{
    RED, GREEN, BLUE
}