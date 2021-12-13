package no.nav.familie.ef.infotrygd.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Suppress("unused") // brukes av hibernate for å generere hvilke tabeller som brukes
@Entity
@Table(name = "SA_SAK_10")
data class SaSak(@Id // Egentlige er ID_SAK id men kolonnen brukes ikke i noen query
                 @Column(name = "F_NR")
                 val fnr: String,

                 @Column(name = "S10_KAPITTELNR")
                 val kapittelNr: String,

                 @Column(name = "S10_VALG")
                 val valg: String)