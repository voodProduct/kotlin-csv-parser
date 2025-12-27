package ru.vood.kotlin.csv.parser.either

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import ru.vood.kotlin.csv.parser.*
import ru.vood.kotlin.csv.parser.dto.NotParsedCsvLine
import ru.vood.kotlin.csv.parser.dto.ParsedHeader
import ru.vood.kotlin.csv.parser.either.ClientFieldsEnum.*
import ru.vood.kotlin.csv.parser.error.ICsvError

class ClientEntityTemplateTestEither() : CsvEntityTemplate<ClientEntityCsv>() {

    context(notParsedCsvLine: NotParsedCsvLine, parsedHeader: ParsedHeader)
    override fun parseLine(
        headerWithIndex: ParsedHeader,
        strValues: NotParsedCsvLine
    ): Either<NonEmptyList<ICsvError>, ClientEntityCsv> = either {
        zipOrAccumulate(
            { NAME.getString().bind() },
            {
                val bind = AGE1.getInt().bind()
                ensure(bind > 0) { raise(BadFieldValue(AGE1, "must be > 0")) }
                bind
            },
            {
                AGE2.getInt()
                    .validate(
                        check = { it > 0 },
                        raise = { BadFieldValue(AGE2, "must be > 0") }
                    )
                    .bind()
            },
            { AGE3.getInt().bind() },
            { EyeColour.getEnum<EyeColourEnum, ClientEntityCsv> { EyeColourEnum.valueOf(it) }.bind() }
        ) { q1, q2, q3, q4, q5 -> ClientEntityCsv(q1, q2, q3, q4, q5) }
    }


}