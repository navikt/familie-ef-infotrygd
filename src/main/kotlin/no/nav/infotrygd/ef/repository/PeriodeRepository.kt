package no.nav.infotrygd.ef.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.ef.rest.api.Periode
import no.nav.infotrygd.ef.rest.api.PeriodeArenaRequest
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class PeriodeRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    /**
     * Forenkled versjon av spørringen på https://confluence.adeo.no/pages/viewpage.action?pageId=395741283
     *
     * INFO: Det er riktig att det står
     *      AND V.DATO_INNV_FOM <= :tom
     *      AND V.DATO_INNV_TOM >= :fom
     *
     *      FOM < TOM - Det finnes perioder med TOM < FOM og denne filtrerer bort de.
     *
     *      KODE_RUTINE
     *        EO: Overgangsstønad
     *        EB, EU, FL: Stønad til barnetilsyn, Skolepenger og Tilskudd. Grunnen til att disse hentes er under avklaring, burde være tilsrekkelig att man kun henter EO
     *      E.KODE
     *        AN: Annulert
     *        UA: Uavklart
     *
     *      fom/tom settes til dagens dato som default, det er slik InfotrygdVedtak_v1 gjorde det.
     */
    fun hentPerioder(request: PeriodeArenaRequest): List<Periode> {
        val values = MapSqlParameterSource()
            .addValue("personIdenter", request.personIdenter.map { it.asString })
            .addValue("fom", request.fomDato ?: LocalDate.now())
            .addValue("tom", request.tomDato ?: LocalDate.now())
        return jdbcTemplate.query(
            """
            SELECT DISTINCT L.PERSONNR
              ,V.TKNR
              ,S.STONAD_ID
              ,D.TYPE_SATS
              ,S.DATO_START
              ,V.DATO_INNV_FOM
              ,V.DATO_INNV_TOM
              ,D.BELOP
              ,S.DATO_OPPHOR
           FROM T_LOPENR_FNR L
            JOIN T_STONAD S ON S.PERSON_LOPENR = L.PERSON_LOPENR
            JOIN T_VEDTAK V ON V.STONAD_ID = S.STONAD_ID
            JOIN T_DELYTELSE D ON D.VEDTAK_ID = V.VEDTAK_ID
            JOIN T_ENDRING E ON E.VEDTAK_ID = V.VEDTAK_ID 
           WHERE L.PERSONNR IN (:personIdenter)
              AND S.OPPDRAG_ID IS NOT NULL
              AND V.KODE_RUTINE IN ('EO','EB','EU','FL')  
              AND E.KODE <> 'AN'
              AND E.KODE <> 'UA'
              AND V.DATO_INNV_FOM <= :tom
              AND V.DATO_INNV_TOM >= :fom
              AND V.DATO_INNV_FOM < V.DATO_INNV_TOM
      """, values
        ) { rs, _ ->
            Periode(
                FoedselsNr(rs.getString("PERSONNR")),
                rs.getDate("DATO_INNV_FOM").toLocalDate(),
                rs.getDate("DATO_INNV_TOM").toLocalDate(),
                rs.getDate("DATO_OPPHOR")?.toLocalDate(),
                rs.getFloat("BELOP")
            )
        }
    }
}