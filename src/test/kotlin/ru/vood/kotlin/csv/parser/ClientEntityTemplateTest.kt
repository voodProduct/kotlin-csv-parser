package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.right

class ClientEntityTemplateTest(
    override val delimiter: String
) : CsvEntityTemplate<ClientEntityCsv>() {

    override fun toEntity(
        strValues: List<String>,
        headerWithIndex: ParsedHeader
    ): Either<Throwable, ClientEntityCsv> {

        val prepareConvert = prepareConvert(
            headerWithIndex,
            strValues,
            ClientFieldsEnum.NAME::getString,
            ClientFieldsEnum.AGE::getLong
        )

        if (prepareConvert.isNotEmpty())
            return Either.Left(IllegalStateException(prepareConvert.joinToString(separator = "\n")))

        return ClientEntityCsv(
            name = ClientFieldsEnum.NAME.getString(headerWithIndex.headerWithIndex, strValues),
            age = ClientFieldsEnum.AGE.getLong(headerWithIndex.headerWithIndex, strValues)
        ).right()
    }


}