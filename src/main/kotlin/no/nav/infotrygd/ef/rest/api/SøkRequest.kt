package no.nav.infotrygd.ef.rest.api

import no.nav.commons.foedselsnummer.FoedselsNr

data class InfotrygdSøkRequest(val identer: List<FoedselsNr>,
                               val stønad: StønadType)

enum class StønadType {
    OVERGANGSSTØNAD,
    BARNETILSYN,
    SKOLEPENGER
}