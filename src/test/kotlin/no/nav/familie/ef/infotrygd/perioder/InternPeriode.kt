package no.nav.familie.ef.infotrygd.perioder

import no.nav.familie.ef.infotrygd.rest.api.Periode
import no.nav.familie.kontrakter.felles.ef.PeriodeOvergangsstønad
import java.time.LocalDate

/**
 * Brukes for å mappe interne ef-perioder og infotrygd perioder til ett felles format
 */
data class InternPeriode(
    val personIdent: String,
    val inntektsreduksjon: Int,
    val samordningsfradrag: Int,
    val utgifterBarnetilsyn: Int,
    val månedsbeløp: Int,
    val engangsbeløp: Int,
    val stønadFom: LocalDate,
    val stønadTom: LocalDate,
    val opphørsdato: LocalDate?,
    val datakilde: PeriodeOvergangsstønad.Datakilde
)

fun Periode.tilInternPeriode(): InternPeriode = InternPeriode(
    personIdent = this.personIdent,
    inntektsreduksjon = this.inntektsreduksjon,
    samordningsfradrag = this.samordningsfradrag,
    utgifterBarnetilsyn = this.utgifterBarnetilsyn,
    månedsbeløp = this.månedsbeløp,
    engangsbeløp = this.engangsbeløp,
    stønadFom = this.stønadFom,
    stønadTom = this.stønadTom,
    opphørsdato = this.opphørsdato,
    datakilde = PeriodeOvergangsstønad.Datakilde.INFOTRYGD
)
