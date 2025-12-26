package ru.vood.kotlin.csv.parser.error

import arrow.core.NonEmptyList
import ru.vood.kotlin.csv.parser.IFieldConstants
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

data class LineError(
    val lineIndex: Int,
    val errors: NonEmptyList<ICsvError>
)
