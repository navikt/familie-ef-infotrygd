package no.nav.infotrygd.ef.rest.api

import no.nav.commons.foedselsnummer.FoedselsNr
import java.time.LocalDate

data class OvergangsstønadPeriodeRequest(val identer: Set<FoedselsNr>,
                                         val fomDato: LocalDate?,
                                         val tomDato: LocalDate?)

data class OvergangsstønadPeriodeResponse(val perioder: List<OvergangsstønadPeriode>)

//TODO hvordan beregne fullOvergangsstønad?
/**
 * Det er full overgangsstønad hvis månedsbeløpet som utbetales =  (Grunnbeløp * 2,25) / 12 + 0,5....
 * Vi beregner dette ut fra verdier vi har for Grunnbeløp og EF-faktor (pr nå 2.25) i tabeller i Infotrygd.
 */
data class OvergangsstønadPeriode(val ident: FoedselsNr,
                                  val fomDato: LocalDate,
                                  val tomDato: LocalDate,
                                  val fullOvergangsstonad: Boolean) {

    @Suppress("unused") // brukes av json
    val datakilde = Datakilde.INFOTRYGD
}

enum class Datakilde {
    INFOTRYGD
}