package ru.vood.kotlin.csv.parser

enum class ClientFieldsEnum(override val fieldName: String) : IFieldConstants {

    NAME("name"),
    AGE("age")
}