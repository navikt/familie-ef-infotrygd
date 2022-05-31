package no.nav.familie.ef.infotrygd.model.converters

import javax.persistence.AttributeConverter

class ReversedLongFoedselNrConverter : AttributeConverter<String?, Long?> {
    private val converter = ReversedFoedselNrConverter()

    override fun convertToDatabaseColumn(attribute: String?): Long? {
        return converter.convertToDatabaseColumn(attribute)?.toLong() ?: 0
    }

    override fun convertToEntityAttribute(dbData: Long?): String? {
        return converter.convertToEntityAttribute(dbData?.toString()?.padStart(11, '0'))
    }
}
