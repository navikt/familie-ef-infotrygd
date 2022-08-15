package no.nav.familie.ef.infotrygd.service

import no.nav.familie.ef.infotrygd.model.StønadType
import no.nav.familie.ef.infotrygd.model.StønadType.BARNETILSYN
import no.nav.familie.ef.infotrygd.repository.PeriodeRepository
import no.nav.familie.ef.infotrygd.rest.api.Periode
import no.nav.familie.ef.infotrygd.rest.api.PeriodeRequest
import no.nav.familie.ef.infotrygd.utils.InfotrygdPeriodeUtil
import org.springframework.stereotype.Service

@Service
class PeriodeService(private val periodeRepository: PeriodeRepository) {

    fun hentPerioder(request: PeriodeRequest): Map<StønadType, List<Periode>> {
        val perioder =
            periodeRepository.hentPerioder(request).groupBy({ it.first }) { it.second }
                .toMutableMap()
        perioder[BARNETILSYN] = hentBarnetilsynPerioderMedBarn(perioder)
        return perioder.map { it.key to it.value.sortedByDescending { it.stønadFom } }.toMap()
    }

    fun hentSammenslåttePerioder(request: PeriodeRequest): Map<StønadType, List<Periode>> {
        return hentPerioder(request).map { it.key to slåSammenPerioder(it.value) }.toMap()
    }

    private fun hentBarnetilsynPerioderMedBarn(perioder: Map<StønadType, List<Periode>>): List<Periode> {
        val barnetilsynPerioder = perioder.getOrDefault(BARNETILSYN, emptyList())
        val barnetilsynPeriodeBarnListe = periodeRepository.hentBarnForPerioder(barnetilsynPerioder)

        return barnetilsynPerioder.groupBy { it.stønadId }
            .values
            .flatMap { perioder ->
                perioder.sortedBy { it.vedtakId }
                    .fold(emptyList()) { acc, periode ->
                        val barnIdenter = hentBarnIdenter(acc, periode, barnetilsynPeriodeBarnListe)
                        acc + periode.copy(barnIdenter = barnIdenter)
                    }
            }
    }

    private fun hentBarnIdenter(
        acc: List<Periode>,
        periode: Periode,
        barnetilsynPeriodeBarnListe: Map<Long, List<String>>
    ): List<String> {
        val barnIdenter = barnetilsynPeriodeBarnListe[periode.vedtakId] ?: emptyList()
        if (barnIdenter.isNotEmpty()) return barnIdenter

        return brukBarnIdenterFraForrigeVedtak(acc, periode, barnIdenter)
    }

    /**
     * 49 vedtak finnes med kode_resultat = 'FI', som betyder at stønaden fortsatt er innvilget,
     * men der barnen har blitt fjernet. I de tilfellene bruker vi barnen fra forrige vedtak
     */
    private fun brukBarnIdenterFraForrigeVedtak(
        acc: List<Periode>,
        periode: Periode,
        barnIdenter: List<String>
    ): List<String> {
        val last = acc.lastOrNull()
        return if (periode.erFortsattInnvilget() && barnIdenter.isEmpty() &&
            last != null && last.barnIdenter.isNotEmpty()
        ) {
            last.barnIdenter
        } else {
            emptyList()
        }
    }

    private fun slåSammenPerioder(perioder: List<Periode>): List<Periode> {
        return InfotrygdPeriodeUtil.slåSammenInfotrygdperioder(perioder)
    }
}
