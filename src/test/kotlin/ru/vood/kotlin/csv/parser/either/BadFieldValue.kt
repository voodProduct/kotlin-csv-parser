package ru.vood.kotlin.csv.parser.either

import ru.vood.kotlin.csv.parser.IFieldConstants
import ru.vood.kotlin.csv.parser.error.ICsvError

data class BadFieldValue(
    val field: IFieldConstants<ClientEntityCsv>,
    val error: String
) : ICsvError