package no.nav.familie.ef.infotrygd.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Suppress("unused") // brukes av hibernate for å generere hvilke tabeller som brukes
@Entity
@Table(name = "T_BEREGN_GRL")
data class BeregningGrunnlag(
    @Id
    @Column(name = "VEDTAK_ID")
    val id: Long,

    @Column(name = "BELOP")
    val beløp: Long,

    @Column(name = "TYPE_BELOP")
    val type: String
)
