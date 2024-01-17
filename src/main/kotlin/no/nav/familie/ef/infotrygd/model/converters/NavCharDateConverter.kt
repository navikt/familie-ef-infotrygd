package no.nav.familie.ef.infotrygd.model.converters

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.LocalDate

@Converter
class NavCharDateConverter : AttributeConverter<LocalDate?, String?> {
    private val converter = NavLocalDateConverter()

    override fun convertToDatabaseColumn(attribute: LocalDate?): String? {
        return converter.convertToDatabaseColumn(attribute)?.toString()
    }

    override fun convertToEntityAttribute(dbData: String?): LocalDate? {
        return dbData?.let { converter.convertToEntityAttribute(it.toInt()) }
    }
}
