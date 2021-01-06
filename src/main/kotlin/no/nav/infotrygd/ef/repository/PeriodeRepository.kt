package no.nav.infotrygd.ef.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.infotrygd.ef.rest.api.OvergangsstønadPeriode
import no.nav.infotrygd.ef.rest.api.OvergangsstønadPeriodeRequest
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class PeriodeRepository(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) {

    fun hentOvergangsstønadPerioder(overgangsstønadPeriodeRequest: OvergangsstønadPeriodeRequest): List<OvergangsstønadPeriode> {
        val values = MapSqlParameterSource()
            .addValue("fnr", overgangsstønadPeriodeRequest.identer.map { it.asString })
            .addValue("fom", overgangsstønadPeriodeRequest.fomDato ?: LocalDate.of(1, 1, 1))
            .addValue("tom", overgangsstønadPeriodeRequest.tomDato ?: LocalDate.of(9999, 1, 1))
        return namedParameterJdbcTemplate.query(
            """
    SELECT DISTINCT L.PERSONNR
      ,V.TKNR
      ,S.STONAD_ID
      ,K1.KODE_KLASSE
      ,K2.KODE_KLASSE
      ,K3.KODE_KLASSE
      ,D.TYPE_SATS
      ,S.DATO_START
      ,V.DATO_INNV_FOM
      ,V.DATO_INNV_TOM
      ,D.BELOP
      ,S.DATO_OPPHOR
   FROM T_LOPENR_FNR L
      ,T_VEDTAK V
      ,T_STONAD S
      ,T_DELYTELSE D
      ,T_ENDRING E
      ,T_STONADSKLASSE K1
      ,T_STONADSKLASSE K2
      ,T_STONADSKLASSE K3

   WHERE L.PERSONNR IN (:fnr)
      AND S.OPPDRAG_ID IS NOT NULL
      AND S.PERSON_LOPENR = L.PERSON_LOPENR
      AND V.STONAD_ID = S.STONAD_ID
      AND V.KODE_RUTINE IN ('EO') 
      AND D.VEDTAK_ID = V.VEDTAK_ID
      AND E.VEDTAK_ID = V.VEDTAK_ID
      AND E.KODE <> 'AN'
      AND E.KODE <> 'UA'
      AND V.DATO_INNV_FOM <= :tom
      AND V.DATO_INNV_TOM >= :fom
      AND V.DATO_INNV_FOM < V.DATO_INNV_TOM
      AND K1.VEDTAK_ID = V.VEDTAK_ID
      AND K2.VEDTAK_ID = V.VEDTAK_ID
      AND K3.VEDTAK_ID = V.VEDTAK_ID
      AND K1.KODE_NIVAA = 'EF'
      AND K2.KODE_NIVAA = 'OG' 
      AND K3.KODE_NIVAA IN ('NY', 'OR') 
        """, values
        ) { rs, _ ->
            OvergangsstønadPeriode(
                FoedselsNr(rs.getString("PERSONNR")),
                rs.getDate("DATO_INNV_FOM").toLocalDate(),
                rs.getDate("DATO_INNV_TOM").toLocalDate(),
                true
            )
        }
    }
}