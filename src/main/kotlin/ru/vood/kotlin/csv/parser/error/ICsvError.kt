package ru.vood.kotlin.csv.parser.error

import kotlin.reflect.KClass

sealed interface ICsvError

data class CastError(
    val errorClass: KClass<out Throwable>,
    val errorMsg: String
) : ICsvError

data class UnsupportedClassError(
    val errorMsg: String
) : ICsvError

data class UnsupportedBooleanValueError(
    val errorMsg: String
) : ICsvError