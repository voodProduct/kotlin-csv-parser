package ru.vood.kotlin.csv.parser

import arrow.core.Either
import arrow.core.right

class ClientEntityTemplateTest(
    override val header: String,
    override val delimiter: String
) : CsvEntityTemplate<ClientEntityCsv>() {

    override fun toEntity(strValues: List<String>): Either<Throwable, ClientEntityCsv> {

        val prepareConvert = prepareConvert(
            mapHeaderWithIndex,
            strValues,
            ClientFieldsEnum.NAME::getString,
                    ClientFieldsEnum.AGE::getLong
        )

        if (prepareConvert.isNotEmpty())
            return Either.Left(IllegalStateException(prepareConvert.joinToString(separator = "\n")))

        return ClientEntityCsv(
            name = ClientFieldsEnum.NAME.getString(mapHeaderWithIndex, strValues),
            age = ClientFieldsEnum.AGE.getLong(mapHeaderWithIndex, strValues)
        ).right()
    }
}