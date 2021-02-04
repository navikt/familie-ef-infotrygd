package no.nav.infotrygd.ef.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.ef.rest.api.PeriodeOvergangsstønad
import no.nav.infotrygd.ef.rest.api.PeriodeOvergangsstønadRequest
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class PeriodeRepository(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) {

    /**
     * Forenkled versjon av spørringen på https://confluence.adeo.no/pages/viewpage.action?pageId=395741283
     *
     * INFO: Det er riktig att det står
     *      AND V.DATO_INNV_FOM <= :tom
     *      AND V.DATO_INNV_TOM >= :fom
     *
     *      KODE_RUTINE
     *        EO: Overgangsstønad
     *      E.KODE
     *        AN: Annulert
     *        UA: UAVKLART
     */
    fun hentPerioderForOvergangsstønad(periodeOvergangsstønadRequest: PeriodeOvergangsstønadRequest): List<PeriodeOvergangsstønad> {
        val values = MapSqlParameterSource()
            .addValue("personIdenter", periodeOvergangsstønadRequest.personIdenter.map { it.asString })
            .addValue("fom", periodeOvergangsstønadRequest.fomDato ?: LocalDate.of(1, 1, 1))
            .addValue("tom", periodeOvergangsstønadRequest.tomDato ?: LocalDate.of(9999, 1, 1))
        return namedParameterJdbcTemplate.query(
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
              AND V.KODE_RUTINE = 'EO' 
              AND E.KODE <> 'AN'
              AND E.KODE <> 'UA'
              AND V.DATO_INNV_FOM <= :tom
              AND V.DATO_INNV_TOM >= :fom
              AND V.DATO_INNV_FOM < V.DATO_INNV_TOM
      """, values
        ) { rs, _ ->
            PeriodeOvergangsstønad(
                FoedselsNr(rs.getString("PERSONNR")),
                rs.getDate("DATO_INNV_FOM").toLocalDate(),
                rs.getDate("DATO_INNV_TOM").toLocalDate(),
                rs.getDate("DATO_OPPHOR")?.toLocalDate(),
                rs.getFloat("BELOP")
            )
        }
    }
}