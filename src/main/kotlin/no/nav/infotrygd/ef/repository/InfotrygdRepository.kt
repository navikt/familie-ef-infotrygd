package no.nav.infotrygd.ef.repository

import no.nav.infotrygd.ef.model.StønadType
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDate


@Repository
class InfotrygdRepository(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) {

    fun harStønad(identer: Set<String>, typer: Set<StønadType>, kunAktive: Boolean = false): Map<StønadType, Boolean> {
        val values = MapSqlParameterSource()
                .addValue("identer", identer)
                .addValue("kodeRutiner", typer.map { it.kodeRutine })
        val filter: String = if (kunAktive) {
            values.addValue("dagensDato", LocalDate.now()) //TODO
            " AND (DATO_OPPHOR IS NULL OR DATO_OPPHOR > :dagensDato) "
        } else {
            ""
        }

        val result = namedParameterJdbcTemplate.query(
                """
            SELECT S.KODE_RUTINE, count(*) cn 
              FROM T_LOPENR_FNR L
              JOIN T_STONAD S ON S.PERSON_LOPENR = L.PERSON_LOPENR
            WHERE L.PERSONNR IN (:identer)
              AND S.KODE_RUTINE IN (:kodeRutiner)
              $filter
            GROUP BY S.KODE_RUTINE
        """, values
        ) { resultSet, _ ->
            Pair(StønadType.fraKodeRutine(resultSet.getString("KODE_RUTINE")),
                 resultSet.getLong("cn") > 0)
        }
        return result.toMap()
    }
}