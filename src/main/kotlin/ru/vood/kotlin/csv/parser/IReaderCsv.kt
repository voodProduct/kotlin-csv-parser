package ru.vood.kotlin.csv.parser

import kotlinx.coroutines.flow.Flow

interface IReaderCsv {
    fun <T : ICSVLine> readCSV(
        stringFlow: Flow<String>,
        delimiter: String,
        entity: CsvEntityTemplate<T>,
        mapHeaderWithIndex: Map<String, Int>,
    ): Flow<T>
}
