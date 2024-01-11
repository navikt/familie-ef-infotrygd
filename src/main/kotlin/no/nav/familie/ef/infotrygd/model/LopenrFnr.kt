package no.nav.familie.ef.infotrygd.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Suppress("unused") // brukes av hibernate for å generere hvilke tabeller som brukes
@Entity
@Table(name = "T_LOPENR_FNR")
data class LopenrFnr(
    @Id
    @Column(name = "PERSON_LOPENR")
    val personKey: Long,
    @Column(name = "PERSONNR")
    val fnr: String,
)
