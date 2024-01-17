package no.nav.familie.ef.infotrygd.model.converters

import jakarta.persistence.AttributeConverter
import no.nav.commons.foedselsnummer.FoedselsNr

class FoedselNrConverter : AttributeConverter<FoedselsNr?, String?> {
    override fun convertToDatabaseColumn(attribute: FoedselsNr?): String? {
        return attribute?.asString
    }

    override fun convertToEntityAttribute(dbData: String?): FoedselsNr? {
        return dbData?.let { FoedselsNr(it) }
    }
}
