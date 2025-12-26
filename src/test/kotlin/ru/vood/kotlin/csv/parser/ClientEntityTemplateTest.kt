package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import arrow.core.right
import ru.vood.kotlin.csv.parser.error.LineParseError

class ClientEntityTemplateTest() : CsvEntityTemplate<ClientEntityCsv>() {

    override fun toEntity(
        strValues: List<String>,
        headerWithIndex: ParsedHeader
    ): Either<Throwable, ClientEntityCsv> {

        val prepareConvert = prepareConvert(
            headerWithIndex,
            strValues,
            { mapHeaderWithIndex, strValues -> ClientFieldsEnum.NAME.getString(mapHeaderWithIndex, strValues) },
            ClientFieldsEnum.AGE::getLong
        )

        if (prepareConvert.isNotEmpty())
            return Either.Left(IllegalStateException(prepareConvert.joinToString(separator = "\n")))

        return ClientEntityCsv(
            name = ClientFieldsEnum.NAME.getString(headerWithIndex.headerWithIndex, strValues),
            age = ClientFieldsEnum.AGE.getInt(headerWithIndex.headerWithIndex, strValues)
        ).right()
    }

    override fun toEntityEither(
        strValues: List<String>,
        lineIndex: Int,
        headerWithIndex: ParsedHeader
    ): Either<LineParseError, ClientEntityCsv> {

        val d = either {
            zipOrAccumulate(
                { ClientFieldsEnum.NAME.getString(headerWithIndex.headerWithIndex, strValues) },
                { ClientFieldsEnum.AGE.getIntEither(headerWithIndex.headerWithIndex, strValues).bind() }
            ) { q1, q2 -> ClientEntityCsv(q1, q2) }

        }
        TODO()
//        return d

//
//    val either = either {
//        zipOrAccumulateMy<ICsvError, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int>(
//            { 1 },
//            { 1 },
//            { 1 },
//            { 1 },
//            { 1 },
//            { 1 },
//            { 1 },
//            { 1 },
//            { 1 },
//            { 1 },
//        ) { q1, q2, q3, q4, q5, q6, q7, q8, q9, q10 ->
//
//            1
//        }
//    }
//        either {
//            zipOrAccumulate()
//            TODO()
//        }
//    TODO("Not yet implemented")
    }


}