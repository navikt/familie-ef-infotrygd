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

    @Autowired
    lateinit var periodeRepository: PeriodeRepository
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    private val startdato = LocalDate.now().minusYears(1)
    private val sluttdato = LocalDate.now().plusYears(1)
    private val førStartdato = startdato.minusYears(1)
    private val etterSluttdato = sluttdato.plusYears(1)

    @Before
    fun setUp() {
        lagVedtak(stønadType = "EO", vedtakId = 1, stønadId = 1)
        jdbcTemplate.update("INSERT INTO t_beregn_grl (vedtak_id, type_belop, fom, belop, brukerid) VALUES (1,'ARBM',CURRENT_DATE, 100, 'A')")
        jdbcTemplate.update("INSERT INTO t_beregn_grl (vedtak_id, type_belop, fom, belop, brukerid) VALUES (1,'ABCD',CURRENT_DATE, 50, 'A')")
    }

    @After
    fun tearDown() {
        listOf(
            "T_ROLLE",
            "T_LOPENR_FNR",
            "T_VEDTAK",
            "T_STONAD",
            "T_DELYTELSE",
            "T_ENDRING",
            "T_EF",
            "T_BEREGN_GRL"
        ).forEach {
            jdbcTemplate.update("TRUNCATE TABLE $it")
        }
    }

    @Test
    fun `hent tom liste barnetilsynbarn gitt ingen barnetilsynperioder`() {
        val barn = periodeRepository.hentBarnForPerioder(emptyList())
        assertThat(barn).isEmpty()
    }

    @Test
    fun `hent barnetilsyn-barn gitt barnetilsynperioder som finnes`() {
        insertBarnetilsynsak()

        val perioder = periodeRepository.hentPerioder(
            PeriodeRequest(
                setOf(FoedselsNr("01234567890")),
                setOf(StønadType.BARNETILSYN)
            )
        )
            .groupBy({ it.first }) { it.second }
            .toMutableMap()
        assertThat(perioder).isNotEmpty
        val barn = periodeRepository.hentBarnForPerioder(perioder.getOrDefault(StønadType.BARNETILSYN, emptyList()))
        assertThat(barn.get(2)).containsExactly("01234567891", "01234567892")
    }

    @Test
    fun `hentPersonerForMigrering går fint`() {
        periodeRepository.hentPersonerForMigrering(10)
    }

    @Test
    fun `skal hente perioder`() {
        val perioder = periodeRepository.hentPerioder(
            PeriodeRequest(
                setOf(FoedselsNr("01234567890")),
                setOf(StønadType.OVERGANGSSTØNAD)
            )
        )
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
        assertThat(periode.inntektsgrunnlag).isEqualTo(100)
        assertThat(periode.sakstype).isEqualTo(InfotrygdSakstype.SØKNAD)
    }

    @Test
    fun `henting av perioder uten T_BEREGN_GRL gir 0 i inntektsgrunnlag`() {
        jdbcTemplate.update("TRUNCATE TABLE t_beregn_grl")
        val perioder = hentPerioder()
        assertThat(perioder).hasSize(1)
        assertThat(perioder.first().second.inntektsgrunnlag).isEqualTo(0)
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

    private fun lagVedtak(stønadType: String, vedtakId: Int, stønadId: Int) {

        jdbcTemplate.update("INSERT INTO t_lopenr_fnr (person_lopenr, personnr) VALUES (1,  '01234567890')")
        jdbcTemplate.update(
            """INSERT INTO t_vedtak (vedtak_id, person_lopenr, stonad_id, kode_rutine, kode_resultat, 
                dato_innv_fom, dato_innv_tom, brukerid, type_sak, tidspunkt_reg)
                          VALUES (?,1,?,?,'I',?,?, 'NISSEN', 'S ', CURRENT_TIMESTAMP)""",
            vedtakId,
            stønadId,
            stønadType,
            startdato,
            sluttdato
        )
        jdbcTemplate.update(
            """INSERT INTO t_stonad (stonad_id, oppdrag_id, person_lopenr, dato_start, dato_opphor)
                                         VALUES (?, 1, 1, sysdate, NULL)""",
            stønadId
        )
        jdbcTemplate.update("INSERT INTO t_delytelse (vedtak_id, type_sats, belop) VALUES (?, '', 100.34)", vedtakId)
        jdbcTemplate.update("INSERT INTO t_endring (vedtak_id, kode) VALUES (?, 'F ')", vedtakId)
        jdbcTemplate.update(
            "INSERT INTO t_ef (vedtak_id, stonad_belop, innt_fradrag, netto_belop, sam_fradrag, kode_overg, aktivitet, barnt_utg)" +
                " VALUES (?,1,1,1,1,' ',' ', 1)",
            vedtakId
        )
    }

    private fun insertBarnetilsynsak() {
        // Lag barnetilsynvedtak
        lagVedtak(stønadType = "EB", vedtakId = 2, stønadId = 2)
        // legg til barn
        jdbcTemplate.update("INSERT INTO t_lopenr_fnr (person_lopenr, personnr) VALUES (2, '01234567891')")
        jdbcTemplate.update("INSERT INTO t_lopenr_fnr (person_lopenr, personnr) VALUES (3, '01234567892')")

        // barnetilsynbarn 1 på barnetilsynvedtak
        jdbcTemplate.update(
            "INSERT INTO t_rolle (vedtak_id,type,tidspunkt_reg,fom,tom,person_lopenr_r,brukerid,barn_type,bor_sammen_med,trygdetid_faktisk,trygdetid_anvendt,trygdetid_unntak,trygd_medlem_siden,utenlandsopphold,bt_1_sum,bt_1_antall,bt_2_sum,bt_2_antall,bt_s_sum,bt_s_antall,opprettet,oppdatert) " +
                "VALUES (2,'EB',CURRENT_TIMESTAMP, CURRENT_DATE,CURRENT_DATE,2,'MIA4408',  NULL, NULL, NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL, CURRENT_DATE,CURRENT_DATE)"
        )

        // barnetilsynbarn 2 på barnetilsynvedtak
        jdbcTemplate.update(
            "INSERT INTO t_rolle (vedtak_id,type,tidspunkt_reg,fom,tom,person_lopenr_r,brukerid,barn_type,bor_sammen_med,trygdetid_faktisk,trygdetid_anvendt,trygdetid_unntak,trygd_medlem_siden,utenlandsopphold,bt_1_sum,bt_1_antall,bt_2_sum,bt_2_antall,bt_s_sum,bt_s_antall,opprettet,oppdatert) " +
                "VALUES (2,'EB',CURRENT_TIMESTAMP, CURRENT_DATE,CURRENT_DATE,3,'MIA4409',  NULL, NULL, NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL, CURRENT_DATE,CURRENT_DATE)"
        )
    }
}
