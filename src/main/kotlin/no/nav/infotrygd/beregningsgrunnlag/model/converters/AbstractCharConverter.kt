package no.nav.infotrygd.beregningsgrunnlag.model.converters

import javax.persistence.AttributeConverter
import javax.persistence.Converter

abstract class AbstractCharConverter(private val size: Int) : AttributeConverter<String?, String?> {
    override fun convertToDatabaseColumn(attribute: String?): String? {
        val str = attribute ?: ""
        return str.padEnd(size, ' ')
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        if(dbData.isNullOrBlank()) {
            return null
        }

        return dbData.trimEnd()
    }

}

@Converter
class BrukerIdConverter : AbstractCharConverter(7)