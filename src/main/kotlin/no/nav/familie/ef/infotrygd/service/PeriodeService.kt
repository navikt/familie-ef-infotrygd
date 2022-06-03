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
        return barnetilsynPerioder.map {
            it.copy(barnIdenter = barnetilsynPeriodeBarnListe.get(it.vedtakId) ?: emptyList())
        }
    }
}