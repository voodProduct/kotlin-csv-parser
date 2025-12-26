package ru.vood.kotlin.csv.parser.either

import ru.vood.kotlin.csv.parser.IFieldConstants

enum class ClientFieldsEnum(override val fieldName: String) : IFieldConstants {

    NAME("name"),
    AGE1("age1"),
    AGE2("age2"),
    AGE3("age3"),
    EyeColour("eyeColourEnum"),

}