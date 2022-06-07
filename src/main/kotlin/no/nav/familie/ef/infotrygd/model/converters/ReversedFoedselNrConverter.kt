package no.nav.familie.ef.infotrygd.model.converters

import no.nav.familie.ef.infotrygd.utils.reverserFnr
import javax.persistence.AttributeConverter

class ReversedFoedselNrConverter : AttributeConverter<String?, String?> {
    override fun convertToDatabaseColumn(attribute: String?): String? {
        return attribute?.reverserFnr() ?: "00000000000"
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        if (dbData == null) {
            return null
        }

        if (dbData.toLong() == 0L) {
            return null
        }

        return dbData.reverserFnr()
    }
}
