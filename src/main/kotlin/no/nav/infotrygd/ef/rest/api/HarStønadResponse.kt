package no.nav.infotrygd.ef.rest.api

import no.nav.infotrygd.ef.model.StønadType

data class HarStønadResponse(val stønader: Map<StønadType, StønadTreff>)

data class StønadTreff(val harStønad: Boolean,
                       val harAktivStønad: Boolean)