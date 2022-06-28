package no.nav.familie.ef.infotrygd.service

import io.mockk.every
import io.mockk.mockk
import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.familie.ef.infotrygd.model.StønadType.BARNETILSYN
import no.nav.familie.ef.infotrygd.model.StønadType.OVERGANGSSTØNAD
import no.nav.familie.ef.infotrygd.repository.PeriodeRepository
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdEndringKode
import no.nav.familie.ef.infotrygd.rest.api.InfotrygdSakstype
import no.nav.familie.ef.infotrygd.rest.api.Periode
import no.nav.familie.ef.infotrygd.rest.api.PeriodeRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

internal class PeriodeServiceTest {

    val periodeRepository = mockk<PeriodeRepository>()
    val periodeService: PeriodeService = PeriodeService(periodeRepository)

    @Test
    fun `Skal legge til barn på barnetilsyn - andre skal være uendret`() {
        val request = PeriodeRequest(
            personIdenter = setOf(FoedselsNr("01015450572")),
            stønadstyper = setOf(BARNETILSYN, OVERGANGSSTØNAD)
        )
        val uendretPeriode = lagPeriode()
        every { periodeRepository.hentPerioder(any()) } returns listOf(
            Pair(BARNETILSYN, lagPeriode(vedtakId = 35L)),
            Pair(OVERGANGSSTØNAD, uendretPeriode)
        )
        every { periodeRepository.hentBarnForPerioder(any()) } returns mapOf(35L to listOf("123"))

        val perioder = periodeService.hentPerioder(request = request)
        assertThat(perioder[OVERGANGSSTØNAD]!!.first().barnIdenter).isEmpty()
        assertThat(uendretPeriode).isEqualTo(perioder[OVERGANGSSTØNAD]!!.first())
        assertThat(perioder[BARNETILSYN]!!.first().barnIdenter.first()).isEqualTo("123")
    }

    @Test
    fun `Ingen barnetilsynbarn funnet - skal ikke feile hvis det ikke finnes barn`() {
        val request = PeriodeRequest(
            personIdenter = setOf(FoedselsNr("01015450572")),
            stønadstyper = setOf(BARNETILSYN, OVERGANGSSTØNAD)
        )
        val uendretPeriode = lagPeriode()
        every { periodeRepository.hentPerioder(any()) } returns listOf(
            Pair(BARNETILSYN, lagPeriode(vedtakId = 35L)),
            Pair(OVERGANGSSTØNAD, uendretPeriode)
        )
        every { periodeRepository.hentBarnForPerioder(any()) } returns emptyMap()

        val perioder = periodeService.hentPerioder(request = request)
        val barnetilsynPerioderHentet = perioder[BARNETILSYN]!!.first()
        assertThat(perioder[OVERGANGSSTØNAD]!!.first().barnIdenter).isEmpty()
        assertThat(barnetilsynPerioderHentet.barnIdenter).isEmpty()
    }

    @Test
    fun `Ingen barnetilsynbarn funnet - skal bruke forrige periode sine barn hvis det finnes`() {
        val request = PeriodeRequest(
            personIdenter = setOf(FoedselsNr("01015450572")),
            stønadstyper = setOf(BARNETILSYN, OVERGANGSSTØNAD)
        )
        val uendretPeriode = lagPeriode()
        every { periodeRepository.hentPerioder(any()) } returns listOf(
            Pair(BARNETILSYN, lagPeriode(vedtakId = 35L, "FI")),
            Pair(BARNETILSYN, lagPeriode(vedtakId = 34L)),
            Pair(OVERGANGSSTØNAD, uendretPeriode)
        )
        every { periodeRepository.hentBarnForPerioder(any()) } returns mapOf(34L to listOf("123"))

        val perioder = periodeService.hentPerioder(request = request)
        val barnetilsynPerioderHentet = perioder[BARNETILSYN]!!.associateBy { it.vedtakId }
        assertThat(barnetilsynPerioderHentet[34L]!!.barnIdenter).isNotEmpty
        assertThat(barnetilsynPerioderHentet[35L]!!.barnIdenter).isNotEmpty
    }

    private fun lagPeriode(vedtakId: Long = 1, vedtakKodeResultat: String = "I") = Periode(
        personIdent = "123",
        sakstype = InfotrygdSakstype.SØKNAD,
        kode = InfotrygdEndringKode.FØRSTEGANGSVEDTAK,
        kodeOvergangsstønad = null,
        aktivitetstype = null,
        brukerId = "",
        stønadId = 0,
        vedtakId = vedtakId,
        vedtakstidspunkt = LocalDateTime.MIN,
        engangsbeløp = 0,
        inntektsgrunnlag = 0,
        inntektsreduksjon = 0,
        samordningsfradrag = 0,
        utgifterBarnetilsyn = 0,
        månedsbeløp = 0,
        startDato = LocalDate.MIN,
        stønadFom = LocalDate.MIN,
        stønadTom = LocalDate.MAX,
        opphørsdato = null,
        barnIdenter = emptyList(),
        vedtakKodeResultat = vedtakKodeResultat
    )
}
