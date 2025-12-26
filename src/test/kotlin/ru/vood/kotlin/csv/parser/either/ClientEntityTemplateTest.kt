package ru.vood.kotlin.csv.parser.either

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import ru.vood.kotlin.csv.parser.CsvEntityTemplate
import ru.vood.kotlin.csv.parser.ParsedHeader
import ru.vood.kotlin.csv.parser.error.LineError

class ClientEntityTemplateTestEither() : CsvEntityTemplate<ClientEntityCsv2>() {

    override fun toEntity(
        strValues: List<String>,
        headerWithIndex: ParsedHeader
    ): Either<Throwable, ClientEntityCsv2> {
        TODO()
    }

    override fun toEntityEither(
        strValues: List<String>,
        lineIndex: Int,
        headerWithIndex: ParsedHeader
    ): Either<LineError, ClientEntityCsv2> {
        val d = either {
            zipOrAccumulate(
                { ClientFieldsEnum2.NAME.getString(headerWithIndex.headerWithIndex, strValues) },
                { ClientFieldsEnum2.AGE1.getIntEither(headerWithIndex.headerWithIndex, strValues).bind() },
                { ClientFieldsEnum2.AGE2.getIntEither(headerWithIndex.headerWithIndex, strValues).bind() },
                { ClientFieldsEnum2.AGE3.getIntEither(headerWithIndex.headerWithIndex, strValues).bind() }
            ) { q1, q2, q3, q4 -> ClientEntityCsv2(q1, q2, q3, q4) }

        }

        return d.mapLeft { LineError(lineIndex, it) }
    }


}