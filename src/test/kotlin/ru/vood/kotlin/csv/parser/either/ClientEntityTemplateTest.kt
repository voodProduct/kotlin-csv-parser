package ru.vood.kotlin.csv.parser.either

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import ru.vood.kotlin.csv.parser.CsvEntityTemplate
import ru.vood.kotlin.csv.parser.dto.ParsedHeader
import ru.vood.kotlin.csv.parser.error.ICsvError

class ClientEntityTemplateTestEither() : CsvEntityTemplate<ClientEntityCsv>() {

    override fun either(
        headerWithIndex: ParsedHeader,
        strValues: List<String>
    ): Either<NonEmptyList<ICsvError>, ClientEntityCsv> = either {
        zipOrAccumulate(
            { ClientFieldsEnum.NAME.getString(headerWithIndex.headerWithIndex, strValues).bind() },
            { ClientFieldsEnum.AGE1.getInt(headerWithIndex.headerWithIndex, strValues).bind() },
            { ClientFieldsEnum.AGE2.getInt(headerWithIndex.headerWithIndex, strValues).bind() },
            { ClientFieldsEnum.AGE3.getInt(headerWithIndex.headerWithIndex, strValues).bind() }
        ) { q1, q2, q3, q4 -> ClientEntityCsv(q1, q2, q3, q4) }
    }


}