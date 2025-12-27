package ru.vood.kotlin.csv.parser.either

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import ru.vood.kotlin.csv.parser.CsvEntityTemplate
import ru.vood.kotlin.csv.parser.dto.NotParsedCsvLine
import ru.vood.kotlin.csv.parser.dto.ParsedHeader
import ru.vood.kotlin.csv.parser.either.ClientFieldsEnum.*
import ru.vood.kotlin.csv.parser.error.ICsvError
import ru.vood.kotlin.csv.parser.getEnum
import ru.vood.kotlin.csv.parser.getInt
import ru.vood.kotlin.csv.parser.getString

class ClientEntityTemplateTestEither() : CsvEntityTemplate<ClientEntityCsv>() {

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    override fun parseLine(
        headerWithIndex: ParsedHeader,
        strValues: NotParsedCsvLine
    ): Either<NonEmptyList<ICsvError>, ClientEntityCsv> = either {
        zipOrAccumulate(
            { NAME.getString().bind() },
            { AGE1.getInt().bind() },
            { AGE2.getInt().bind() },
            { AGE3.getInt().bind() },
            { EyeColour.getEnum<EyeColourEnum, ClientEntityCsv> { EyeColourEnum.valueOf(it) }.bind() }
        ) { q1, q2, q3, q4, q5 -> ClientEntityCsv(q1, q2, q3, q4, q5) }
    }


}