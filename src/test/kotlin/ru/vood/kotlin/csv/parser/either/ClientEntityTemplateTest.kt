package ru.vood.kotlin.csv.parser.either

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import ru.vood.kotlin.csv.parser.CsvEntityTemplate
import ru.vood.kotlin.csv.parser.ParsedHeader
import ru.vood.kotlin.csv.parser.error.ILineError
import ru.vood.kotlin.csv.parser.error.LineDtoCreateError
import ru.vood.kotlin.csv.parser.error.LineParseError

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
    ): Either<ILineError, ClientEntityCsv2> {


        val mapLeft: Either<ILineError, ClientEntityCsv2> = Either.catch {
            either {
                zipOrAccumulate(
                    { ClientFieldsEnum2.NAME.getString(headerWithIndex.headerWithIndex, strValues) },
                    { ClientFieldsEnum2.AGE1.getIntEither(headerWithIndex.headerWithIndex, strValues).bind() },
                    { ClientFieldsEnum2.AGE2.getIntEither(headerWithIndex.headerWithIndex, strValues).bind() },
                    { ClientFieldsEnum2.AGE3.getIntEither(headerWithIndex.headerWithIndex, strValues).bind() }
                ) { q1, q2, q3, q4 -> ClientEntityCsv2(q1, q2, q3, q4) }
            }
        }.fold(
            {
                val left: Either<LineDtoCreateError, ClientEntityCsv2> = LineDtoCreateError(
                    lineIndex = lineIndex,
                    strValues = strValues,
                    headerWithIndex = headerWithIndex,
                    errorClass = it::class,
                    errorMsg = it.message
                ).left()
                left
            },
            {
                val mapLeft = it.mapLeft {
                    LineParseError(
                        lineIndex = lineIndex,
                        errors = it,
                        strValues = strValues,
                        headerWithIndex = headerWithIndex
                    )
                }
                mapLeft
            }
        )

        return mapLeft
//        val d =
//            either {
//                zipOrAccumulate(
//                    { ClientFieldsEnum2.NAME.getString(headerWithIndex.headerWithIndex, strValues) },
//                    { ClientFieldsEnum2.AGE1.getIntEither(headerWithIndex.headerWithIndex, strValues).bind() },
//                    { ClientFieldsEnum2.AGE2.getIntEither(headerWithIndex.headerWithIndex, strValues).bind() },
//                    { ClientFieldsEnum2.AGE3.getIntEither(headerWithIndex.headerWithIndex, strValues).bind() }
//                ) { q1, q2, q3, q4 -> ClientEntityCsv2(q1, q2, q3, q4) }
//
//            }
//
//        return d.mapLeft {
//            LineParseError(
//                lineIndex = lineIndex,
//                errors = it,
//                strValues = strValues,
//                headerWithIndex = headerWithIndex
//            )
//        }
    }


}