package ru.vood.kotlin.csv.parser.error

import arrow.core.NonEmptyList
import ru.vood.kotlin.csv.parser.IFieldConstants
import ru.vood.kotlin.csv.parser.ParsedHeader
import kotlin.reflect.KClass

sealed interface ICsvError

data class CsvFieldError(
    val fieldName: IFieldConstants,
    val error: ICastError
) : ICsvError

sealed interface ICastError

data class CastError(
    val errorClass: KClass<out Throwable>,
    val errorMsg: String
) : ICastError

data class UnsupportedClassError(
    val errorMsg: String
) : ICastError

data class UnsupportedBooleanValueError(
    val errorMsg: String
) : ICastError

sealed interface ILineError{
    val lineIndex: Int
    val strValues: List<String>
    val headerWithIndex: ParsedHeader
}

data class LineDtoCreateError(
    override val lineIndex: Int,
    override val strValues: List<String>,
    override val headerWithIndex: ParsedHeader,
    val errorClass: KClass<out Throwable>,
    val errorMsg: String?
): ILineError

data class LineParseError(
    override val lineIndex: Int,
    val errors: NonEmptyList<ICsvError>,
    override val strValues: List<String>,
    override val headerWithIndex: ParsedHeader
):ILineError
