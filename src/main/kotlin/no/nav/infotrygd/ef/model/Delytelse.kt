package no.nav.infotrygd.ef.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Suppress("unused") // brukes av hibernate for å generere hvilke tabeller som brukes
@Entity
@Table(name = "T_DELYTELSE")
data class Delytelse(@Id
                     @Column(name = "VEDTAK_ID")
                     val id: Long,

                     @Column(name = "TYPE_SATS")
                     val typeSats: String,

                     @Column(name = "BELOP")
                     val beløp: Float)