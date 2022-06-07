package no.nav.familie.ef.infotrygd

private var current: Long = 1

fun nextId(): Long = current++
