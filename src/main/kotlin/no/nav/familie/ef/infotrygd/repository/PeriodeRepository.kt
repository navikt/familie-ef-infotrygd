package no.nav.familie.ef.infotrygd.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.familie.ef.infotrygd.model.StønadType
import no.nav.familie.ef.infotrygd.rest.api.*
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.YearMonth

/**
 *  FOM < TOM - Det finnes perioder med TOM < FOM og denne filtrerer bort de.
 *
 *  KODE_RUTINE
 *     EO: Overgangsstønad
 *     EB, EU, FL: Stønad til barnetilsyn, Skolepenger og Tilskudd. Grunnen til att disse hentes er under avklaring, burde være tilsrekkelig att man kun henter EO
 *  E.KODE
 *     AN: Annulert
 *     UA: Uavklart
 */
@Repository
class PeriodeRepository(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    /**
     * Forenkled versjon av spørringen på https://confluence.adeo.no/pages/viewpage.action?pageId=395741283
     *
     * INFO: Det er riktig att det står
     *      AND V.DATO_INNV_FOM <= :tom
     *      AND V.DATO_INNV_TOM >= :fom
     *
     *
     *      fom/tom settes til dagens dato som default, det er slik InfotrygdVedtak_v1 gjorde det.
     */
    fun hentPerioderForArena(request: PeriodeArenaRequest): List<ArenaPeriode> {
        val values = MapSqlParameterSource()
            .addValue("personIdenter", request.personIdenter.map { it.asString })
            .addValue("stønadskoder", StønadType.values().map { it.kodeRutine })
            .addValue("kodeAnnulert", InfotrygdEndringKode.ANNULERT.infotrygdKode)
            .addValue("kodeUaktuell", InfotrygdEndringKode.UAKTUELL.infotrygdKode)
            .addValue("fom", request.fomDato ?: LocalDate.now())
            .addValue("tom", request.tomDato ?: LocalDate.now())
        return jdbcTemplate.query(
            """
            SELECT DISTINCT l.personnr
              ,v.tknr
              ,s.stonad_id
              ,d.type_sats
              ,s.dato_start
              ,v.dato_innv_fom
              ,v.dato_innv_tom
              ,d.belop
              ,s.dato_opphor
           FROM t_lopenr_fnr l
            JOIN t_stonad s ON s.person_lopenr = l.person_lopenr
            JOIN t_vedtak v ON v.stonad_id = s.stonad_id
            JOIN t_delytelse d ON d.vedtak_id = v.vedtak_id
            JOIN t_endring e ON e.vedtak_id = v.vedtak_id 
           WHERE l.personnr IN (:personIdenter)
              AND s.oppdrag_id IS NOT NULL
              AND v.kode_rutine IN (:stønadskoder) 
              AND e.kode <> :kodeAnnulert
              AND e.kode <> :kodeUaktuell
              AND v.dato_innv_fom <= :tom
              AND v.dato_innv_tom >= :fom
              AND v.dato_innv_fom < v.dato_innv_tom
      """, values
        ) { rs, _ ->
            ArenaPeriode(
                FoedselsNr(rs.getString("PERSONNR")),
                rs.getDate("DATO_INNV_FOM").toLocalDate(),
                rs.getDate("DATO_INNV_TOM").toLocalDate(),
                rs.getDate("DATO_OPPHOR")?.toLocalDate(),
                rs.getFloat("BELOP")
            )
        }
    }

    fun hentPerioder(request: PeriodeRequest): List<Pair<StønadType, Periode>> {
        val values = MapSqlParameterSource()
            .addValue("personIdenter", request.personIdenter.map { it.asString })
            .addValue("stønadskoder", mapStønadskoder(request))
        return jdbcTemplate.query(
            """
            SELECT 
            l.personnr,
            v.kode_rutine,
            e.kode,
            v.brukerid,
            s.stonad_id,
            v.vedtak_id,
            v.tidspunkt_reg,
            ef.stonad_belop,
            ef.innt_fradrag,
            ef.sam_fradrag,
            ef.netto_belop,
            ef.aktivitet,
            ef.kode_overg,
            s.dato_start,
            v.type_sak,
            v.dato_innv_fom,
            v.dato_innv_tom,
            s.dato_opphor,
            ef.barnt_utg,
            (SELECT bg.belop FROM t_beregn_grl bg WHERE bg.vedtak_id = v.vedtak_id AND bg.type_belop = 'ARBM') inntektsgrunnlag
           FROM t_lopenr_fnr l
            JOIN t_stonad s ON s.person_lopenr = l.person_lopenr
            JOIN t_vedtak v ON v.stonad_id = s.stonad_id
            JOIN t_endring e ON e.vedtak_id = v.vedtak_id 
            JOIN t_ef ef ON ef.vedtak_id = v.vedtak_id
           WHERE l.personnr IN (:personIdenter)
              AND s.oppdrag_id IS NOT NULL
              AND v.kode_rutine IN (:stønadskoder)
              AND v.dato_innv_fom < v.dato_innv_tom
           ORDER BY s.stonad_id ASC, vedtak_id ASC, dato_innv_fom DESC
      """, values
        ) { rs, _ ->
            StønadType.fraKodeRutine(rs.getString("kode_rutine")) to
                    Periode(
                        personIdent = rs.getString("personnr"),
                        sakstype = InfotrygdSakstype.fraInfotrygdKode(rs.getString("type_sak").trim()),
                        kode = InfotrygdEndringKode.fraInfotrygdKode(rs.getString("kode").trim()),
                            kodeOvergangsstønad = mapVerdi(rs.getString("kode_overg"),
                                                           InfotrygdOvergangsstønadKode.Companion::fraInfotrygdKode),
                            aktivitetstype = mapVerdi(rs.getString("aktivitet"),
                                                      InfotrygdAktivitetstype.Companion::fraInfotrygdKode),
                        brukerId = rs.getString("brukerid"),
                        stønadId = rs.getLong("stonad_id"),
                        vedtakId = rs.getLong("vedtak_id"),
                        vedtakstidspunkt = rs.getTimestamp("tidspunkt_reg").toLocalDateTime(),
                        stønadBeløp = rs.getInt("stonad_belop"),
                        engangsbeløp = rs.getInt("stonad_belop"),
                        inntektsgrunnlag = rs.getInt("inntektsgrunnlag"),
                        inntektsreduksjon = rs.getInt("innt_fradrag"),
                        samordningsfradrag = rs.getInt("sam_fradrag"),
                        utgifterBarnetilsyn = rs.getInt("barnt_utg"),
                        beløp = rs.getInt("netto_belop"),
                        månedsbeløp = rs.getInt("netto_belop"),
                        startDato = rs.getDate("dato_start").toLocalDate(),
                        stønadFom = rs.getDate("dato_innv_fom").toLocalDate(),
                        stønadTom = rs.getDate("dato_innv_tom").toLocalDate(),
                        opphørsdato = rs.getDate("dato_opphor")?.toLocalDate()
                    )
        }
    }

    /**
     * Finner personer med vedtak som har tom-dato fra neste måned
     */
    // language=PostgreSQL
    fun hentPersonerForMigrering(antall: Int): PersonerForMigrering {
        val values = MapSqlParameterSource()
            .addValue("stønadskode", StønadType.OVERGANGSSTØNAD.kodeRutine)
            .addValue("nesteMåned", YearMonth.now().plusMonths(1).atDay(1))
            .addValue("antall", antall)
        val identer = jdbcTemplate.query("""
            WITH vedtak AS (SELECT l.personnr, s.stonad_id, v.vedtak_id, v.dato_innv_fom fom, 
            (CASE WHEN (NVL(s.dato_opphor, v.dato_innv_tom) < v.dato_innv_tom) THEN s.dato_opphor ELSE v.dato_innv_tom END) tom
            FROM t_lopenr_fnr l
              JOIN t_stonad s ON s.person_lopenr = l.person_lopenr
              JOIN t_vedtak v ON v.stonad_id = s.stonad_id
              JOIN t_endring e ON e.vedtak_id = v.vedtak_id
            WHERE s.oppdrag_id IS NOT NULL
              AND v.kode_rutine = :stønadskode
              AND e.kode <> 'AN'
              AND e.kode <> 'UA'
              AND v.dato_innv_fom < v.dato_innv_tom
              AND (s.dato_opphor IS NULL OR s.dato_opphor > v.dato_innv_fom))
            , maxvedtakid AS (SELECT personnr, stonad_id, MAX(vedtak_id) vedtak_id FROM vedtak GROUP BY personnr, stonad_id)
            SELECT personnr FROM (SELECT q1.personnr, MAX(tom) 
            FROM maxvedtakid q1 JOIN vedtak q2 ON q1.vedtak_id = q2.vedtak_id
            WHERE q2.tom > :nesteMåned
            GROUP BY q1.personnr) WHERE rownum < :antall
        """, values) { rs, _ ->
            rs.getString("personnr")
        }
        return PersonerForMigrering(identer.toSet())
    }

    fun hentBarnForPerioder(barnetilsynPerioder: List<Periode>): Map<String, List<String>> {
        if (barnetilsynPerioder.isEmpty()) {
            return emptyMap()
        }
        val values = MapSqlParameterSource()
            .addValue("vedtakIdListe", barnetilsynPerioder.map { it.vedtakId })

        return jdbcTemplate.query("""
                SELECT r.vedtak_id, barn.personnr
                FROM t_rolle r JOIN t_lopenr_fnr barn ON barn.person_lopenr = r.person_lopenr_r
                WHERE r.vedtak_id in (:vedtakIdListe)
        """, values
        ) { rs, _ -> rs.getString("vedtak_id") to rs.getString("personnr")}
            .groupBy({ it.first }, { it.second })
    }

    private fun <T> mapVerdi(kode: String, mapper: (String) -> T): T? = kode
        .trim()
        .takeIf(String::isNotEmpty)
        ?.let(mapper)

    private fun mapStønadskoder(request: PeriodeRequest): List<String> {
        return request.stønadstyper.ifEmpty {
            setOf(
                StønadType.OVERGANGSSTØNAD,
                StønadType.SKOLEPENGER,
                StønadType.BARNETILSYN
            )
        }.map { it.kodeRutine }
    }
}
