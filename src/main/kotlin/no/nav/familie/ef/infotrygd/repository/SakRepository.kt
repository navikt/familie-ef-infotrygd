package no.nav.familie.ef.infotrygd.repository

import no.nav.familie.ef.infotrygd.model.StønadType
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSak
import no.nav.familie.ef.infotrygd.rest.api.Saktreff
import no.nav.familie.ef.infotrygd.utils.reverserFnr
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class SakRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun finnesSaker(personIdenter: Set<String>): List<Saktreff> {

        val values = MapSqlParameterSource()
                .addValue("personIdenter", personIdenter.map(String::reverserFnr))
                .addValue("s10_valg", StønadType.values().map { it.saS10Valg })

        return jdbcTemplate.query(
                """
            SELECT s.f_nr, s.s10_valg FROM sa_sak_10 s
            WHERE s.f_nr IN (:personIdenter)
            AND s.s10_kapittelnr = 'EF'
            AND s.s10_valg IN (:s10_valg)
            GROUP BY s.f_nr, s.s10_valg
        """, values
        ) { resultSet, _ ->
            Saktreff(resultSet.getString("f_nr").reverserFnr(),
                     StønadType.fraS10Valg(resultSet.getString("s10_valg")))
        }
    }

    fun hentSaker(personIdenter: Set<String>): List<InfotrygdSak> {
        val values = MapSqlParameterSource()
                .addValue("personIdenter", personIdenter.map(String::reverserFnr))
        return jdbcTemplate.query(
                """
            SELECT * FROM sa_sak_10 s
            WHERE s.f_nr IN (:personIdenter)
            AND s.s10_kapittelnr = 'EF'
            GROUP BY s.f_nr, s.s10_valg
        """, values
        ) { resultSet, _ ->
            Saktreff(resultSet.getString("f_nr").reverserFnr(),
                     StønadType.fraS10Valg(resultSet.getString("s10_valg")))
        }
    }
}
