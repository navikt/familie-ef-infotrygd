package no.nav.infotrygd.ef.rest.api

import no.nav.commons.foedselsnummer.FoedselsNr
import java.time.LocalDate

data class PeriodeOvergangsstønadRequest(val personIdenter: Set<FoedselsNr>,
                                         val fomDato: LocalDate?,
                                         val tomDato: LocalDate?)

data class PeriodeOvergangsstønadResponse(val perioder: List<PeriodeOvergangsstønad>)

data class PeriodeOvergangsstønad(val personIdent: FoedselsNr,
                                  val fomDato: LocalDate,
                                  val tomDato: LocalDate,
                                  val opphørDato: LocalDate?,
                                  val beløp: Float)