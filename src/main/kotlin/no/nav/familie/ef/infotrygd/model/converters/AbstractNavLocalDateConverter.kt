package no.nav.familie.ef.infotrygd.model.converters

import jakarta.persistence.AttributeConverter
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

open class AbstractNavLocalDateConverter(
    datePattern: String,
) : AttributeConverter<LocalDate?, Int?> {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val formatter = DateTimeFormatter.ofPattern(datePattern)

    override fun convertToDatabaseColumn(attribute: LocalDate?): Int? = attribute?.format(formatter)?.toInt()

    override fun convertToEntityAttribute(dbData: Int?): LocalDate? {
        if (dbData == null || dbData == NULL_VALUE) {
            return null
        }
        return try {
            LocalDate.from(formatter.parse(String.format("%08d", dbData)))
        } catch (e: Exception) {
            /*
                Det finnes datoer i databasen som er ugyldige fordi at de er ført feil i kombinasjon
                med manglende input-validering. I disse tilfellene så har vi ikke informasjon om dato
                så vi returnerer et tomt resultat.
             */
            logger.warn("Kunne ikke lese dato: '$dbData'")
            null
        }
    }

    companion object {
        const val NULL_VALUE = 0
    }
}
