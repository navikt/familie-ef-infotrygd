package no.nav.familie.ef.infotrygd.model

import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Suppress("unused") // brukes av hibernate for å generere hvilke tabeller som brukes
@Entity
@Table(name = "T_VEDTAK")
data class Vedtak(
    @Id
    @Column(name = "VEDTAK_ID")
    val id: Long,
    @Column(name = "STONAD_ID")
    val stønadId: Long,
    @Column(name = "KODE_RUTINE")
    val kodeRutine: String,
    @Column(name = "TYPE_SAK")
    val sakstype: String,
    @Column(name = "KODE_RESULTAT")
    val kodeResultat: String,
    @Column(name = "BRUKERID")
    val brukerId: String,
    @Column(name = "tidspunkt_reg")
    val tidspunktReg: LocalDateTime,
    @Column(name = "DATO_INNV_FOM")
    val datoFom: LocalDate,
    @Column(name = "DATO_INNV_TOM")
    val datoTom: LocalDate,
)
