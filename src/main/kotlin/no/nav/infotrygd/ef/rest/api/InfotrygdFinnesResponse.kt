package no.nav.infotrygd.ef.rest.api

import no.nav.infotrygd.ef.model.StønadType

data class InfotrygdFinnesResponse(val vedtak: List<Vedtakstreff>,
                                   val saker: List<Saktreff>)

data class Vedtakstreff(val personIdent: String, val stønadType: StønadType, val harLøpendeVedtak: Boolean)
data class Saktreff(val personIdent: String, val stønadType: StønadType)
