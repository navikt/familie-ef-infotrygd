package no.nav.infotrygd.ef

private var current: Long = 1

fun nextId(): Long = current++