package no.nav.familie.ef.infotrygd.repository

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.familie.ef.infotrygd.model.StønadType
import no.nav.familie.ef.infotrygd.rest.api.ArenaPeriode
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

    @Before
    fun setUp() {
        jdbcTemplate.update("INSERT INTO T_LOPENR_FNR (PERSON_LOPENR, PERSONNR) VALUES (1, '01234567890')")
        jdbcTemplate.update(
            """INSERT INTO T_VEDTAK (VEDTAK_ID, PERSON_LOPENR, STONAD_ID, KODE_RUTINE, DATO_INNV_FOM,
                     DATO_INNV_TOM, BRUKERID) VALUES (1,1,1,'EO',?,?, 'NISSEN')""",
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2022, 1, 1)
        )
        jdbcTemplate.update(
            """INSERT INTO T_STONAD (STONAD_ID, OPPDRAG_ID, PERSON_LOPENR, DATO_START, DATO_OPPHOR)
                                     VALUES (1, 1, 1, sysdate, null)"""
        )
        jdbcTemplate.update("INSERT INTO T_DELYTELSE (VEDTAK_ID, TYPE_SATS, BELOP) VALUES (1, '', 100.34)")
        jdbcTemplate.update("INSERT INTO T_ENDRING (VEDTAK_ID, KODE) VALUES (1, 'F ')")
        jdbcTemplate.update("INSERT INTO T_EF (VEDTAK_ID, STONAD_BELOP, INNT_FRADRAG, NETTO_BELOP, SAM_FRADRAG) VALUES (1,1,1,1,1)")
    }

    @After
    fun tearDown() {
        listOf("T_LOPENR_FNR", "T_VEDTAK", "T_STONAD", "T_DELYTELSE", "T_ENDRING", "T_EF").forEach {
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
        assertThat(hentPerioderForArena(LocalDate.of(1900, 1, 1), LocalDate.of(2021, 1, 1)))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioderForArena(LocalDate.of(1900, 1, 1), LocalDate.of(2030, 1, 1)))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioderForArena(LocalDate.of(2020, 6, 1), LocalDate.of(2020, 6, 1)))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioderForArena(LocalDate.of(2020, 6, 1), LocalDate.of(2030, 1, 1)))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioderForArena(LocalDate.of(1900, 1, 1), LocalDate.of(1901, 1, 1)))
            .withFailMessage("TOM dato < FOM dato(db)")
            .isEmpty()

        assertThat(hentPerioderForArena(LocalDate.of(2030, 1, 1), LocalDate.of(2031, 1, 1)))
            .withFailMessage("FOM dato > TOM dato(db)")
            .isEmpty()
    }

    @Test
    fun `uten tom dato`() {
        assertThat(hentPerioderForArena(LocalDate.of(1900, 1, 1), null))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioderForArena(LocalDate.of(2020, 6, 1), null))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioderForArena(LocalDate.of(2030, 1, 1), null))
            .withFailMessage("FOM dato > TOM dato(db)")
            .isEmpty()
    }

    @Test
    fun `uten fom dato`() {
        assertThat(hentPerioderForArena(null, LocalDate.of(1901, 1, 1)))
            .withFailMessage("TOM dato < FOM dato(db)")
            .isEmpty()

        assertThat(hentPerioderForArena(null, LocalDate.of(2020, 6, 1)))
            .withFailMessage("FOM dato < TOM dato(db)")
            .hasSize(1)

        assertThat(hentPerioderForArena(null, LocalDate.of(2031, 1, 1)))
            .withFailMessage("FOM dato > TOM dato(db)")
            .hasSize(1)
    }

    private fun hentPerioderForArena(fomDato: LocalDate? = null, tomDato: LocalDate? = null): List<ArenaPeriode> =
        periodeRepository.hentPerioderForArena(
            PeriodeArenaRequest(
                        personIdenter = setOf(FoedselsNr("01234567890")),
                        fomDato,
                        tomDato
            )
        )
}