package ru.vood.kotlin.csv.parser

/**
 * Базовый интерфейс для мапперов из csv dto в dto
 */
sealed interface ICSVLineMapper<CSV_DTO : ICSVLine, DTO> {

    fun mapCSVLineToDto(abstractLineCsv: CSV_DTO): List<DTO>
}