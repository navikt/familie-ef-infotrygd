package no.nav.familie.ef.infotrygd.repository

import no.nav.familie.ef.infotrygd.model.StønadType
import no.nav.familie.ef.infotrygd.model.converters.NavReversedLocalDateConverter
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSak
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSakNivå
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSakResultat
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSakType
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSakUndervalg
import no.nav.familie.ef.infotrygd.rest.api.Saktreff
import no.nav.familie.ef.infotrygd.utils.reverserFnr
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class SakRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    private val datoConverter = NavReversedLocalDateConverter()

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

    fun finnSaker(personIdenter: Set<String>): List<InfotrygdSak> {
        val values = MapSqlParameterSource()
                .addValue("personIdenter", personIdenter.map(String::reverserFnr))
        return jdbcTemplate.query(
                """
            SELECT * FROM sa_sak_10 s
            WHERE s.f_nr IN (:personIdenter)
            AND s.s10_kapittelnr = 'EF'
        """, values
        ) { rs, _ ->
            InfotrygdSak(
                    id = rs.getLong("ID_SAK"),
                    saksnr = rs.getString("S10_SAKSNR"),
                    saksblokk = rs.getString("S05_SAKSBLOKK"),
                    registrertDato = datoConverter.convertToEntityAttribute(rs.getInt("S10_REG_DATO")),
                    mottattDato = datoConverter.convertToEntityAttribute(rs.getInt("S10_MOTTATTDATO")),
                    kapittelnr = rs.getString("S10_KAPITTELNR"),
                    stønadType = rs.getString("S10_VALG").let(StønadType.Companion::fraS10Valg),
                    undervalg = rs.getString("S10_UNDERVALG").let(InfotrygdSakUndervalg.Companion::fraInfotrygdKode),
                    type = rs.getString("S10_TYPE").let(InfotrygdSakType.Companion::fraInfotrygdKode),
                    nivå = rs.getString("S10_NIVAA").let(InfotrygdSakNivå.Companion::fraInfotrygdKode),
                    resultat = rs.getString("S10_RESULTAT").let(InfotrygdSakResultat.Companion::fraInfotrygdKode),
                    vedtaksdato = datoConverter.convertToEntityAttribute(rs.getInt("S10_VEDTAKSDATO")),
                    iverksattdato = datoConverter.convertToEntityAttribute(rs.getInt("S10_IVERKSATTDATO")),
                    årsakskode = rs.getString("S10_AARSAKSKODE"),
                    behandlendeEnhet = rs.getString("S10_BEHEN_ENHET"),
                    registrertAvEnhet = rs.getString("S10_REG_AV_ENHET"),
                    tkNr = rs.getString("TK_NR"),
                    region = rs.getString("F_NR"),
            )
        }
    }
}
