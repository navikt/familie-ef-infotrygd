package no.nav.infotrygd.ef.rest.api

import no.nav.infotrygd.ef.model.StønadType

data class FinnesResponse(val stønader: Map<StønadType, StønadTreff>)

data class StønadTreff(val finnes: Boolean,
                       val harAktivStønad: Boolean)