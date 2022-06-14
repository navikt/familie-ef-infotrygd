package no.nav.familie.ef.infotrygd.service

import no.nav.familie.ef.infotrygd.model.StønadType
import no.nav.familie.ef.infotrygd.model.StønadType.BARNETILSYN
import no.nav.familie.ef.infotrygd.repository.PeriodeRepository
import no.nav.familie.ef.infotrygd.rest.api.Periode
import no.nav.familie.ef.infotrygd.rest.api.PeriodeRequest
import org.springframework.stereotype.Service

@Service
class PeriodeService(private val periodeRepository: PeriodeRepository) {

    fun hentPerioder(request: PeriodeRequest): Map<StønadType, List<Periode>> {
        val perioder = periodeRepository.hentPerioder(request).groupBy({ it.first }) { it.second }.toMutableMap()
        perioder[BARNETILSYN] = hentBarnetilsynPerioderMedBarn(perioder)
        return perioder
    }

    private fun hentBarnetilsynPerioderMedBarn(perioder: Map<StønadType, List<Periode>>): List<Periode> {
        val barnetilsynPerioder = perioder.getOrDefault(BARNETILSYN, emptyList())
        val barnetilsynPeriodeBarnListe = periodeRepository.hentBarnForPerioder(barnetilsynPerioder)

        barnetilsynPerioder.groupBy { it.stønadId }
            .values
            .map { perioder ->
                perioder.sortedBy { it.vedtakId }
                    .fold(emptyList<Periode>()) { acc, periode ->
                        var barnIdenter = barnIdenterForVedtakEllerForForrigeVedtakHvisManglerBarnIdenter(acc, periode, barnetilsynPeriodeBarnListe)
                        acc + periode.copy(barnIdenter = barnIdenter)
                    }
            }

        return barnetilsynPerioder.map {
            it.copy(barnIdenter = barnetilsynPeriodeBarnListe[it.vedtakId] ?: emptyList())
        }
    }

    private fun barnIdenterForVedtakEllerForForrigeVedtakHvisManglerBarnIdenter(
        acc: List<Periode>,
        periode: Periode,
        barnetilsynPeriodeBarnListe: Map<Long, List<String>>
    ): List<String> {
        val barnIdenter = barnetilsynPeriodeBarnListe[periode.vedtakId] ?: emptyList()
        val last = acc.lastOrNull()
        if (barnIdenter.isEmpty() && last != null && last.barnIdenter.isNotEmpty()) {
            return last.barnIdenter
        }
        return barnIdenter
    }
}
