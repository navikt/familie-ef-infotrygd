package no.nav.familie.ef.infotrygd.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.familie.ef.infotrygd.model.StønadType
import no.nav.familie.ef.infotrygd.rest.api.ArenaPeriode
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSakstype
import no.nav.familie.ef.infotrygd.rest.api.PeriodeArenaRequest
import no.nav.familie.ef.infotrygd.rest.api.PeriodeRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class PeriodeRepositoryTest {

    @Autowired lateinit var periodeRepository: PeriodeRepository
    @Autowired lateinit var jdbcTemplate: JdbcTemplate

    private val startdato = LocalDate.now().minusYears(1)
    private val sluttdato = LocalDate.now().plusYears(1)
    private val førStartdato = startdato.minusYears(1)
    private val etterSluttdato = sluttdato.plusYears(1)

    @Before
    fun setUp() {
        jdbcTemplate.update("INSERT INTO T_LOPENR_FNR (PERSON_LOPENR, PERSONNR) VALUES (1, '01234567890')")
        jdbcTemplate.update(
                """INSERT INTO T_VEDTAK (VEDTAK_ID, PERSON_LOPENR, STONAD_ID, KODE_RUTINE, DATO_INNV_FOM,
                     DATO_INNV_TOM, BRUKERID, TYPE_SAK, TIDSPUNKT_REG)
                      VALUES (1,1,1,'EO',?,?, 'NISSEN', 'S ', current_timestamp)""",
                startdato,
                sluttdato
        )
        jdbcTemplate.update(
            """INSERT INTO T_STONAD (STONAD_ID, OPPDRAG_ID, PERSON_LOPENR, DATO_START, DATO_OPPHOR)
                                     VALUES (1, 1, 1, sysdate, null)"""
        )
        jdbcTemplate.update("INSERT INTO T_DELYTELSE (VEDTAK_ID, TYPE_SATS, BELOP) VALUES (1, '', 100.34)")
        jdbcTemplate.update("INSERT INTO T_ENDRING (VEDTAK_ID, KODE) VALUES (1, 'F ')")
        jdbcTemplate.update("INSERT INTO T_EF (VEDTAK_ID, STONAD_BELOP, INNT_FRADRAG, NETTO_BELOP, SAM_FRADRAG, KODE_OVERG, AKTIVITET)" +
                            " VALUES (1,1,1,1,1,' ',' ')")
        jdbcTemplate.update("INSERT INTO T_BEREGN_GRL (VEDTAK_ID, TYPE_BELOP, FOM, BELOP, BRUKERID) VALUES (1,'ARBM',current_date, 100, 'A')")
        jdbcTemplate.update("INSERT INTO T_BEREGN_GRL (VEDTAK_ID, TYPE_BELOP, FOM, BELOP, BRUKERID) VALUES (1,'ABCD',current_date, 50, 'A')")
    }

    @After
    fun tearDown() {
        listOf("T_LOPENR_FNR", "T_VEDTAK", "T_STONAD", "T_DELYTELSE", "T_ENDRING", "T_EF", "T_BEREGN_GRL").forEach {
            jdbcTemplate.update("TRUNCATE TABLE $it")
        }
    }

    @Test
    fun `skal hente perioder`() {
        val perioder = periodeRepository.hentPerioder(PeriodeRequest(setOf(FoedselsNr("01234567890")),
                                                                     setOf(StønadType.OVERGANGSSTØNAD)))
        assertThat(perioder).hasSize(1)
        assertThat(perioder.first().first).isEqualTo(StønadType.OVERGANGSSTØNAD)
    }

    @Test
    fun `uten datoer`() {
        assertThat(hentPerioderForArena()).hasSize(1)
    }

    @Test
    fun `beløp er riktig`() {
        val perioder = hentPerioderForArena()
        assertThat(perioder).hasSize(1)
        assertThat(perioder.first().beløp).isEqualTo(100.34f)
    }

    @Test
    fun `fom og tom datoer`() {
        assertThat(hentPerioderForArena(førStartdato, LocalDate.now()))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioderForArena(førStartdato, etterSluttdato))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioderForArena(LocalDate.now(), LocalDate.now()))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioderForArena(LocalDate.now(), etterSluttdato))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioderForArena(førStartdato, førStartdato))
            .withFailMessage("TOM dato < FOM dato(db)")
            .isEmpty()

        assertThat(hentPerioderForArena(etterSluttdato, etterSluttdato))
            .withFailMessage("FOM dato > TOM dato(db)")
            .isEmpty()
    }

    @Test
    fun `uten tom dato`() {
        assertThat(hentPerioderForArena(førStartdato, null))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioderForArena(LocalDate.now(), null))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioderForArena(etterSluttdato, null))
            .withFailMessage("FOM dato > TOM dato(db)")
            .isEmpty()
    }

    @Test
    fun `uten fom dato`() {
        assertThat(hentPerioderForArena(null, førStartdato))
            .withFailMessage("TOM dato < FOM dato(db)")
            .isEmpty()

        assertThat(hentPerioderForArena(null, LocalDate.now()))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioderForArena(null, etterSluttdato))
            .withFailMessage("FOM dato > TOM dato(db)")
            .hasSize(1)
    }

    @Test
    fun `henting av perioder er riktig`() {
        val perioder = hentPerioder()
        assertThat(perioder).hasSize(1)
        val periode = perioder.first().second
        assertThat(periode.inntektsgrunnlag).isEqualTo(150)
        assertThat(periode.sakstype).isEqualTo(InfotrygdSakstype.SØKNAD)
    }

    private fun hentPerioder() =
            periodeRepository.hentPerioder(PeriodeRequest(setOf(FoedselsNr("01234567890")), StønadType.values().toSet()))

    private fun hentPerioderForArena(fomDato: LocalDate? = null, tomDato: LocalDate? = null): List<ArenaPeriode> =
        periodeRepository.hentPerioderForArena(
            PeriodeArenaRequest(
                        personIdenter = setOf(FoedselsNr("01234567890")),
                        fomDato,
                        tomDato
            )
        )
}