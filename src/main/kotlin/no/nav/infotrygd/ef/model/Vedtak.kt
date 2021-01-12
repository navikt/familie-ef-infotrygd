package no.nav.infotrygd.ef.model

import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Suppress("unused") // brukes av hibernate for å generere hvilke tabeller som brukes
@Entity
@Table(name = "T_VEDTAK")
data class Vedtak(@Id
                  @Column(name = "VEDTAK_ID")
                  val id: Long,

                  @Column(name = "STONAD_ID")
                  val stønadId: Long,

                  @Column(name = "TKNR")
                  val tknr: String,

                  @Column(name = "KODE_RUTINE")
                  val kodeRutine: String,

                  @Column(name = "DATO_INNV_FOM")
                  val datoFom: LocalDate,

                  @Column(name = "DATO_INNV_TOM")
                  val datoTom: LocalDate)