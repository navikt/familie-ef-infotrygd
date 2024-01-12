package no.nav.familie.ef.infotrygd.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Suppress("unused") // brukes av hibernate for å generere hvilke tabeller som brukes
@Entity
@Table(name = "T_ENDRING")
data class Endring(
    @Id
    @Column(name = "VEDTAK_ID")
    val id: Long,
    @Column(name = "KODE")
    val kode: String,
)
